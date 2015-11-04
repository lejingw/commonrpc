package com.cross.plateform.common.rpc.tcp.netty4.client;


import com.cross.plateform.common.rpc.core.all.message.CommonRpcRequest;
import com.cross.plateform.common.rpc.core.all.message.CommonRpcResponse;
import com.cross.plateform.common.rpc.core.client.AbstractRpcClient;
import com.cross.plateform.common.rpc.core.client.factory.RpcClientFactory;
import com.cross.plateform.common.rpc.tcp.netty4.client.factory.CommonRpcTcpClientFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class CommonRpcTcpClient extends AbstractRpcClient {
    private static final Logger logger = LoggerFactory.getLogger(CommonRpcTcpClient.class);

    private ChannelFuture cf;

    public CommonRpcTcpClient(ChannelFuture cf) {
        this.cf = cf;
    }

    @Override
    public String getServerIP() {
        return ((InetSocketAddress) cf.channel().remoteAddress()).getHostName();
    }

    @Override
    public int getServerPort() {
        return ((InetSocketAddress) cf.channel().remoteAddress()).getPort();
    }

    @Override
    public RpcClientFactory getClientFactory() {
        return CommonRpcTcpClientFactory.getInstance();
    }

    @Override
    public void sendRequest(final CommonRpcRequest commonRpcRequest) throws Exception {
        if (cf.channel().isOpen()) {
            ChannelFuture writeFuture = cf.channel().writeAndFlush(commonRpcRequest);
            // use listener to avoid wait for write & thread context switch
            writeFuture.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future)
                        throws Exception {
                    if (future.isSuccess()) {
                        return;
                    }
                    String errorMsg = null;
                    if (future.isCancelled()) {
                        errorMsg = "Send request to " + cf.channel().toString() + " cancelled by user,request id is:" + commonRpcRequest.getId();
                    } else {
                        SocketAddress socketAddress = cf.channel().remoteAddress();
                        if (socketAddress instanceof InetSocketAddress) {
                            InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
                            getClientFactory().removeRpcClient(inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort());
                        }
                        errorMsg = "Send request to " + cf.channel().toString() + " error:" + future.cause();
                    }
                    logger.error(errorMsg);
                    CommonRpcResponse response = new CommonRpcResponse(commonRpcRequest.getId(), commonRpcRequest.getCodecType(), commonRpcRequest.getProtocolType());
                    response.setException(new Exception(errorMsg));
                    getClientFactory().receiveResponse(response);
                }
            });
        }
    }

    @Override
    public void destroy() throws Exception {
        if (cf.channel().isOpen()) {
//            cf.channel().closeFuture().sync();
            cf.channel().close();
        }
    }
}
