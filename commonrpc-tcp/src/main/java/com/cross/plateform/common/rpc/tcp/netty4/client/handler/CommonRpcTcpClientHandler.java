package com.cross.plateform.common.rpc.tcp.netty4.client.handler;

import com.cross.plateform.common.rpc.core.all.message.CommonRpcResponse;
import com.cross.plateform.common.rpc.core.client.factory.RpcClientFactory;
import com.cross.plateform.common.rpc.tcp.netty4.client.factory.CommonRpcTcpClientFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class CommonRpcTcpClientHandler extends ChannelInboundHandlerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(CommonRpcTcpClientHandler.class);

	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			if (msg instanceof CommonRpcResponse) {
				CommonRpcResponse response = (CommonRpcResponse) msg;
				logger.debug("receive response list from server: " + ctx.channel().remoteAddress() + ",request is:" + response.getRequestId());
				CommonRpcTcpClientFactory.getInstance().receiveResponse(response);
			} else {
				throw new Exception("receive message error,only support List || ResponseWrapper");
			}
		} finally {
			ReferenceCountUtil.release(msg);
		}
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
		if (!(e.getCause() instanceof IOException)) {
			logger.error("catch some exception not IOException", e);
		}
		SocketAddress socketAddress = ctx.channel().remoteAddress();
		if (socketAddress instanceof InetSocketAddress) {
			InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
			RpcClientFactory instance = CommonRpcTcpClientFactory.getInstance();
			instance.removeRpcClient(inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort());
		}
	}

	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		SocketAddress socketAddress = ctx.channel().remoteAddress();
		logger.info("connection closed: " + socketAddress);
		if (socketAddress instanceof InetSocketAddress) {
			InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
			RpcClientFactory instance = CommonRpcTcpClientFactory.getInstance();
			instance.removeRpcClient(inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort());
		}
	}
}
