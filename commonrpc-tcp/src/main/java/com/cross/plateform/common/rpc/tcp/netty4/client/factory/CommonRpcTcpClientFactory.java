package com.cross.plateform.common.rpc.tcp.netty4.client.factory;

import com.cross.plateform.common.rpc.core.client.factory.AbstractRpcClientFactory;
import com.cross.plateform.common.rpc.core.client.factory.RpcClientFactory;
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
	private static final Logger logger = LoggerFactory.getLogger(CommonRpcTcpClientFactory.class);
	private static final int PROCESSORS_COUNT = Runtime.getRuntime().availableProcessors();
	private static final RpcClientFactory instance = new CommonRpcTcpClientFactory();

	private static final ThreadFactory workerThreadFactory = new NamedThreadFactory("CommonRpc-WORKER-");
	private static EventLoopGroup workerGroup = new NioEventLoopGroup(PROCESSORS_COUNT, workerThreadFactory);
	private final Bootstrap bootstrap = new Bootstrap();

	private CommonRpcTcpClientFactory() {
	}

	public static RpcClientFactory getInstance() {
		return instance;
	}

	@Override
	public void startClientFactory(int connectTimeout) {
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
		logger.info("CommonRpc client is started ...");
		logger.info("====CommonRpc服务已启动====");
	}

	@Override
	public void stopClientFactory() throws Exception {
		try {
			clearClients();
		}finally {
			workerGroup.shutdownGracefully();
		}
		logger.info("CommonRpc client has been stoped !!!");
		logger.info("====CommonRpc服务已停止====");
	}

	@Override
	protected CommonRpcTcpClient createClient(String ip, int port) throws Exception {
		ChannelFuture future = null;
		try {
			future = bootstrap.connect(new InetSocketAddress(ip, port)).sync();
		} catch (Exception e) {
			RpcClientFactory instance = CommonRpcTcpClientFactory.getInstance();
			instance.removeRpcClient(ip, port);
			throw e;
		}
		future.awaitUninterruptibly();
		if (!future.isDone()) {
			throw new Exception("Create connection to " + ip + ":" + port + " timeout!");
		}
		if (future.isCancelled()) {
			throw new Exception("Create connection to " + ip + ":" + port + " cancelled by user!");
		}
		if (!future.isSuccess()) {
			throw new Exception("Create connection to " + ip + ":" + port + " error", future.cause());
		}
		return new CommonRpcTcpClient(future);
	}

	@Override
	protected void destroyClient(CommonRpcTcpClient client) throws Exception {
		client.destroy();
	}
}
