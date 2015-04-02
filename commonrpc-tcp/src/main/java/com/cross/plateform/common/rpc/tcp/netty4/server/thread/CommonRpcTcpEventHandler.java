/**
 * 
 */
package com.cross.plateform.common.rpc.tcp.netty4.server.thread;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import com.cross.plateform.common.rpc.core.all.message.CommonRpcRequest;
import com.cross.plateform.common.rpc.core.all.message.CommonRpcResponse;
import com.cross.plateform.common.rpc.core.disruptor.RpcValueEvent;
import com.cross.plateform.common.rpc.core.server.handler.factory.CommonRpcServerHandlerFactory;
import com.cross.plateform.common.rpc.core.util.StringUtils;
import com.lmax.disruptor.EventHandler;

/**
 * @author liubing
 *
 */
public class CommonRpcTcpEventHandler implements EventHandler<RpcValueEvent> {

	private String token;// token

	private int procotolType;// 协议名称

	private int codecType;// 编码类型
	
	private int port;
	
	private static final Log LOGGER = LogFactory
			.getLog(RocketRPCServerTask.class);
	
	public CommonRpcTcpEventHandler(String token, int procotolType,
			int codecType, int port) {
		super();
		this.token = token;
		this.procotolType = procotolType;
		this.codecType = codecType;
		this.port = port;
	}

	@Override
	public void onEvent(RpcValueEvent event, long sequence, boolean endOfBatch)
			throws Exception {
		// TODO Auto-generated method stub
		CommonRpcResponse rocketRPCResponse = null;
		final ChannelHandlerContext ctx=(ChannelHandlerContext) event.getCtx();
		try{
			CommonRpcRequest request = (CommonRpcRequest) event.getValue();
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
			sendErrorResponse(ctx, (CommonRpcRequest) event.getValue(),e.getMessage()+",server Ip:"+getLocalhost());
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
