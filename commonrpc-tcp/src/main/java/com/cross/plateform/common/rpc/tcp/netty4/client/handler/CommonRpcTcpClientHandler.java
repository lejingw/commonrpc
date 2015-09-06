package com.cross.plateform.common.rpc.tcp.netty4.client.handler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import com.cross.plateform.common.rpc.core.all.message.CommonRpcResponse;
import com.cross.plateform.common.rpc.core.client.factory.AbstractRpcClientFactory;
import com.cross.plateform.common.rpc.tcp.netty4.client.factory.CommonRpcTcpClientFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonRpcTcpClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonRpcTcpClientHandler.class);
    private static final boolean isDebugEnabled = LOGGER.isDebugEnabled();

    public CommonRpcTcpClientHandler() {
        super();
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof CommonRpcResponse) {
                CommonRpcResponse response = (CommonRpcResponse) msg;
                if (isDebugEnabled) {
                    // for performance trace
                    LOGGER.debug("receive response list from server: "
                            + ctx.channel().remoteAddress() + ",request is:"
                            + response.getRequestId());
                }
                CommonRpcTcpClientFactory.getInstance().receiveResponse(response);
            } else {
                LOGGER.error("receive message error,only support List || ResponseWrapper");
                throw new Exception(
                        "receive message error,only support List || ResponseWrapper");
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
        if (!(e.getCause() instanceof IOException)) {
            LOGGER.error("catch some exception not IOException", e);
        }
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        if (socketAddress instanceof InetSocketAddress) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
            AbstractRpcClientFactory rpcClientFactory = CommonRpcTcpClientFactory.getInstance();
            String key = rpcClientFactory.getKey(inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort());
            rpcClientFactory.removeRpcClient(key);
        }
    }

    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        LOGGER.error("connection closed: " + socketAddress);
        if (socketAddress instanceof InetSocketAddress) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
            AbstractRpcClientFactory rpcClientFactory = CommonRpcTcpClientFactory.getInstance();
            String key = rpcClientFactory.getKey(inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort());
            rpcClientFactory.removeRpcClient(key);
        }
    }
}
