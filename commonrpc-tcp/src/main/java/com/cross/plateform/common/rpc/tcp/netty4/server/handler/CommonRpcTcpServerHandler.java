/**
 * 
 */
package com.cross.plateform.common.rpc.tcp.netty4.server.handler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cross.plateform.common.rpc.core.all.message.CommonRpcRequest;
import com.cross.plateform.common.rpc.core.all.message.CommonRpcResponse;
import com.cross.plateform.common.rpc.core.disruptor.RpcProducer;
import com.cross.plateform.common.rpc.core.thread.CommonRpcTaskExecutors;
import com.cross.plateform.common.rpc.service.server.service.CommonRpcServerService;
import com.cross.plateform.common.rpc.tcp.netty4.server.thread.CommonRpcServerFuture;
import com.cross.plateform.common.rpc.tcp.netty4.server.thread.CommonRpcTcpEventHandler;
import com.cross.plateform.common.rpc.tcp.netty4.server.thread.RocketRPCServerTask;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
/**
 * @author liubing1
 *
 */
public class CommonRpcTcpServerHandler extends ChannelInboundHandlerAdapter {
	
	private static final Log LOGGER = LogFactory
			.getLog(CommonRpcTcpServerHandler.class);
	
	private int timeout;
	
	private int port;
	
	private String token;
	
	private int procotolType;//协议名称
	
	private int codecType;//编码类型
	/**
	 * 1:java 多线程
	 * 2:disruptor
	 */
	private int handleType;//服务端处理方式
	
	public CommonRpcTcpServerHandler(int timeout, int port, String token,
			int procotolType, int codecType, int handleType) {
		super();
		this.timeout = timeout;
		this.port = port;
		this.token = token;
		this.procotolType = procotolType;
		this.codecType = codecType;
		this.handleType = handleType;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		CommonRpcServerService.getInstance().registerClient(getLocalhost(), ctx.channel().remoteAddress().toString());
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		ctx.channel().close();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable e)
		      throws Exception {
		    if (!(e.getCause() instanceof IOException)) {
		      // only log
		      LOGGER.error("catch some exception not IOException", e);
		    }
		    ctx.channel().close();
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		// TODO Auto-generated method stub
		if (!(msg instanceof CommonRpcRequest) ) {
		      LOGGER.error("receive message error,only support RequestWrapper");
		      throw new Exception(
		          "receive message error,only support RequestWrapper || List");
		}
		if(handleType==1||handleType==0){
			handleRequest(ctx, msg);
		}else if(handleType==2){
			handleRequestWithDisruptor(ctx, msg);
		}
		
	}
	/**
	 * disruptor处理方式
	 * @param ctx
	 * @param message
	 */
	private void handleRequestWithDisruptor( ChannelHandlerContext ctx,  Object message){
		
		RpcProducer.getInstance().publish(timeout, new CommonRpcTcpEventHandler(token, procotolType, codecType, port), message,ctx);
	}
	/**
	 * java 多线程处理
	 * @param ctx
	 * @param message
	 */
	private void handleRequest(final ChannelHandlerContext ctx, final Object message) {
	    try {
	    	ListeningExecutorService service = CommonRpcTaskExecutors.getInstance()
					.getService();
	    	ListenableFuture<CommonRpcResponse> future=service.submit(new RocketRPCServerTask(message,token,procotolType,codecType));
	    	long begintime=System.currentTimeMillis();
	    	try {
				if(future.get(timeout, TimeUnit.MILLISECONDS)!=null){
					Futures.addCallback(future, new CommonRpcServerFuture(ctx));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				long endtime=System.currentTimeMillis();
				sendErrorResponse(ctx, (CommonRpcRequest) message,"the server operate CommonRpcRequest timeout,timeout is:"+timeout+",but really cost time is:"+(endtime-begintime)+",client Ip is:"+ctx.channel().remoteAddress().toString()+",server Ip:"+getLocalhost());
			}
	    	
	    } catch (RejectedExecutionException exception) {
	        
	        sendErrorResponse(ctx, (CommonRpcRequest) message,"server threadpool full,maybe because server is slow or too many requests"+",server Ip:"+getLocalhost());
	    }finally{
			ReferenceCountUtil.release(message);
		}
	}
	
	private void sendErrorResponse(final ChannelHandlerContext ctx, final CommonRpcRequest request,String errorMessage) {
	    CommonRpcResponse commonRpcResponse =
	        new CommonRpcResponse(request.getId(), request.getCodecType(), request.getProtocolType());
	    //commonRpcResponse.setException(new Exception("server threadpool full,maybe because server is slow or too many requests"));
	    commonRpcResponse.setException(new Exception(errorMessage));
	    ChannelFuture wf = ctx.channel().writeAndFlush(commonRpcResponse);
	    wf.addListener(new ChannelFutureListener() {
	      public void operationComplete(ChannelFuture future) throws Exception {
	        if (!future.isSuccess()) {
	          LOGGER.error("server write response error,request id is: " + request.getId()+",client Ip is:"+ctx.channel().remoteAddress().toString()+",server Ip:"+getLocalhost());
	          ctx.channel().close();
	        }
	      }
	    });
	  }
	
	private String getLocalhost(){
		try {
			String ip = InetAddress.getLocalHost().getHostAddress();
			return ip+":"+port;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("无法获取本地Ip",e);
		}
		
	}
}
