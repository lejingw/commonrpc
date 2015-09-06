package com.cross.plateform.common.rpc.tcp.netty4.client.factory;

import com.cross.plateform.common.rpc.core.client.factory.AbstractRpcClientFactory;
import com.cross.plateform.common.rpc.core.thread.NamedThreadFactory;
import com.cross.plateform.common.rpc.tcp.netty4.client.CommonRpcTcpClient;
import com.cross.plateform.common.rpc.tcp.netty4.client.handler.CommonRpcTcpClientHandler;
import com.cross.plateform.common.rpc.tcp.netty4.codec.CommonRpcDecoderHandler;
import com.cross.plateform.common.rpc.tcp.netty4.codec.CommonRpcEncoderHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;

public class CommonRpcTcpClientFactory extends AbstractRpcClientFactory<CommonRpcTcpClient> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonRpcTcpClientFactory.class);
    private static final int PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final ThreadFactory workerThreadFactory = new NamedThreadFactory("NETTYCLIENT-WORKER-");
    private static EventLoopGroup workerGroup = new NioEventLoopGroup(6 * PROCESSORS, workerThreadFactory);
    private static AbstractRpcClientFactory _self = new CommonRpcTcpClientFactory();
    private final Bootstrap bootstrap = new Bootstrap();

    private CommonRpcTcpClientFactory(){}

    public static AbstractRpcClientFactory getInstance() {
        return _self;
    }

    @Override
    public void startClientFactory(int connectTimeout) {
        LOGGER.info("----------------客户端开始启动-------------------------------");
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_SNDBUF, 65535)
                .option(ChannelOption.SO_RCVBUF, 65535);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast("decoder", new CommonRpcDecoderHandler());
                pipeline.addLast("encoder", new CommonRpcEncoderHandler());
                pipeline.addLast("timeout", new IdleStateHandler(0, 0, 120));
                pipeline.addLast("handler", new CommonRpcTcpClientHandler());
            }
        });
        LOGGER.info("----------------客户端启动结束-------------------------------");
    }

    @Override
    public void stopClientFactory() throws Exception {
        clearClients();
        workerGroup.shutdownGracefully();
    }

    @Override
    protected CommonRpcTcpClient createClient(String targetIP, int targetPort) throws Exception {
        ChannelFuture future = null;
        try {
            future = bootstrap.connect(new InetSocketAddress(targetIP, targetPort)).sync();
        }catch (Exception e){
            AbstractRpcClientFactory instance = CommonRpcTcpClientFactory.getInstance();
            String key = instance.getKey(targetIP, targetPort);
            if(instance.containClient(key)){
                instance.removeRpcClient(key);
            }
            throw e;
        }
        future.awaitUninterruptibly();
        if (!future.isDone()) {
            LOGGER.error("Create connection to " + targetIP + ":" + targetPort + " timeout!");
            throw new Exception("Create connection to " + targetIP + ":" + targetPort + " timeout!");
        }
        if (future.isCancelled()) {
            LOGGER.error("Create connection to " + targetIP + ":" + targetPort + " cancelled by user!");
            throw new Exception("Create connection to " + targetIP + ":" + targetPort + " cancelled by user!");
        }
        if (!future.isSuccess()) {
            LOGGER.error("Create connection to " + targetIP + ":" + targetPort + " error", future.cause());
            throw new Exception("Create connection to " + targetIP + ":" + targetPort + " error", future.cause());
        }
        CommonRpcTcpClient client = new CommonRpcTcpClient(future);
        return client;
    }
}
