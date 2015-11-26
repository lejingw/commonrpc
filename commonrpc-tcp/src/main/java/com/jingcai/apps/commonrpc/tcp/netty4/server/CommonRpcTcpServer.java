package com.jingcai.apps.commonrpc.tcp.netty4.server;

import com.jingcai.apps.commonrpc.core.server.RpcServer;
import com.jingcai.apps.commonrpc.tcp.server.handler.impl.RpcTcpServerHandler;
import com.jingcai.apps.commonrpc.core.thread.NamedThreadFactory;
import com.jingcai.apps.commonrpc.core.filter.RpcFilter;
import com.jingcai.apps.commonrpc.tcp.netty4.codec.CommonRpcDecoderHandler;
import com.jingcai.apps.commonrpc.tcp.netty4.codec.CommonRpcEncoderHandler;
import com.jingcai.apps.commonrpc.tcp.netty4.server.handler.CommonRpcTcpServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommonRpcTcpServer implements RpcServer {
	private static final Logger logger = LoggerFactory.getLogger(CommonRpcTcpServer.class);
	private static final int PROCESSORS_COUNT = Runtime.getRuntime().availableProcessors();
	private ExecutorService executorService;
	private ChannelFuture channelFuture;
	private EventLoopGroup bossGroup;
	private NioEventLoopGroup workerGroup;
	private int procotolType;//协议名称
	private int codecType;//编码类型
	private int threadCount;//线程数

	private CommonRpcTcpServer() {
	}

	private static class SingletonHolder {
		static final CommonRpcTcpServer instance = new CommonRpcTcpServer();
	}

	public static CommonRpcTcpServer getInstance() {
		return SingletonHolder.instance;
	}

	@Override
	public void registerProcessor(String serviceName, Object serviceInstance, RpcFilter rpcFilter, int codecType) {
		RpcTcpServerHandler.getInstance().registerProcessor(serviceName, serviceInstance, rpcFilter, codecType);
	}

	@Override
	public void start(final String group, final String ip, final int port, final int timeout) throws Exception {
		executorService = Executors.newFixedThreadPool(threadCount);
		bossGroup = new NioEventLoopGroup(PROCESSORS_COUNT, new NamedThreadFactory("CommonRpc-BOSS-"));
		workerGroup = new NioEventLoopGroup(PROCESSORS_COUNT * 2, new NamedThreadFactory("CommonRpc-WORKER-"));
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
				pipeline.addLast("handler", new CommonRpcTcpServerHandler(group, ip, port, executorService, procotolType, codecType));
			}
		});

		channelFuture = bootstrap.bind(new InetSocketAddress(port)).sync();
		logger.info("CommonRpc Server is listening on port:{} ...", port);
		logger.info("====CommonRpc服务已启动，正在监听端口" + port + "====");
	}

	@Override
	public void stop() throws Exception {
		try {
			channelFuture.channel().close().awaitUninterruptibly();
			executorService.shutdownNow();
			RpcTcpServerHandler.getInstance().clear();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
		logger.info("CommonRpc Server has been stoped !!!");
		logger.info("====CommonRpc服务已停止====");
	}

	public void setProcotolType(int procotolType) {
		this.procotolType = procotolType;
	}

	public void setCodecType(int codecType) {
		this.codecType = codecType;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}
}
