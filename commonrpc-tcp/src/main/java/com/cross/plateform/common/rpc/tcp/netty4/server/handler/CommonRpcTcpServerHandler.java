/**
 * 
 */
package com.cross.plateform.common.rpc.tcp.netty4.server.handler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cross.plateform.common.rpc.core.all.message.CommonRpcRequest;
import com.cross.plateform.common.rpc.core.all.message.CommonRpcResponse;
import com.cross.plateform.common.rpc.core.server.handler.factory.CommonRpcServerHandlerFactory;
import com.cross.plateform.common.rpc.core.util.StringUtils;
import com.cross.plateform.common.rpc.service.server.service.CommonRpcServerService;
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
	
	public CommonRpcTcpServerHandler(int timeout, int port, String token,
			int procotolType, int codecType) {
		super();
		this.timeout = timeout;
		this.port = port;
		this.token = token;
		this.procotolType = procotolType;
		this.codecType = codecType;
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
		
		handleRequestWithSingleThread(ctx, msg);
		
	}
	/**
	 * disruptor处理方式
	 * @param ctx
	 * @param message
	 */
	private void handleRequestWithSingleThread( final ChannelHandlerContext ctx,  Object message){
		CommonRpcResponse rocketRPCResponse = null;
		try{
			CommonRpcRequest request = (CommonRpcRequest) message;
			if (!StringUtils.isNullOrEmpty(request.getToken())&&!new String(request.getToken()).equals(this.token)) {
					LOGGER.error("client token is wrong");
					rocketRPCResponse = new CommonRpcResponse(request.getId(),
							codecType, procotolType);
					rocketRPCResponse.setException(new Exception(
							"client token is wrong"));
			}else{
					rocketRPCResponse = CommonRpcServerHandlerFactory
							.getServerHandler().handleRequest(request, codecType,
									procotolType);
			}
			
			if(ctx.channel().isOpen()){
				ChannelFuture wf = ctx.channel().writeAndFlush(rocketRPCResponse);
			    wf.addListener(new ChannelFutureListener() {
			      public void operationComplete(ChannelFuture future) throws Exception {
			        if (!future.isSuccess()) {
			          LOGGER.error("server write response error,client  host is: " + ((InetSocketAddress) ctx.channel().remoteAddress()).getHostName()+":"+((InetSocketAddress) ctx.channel().remoteAddress()).getPort()+",server Ip:"+getLocalhost());
			          ctx.channel().close();
			        }
			      }
			    });
			}
			
		}catch(Exception e){
			e.printStackTrace();
			sendErrorResponse(ctx, (CommonRpcRequest) message,e.getMessage()+",server Ip:"+getLocalhost());
		}finally{
			ReferenceCountUtil.release(message);
		}
	}
	
	
	private void sendErrorResponse(final ChannelHandlerContext ctx, final CommonRpcRequest request,String errorMessage) {
	    CommonRpcResponse commonRpcResponse =
	        new CommonRpcResponse(request.getId(), request.getCodecType(), request.getProtocolType());
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
