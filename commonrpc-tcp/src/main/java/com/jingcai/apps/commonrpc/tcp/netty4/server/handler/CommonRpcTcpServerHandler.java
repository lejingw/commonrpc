package com.jingcai.apps.commonrpc.tcp.netty4.server.handler;

import com.jingcai.apps.commonrpc.core.all.message.CommonRpcRequest;
import com.jingcai.apps.commonrpc.core.all.message.CommonRpcResponse;
import com.jingcai.apps.commonrpc.tcp.server.handler.impl.RpcTcpServerHandler;
import com.jingcai.apps.commonrpc.zk.zk.factory.CommonRpcZkFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;

public class CommonRpcTcpServerHandler extends ChannelInboundHandlerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(CommonRpcTcpServerHandler.class);
	private String group;
	private String ip;
	private int port;
	private int procotolType;//协议名称
	private int codecType;//编码类型
	private ExecutorService threadPoolExecutor;

	public CommonRpcTcpServerHandler(String group, String ip, int port, ExecutorService threadPoolExecutor, int procotolType, int codecType) {
		this.group = group;
		this.ip = ip;
		this.port = port;
		this.procotolType = procotolType;
		this.codecType = codecType;
		this.threadPoolExecutor = threadPoolExecutor;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("client:{} connected", ctx.channel().remoteAddress().toString());

		SocketAddress socketAddress = ctx.channel().remoteAddress();
		if (socketAddress instanceof InetSocketAddress) {
			InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
			String remoteAddr = inetSocketAddress.getAddress().getHostAddress() + ":" + inetSocketAddress.getPort();
			CommonRpcZkFactory.getServer().registerClient(group, ip + ":" + port, remoteAddr);
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("client:{} disconnected", ctx.channel().remoteAddress().toString());
		ctx.channel().close();

		SocketAddress socketAddress = ctx.channel().remoteAddress();
		if (socketAddress instanceof InetSocketAddress) {
			InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
			String remoteAddr = inetSocketAddress.getAddress().getHostAddress() + ":" + inetSocketAddress.getPort();
			CommonRpcZkFactory.getServer().unregisterClient(group, ip + ":" + port, remoteAddr);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable e)
			throws Exception {
		if (!(e.getCause() instanceof IOException)) {
			logger.error("catch some exception not IOException", e);
		}
		closeChannel(ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if (msg instanceof CommonRpcRequest) {
			threadPoolExecutor.submit(new ServerHandlerRunnable(ctx, (CommonRpcRequest) msg));
		} else {
			throw new Exception("receive message error,only support CommonRpcRequest || List");
		}
	}


	private class ServerHandlerRunnable implements Runnable {
		private ChannelHandlerContext ctx;
		private CommonRpcRequest message;

		public ServerHandlerRunnable(ChannelHandlerContext ctx, CommonRpcRequest message) {
			super();
			this.ctx = ctx;
			this.message = message;
		}

		@Override
		public void run() {
			handleRequestWithSingleThread(ctx, message);
		}
	}

	/**
	 * disruptor处理方式
	 *
	 * @param ctx
	 * @param request
	 */
	private void handleRequestWithSingleThread(final ChannelHandlerContext ctx, CommonRpcRequest request) {
		try {
			CommonRpcResponse rocketRPCResponse = RpcTcpServerHandler.getInstance().handleRequest(request, codecType, procotolType);
			if (ctx.channel().isOpen()) {
				ChannelFuture wf = ctx.channel().writeAndFlush(rocketRPCResponse);
				wf.addListener(new ChannelFutureListener() {
					public void operationComplete(ChannelFuture future) throws Exception {
						if (!future.isSuccess()) {
							closeChannel(ctx);
						}
					}
				});
			}
		} catch (Exception e) {
			sendErrorResponse(ctx, request, e.getMessage());
		} finally {
			ReferenceCountUtil.release(request);
		}
	}

	private void sendErrorResponse(final ChannelHandlerContext ctx, final CommonRpcRequest request, String errorMessage) {
		CommonRpcResponse commonRpcResponse = new CommonRpcResponse(request.getId(), request.getCodecType(), request.getProtocolType());
		commonRpcResponse.setException(new Exception(errorMessage));
		ChannelFuture wf = ctx.channel().writeAndFlush(commonRpcResponse);
		wf.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future) throws Exception {
				if (!future.isSuccess()) {
					closeChannel(ctx);
				}
			}
		});
	}

	private void closeChannel(ChannelHandlerContext ctx){
		InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
		logger.error("server write response error, client Ip is:{}", remoteAddress);
		ctx.channel().close();
	}
}
