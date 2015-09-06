/**
 *
 */
package com.cross.plateform.common.rpc.tcp.netty4.server;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import com.cross.plateform.common.rpc.core.server.RpcServer;
import com.cross.plateform.common.rpc.core.server.handler.factory.CommonRpcServerHandlerFactory;
import com.cross.plateform.common.rpc.core.thread.NamedThreadFactory;
import com.cross.plateform.common.rpc.server.filter.RpcFilter;
import com.cross.plateform.common.rpc.tcp.netty4.codec.CommonRpcDecoderHandler;
import com.cross.plateform.common.rpc.tcp.netty4.codec.CommonRpcEncoderHandler;
import com.cross.plateform.common.rpc.tcp.netty4.server.handler.CommonRpcTcpServerHandler;

/**
 * @author liubing1
 */
public class CommonRpcTcpServer implements RpcServer {

    private static final Log LOGGER = LogFactory.getLog(CommonRpcTcpServer.class);

    private static final int PROCESSORS = Runtime.getRuntime().availableProcessors() * 2;

    private EventLoopGroup bossGroup;

    private NioEventLoopGroup workerGroup;

    private int procotolType;//协议名称

    private int codecType;//编码类型

    private int threadCount;//线程数

    public CommonRpcTcpServer() {

    }

    private static class SingletonHolder {
        static final CommonRpcTcpServer instance = new CommonRpcTcpServer();
    }

    public static CommonRpcTcpServer getInstance() {
        return SingletonHolder.instance;
    }

    @Override
    public void registerProcessor(String serviceName, Object serviceInstance, RpcFilter rpcFilter) {
        CommonRpcServerHandlerFactory.getServerHandler().registerProcessor(serviceName, serviceInstance, rpcFilter);
    }

    @Override
    public void stop() throws Exception {
        CommonRpcServerHandlerFactory.getServerHandler().clear();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    @Override
    public void start(final int port, final int timeout) throws Exception {
        ThreadFactory serverBossTF = new NamedThreadFactory("NETTYSERVER-BOSS-");
        ThreadFactory serverWorkerTF = new NamedThreadFactory("NETTYSERVER-WORKER-");
        bossGroup = new NioEventLoopGroup(PROCESSORS, serverBossTF);
        workerGroup = new NioEventLoopGroup(PROCESSORS * 2, serverWorkerTF);
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_SNDBUF, 65535)
                .option(ChannelOption.SO_RCVBUF, 65535)
                .childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

            protected void initChannel(SocketChannel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast("decoder", new CommonRpcDecoderHandler());
                pipeline.addLast("encoder", new CommonRpcEncoderHandler());
                pipeline.addLast("timeout", new IdleStateHandler(0, 0, 120));
                pipeline.addLast("handler", new CommonRpcTcpServerHandler(threadCount, port, procotolType, codecType));

            }

        });
        LOGGER.info("-----------------开始启动--------------------------");
        bootstrap.bind(new InetSocketAddress(port)).sync();
        LOGGER.info("端口号：" + port + "的服务端已经启动");
        LOGGER.info("-----------------启动结束--------------------------");
    }

    public void setProcotolType(int procotolType) {
        this.procotolType = procotolType;
    }

    public void setCodecType(int codecType) {
        this.codecType = codecType;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }
}
