package com.jingcai.apps.commonrpc.core.client.invocation;

import com.jingcai.apps.commonrpc.core.client.RpcClient;
import com.jingcai.apps.commonrpc.core.client.factory.RpcClientFactory;
import com.jingcai.apps.commonrpc.core.route.bean.RpcRouteServer;
import com.jingcai.apps.commonrpc.core.util.SocketAddressUtil;
import com.jingcai.apps.commonrpc.zk.zk.factory.CommonRpcZkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;
import java.util.Set;

public abstract class AbstractRpcClientInvocationHandler implements InvocationHandler {
	private static final Logger logger = LoggerFactory.getLogger(AbstractRpcClientInvocationHandler.class);
	private final String group;
	private final int timeout;
	private final String targetInstanceName;
	private final int codecType;
	private final int protocolType;
	private final Random random;

	public AbstractRpcClientInvocationHandler(String group, int timeout, String targetInstanceName, int codecType, int protocolType) {
		this.group = group;
		this.timeout = timeout;
		this.targetInstanceName = targetInstanceName;
		this.codecType = codecType;
		this.protocolType = protocolType;
		this.random = new Random();
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Set<InetSocketAddress> addresses = CommonRpcZkFactory.getClient().getServersByGroup(group);
		if (null == addresses || addresses.size() < 1) {
			throw new RuntimeException("count not find remote server, please check the server side");
		}
		List<RpcRouteServer> servers = SocketAddressUtil.getInetSocketAddress(addresses);
		int j = random.nextInt(servers.size());
		InetSocketAddress server = servers.get(j).getServer();
		String ip = server.getAddress().getHostAddress();
		int port = server.getPort();
		logger.debug("call [{}-{}:{}] method {}", group, ip, port, method);

		RpcClient client = getClientFactory().getClient(ip, port);
		String methodName = method.getName();
		String[] argTypes = createParamSignature(method.getParameterTypes());
		Object result = client.invokeImpl(targetInstanceName, methodName, argTypes, args, timeout, codecType, protocolType);
		return result;
	}

	private String[] createParamSignature(Class<?>[] argTypes) {
		if (argTypes == null || argTypes.length == 0) {
			return new String[]{};
		}
		String[] paramSig = new String[argTypes.length];
		for (int i = 0; i < argTypes.length; i++) {
			paramSig[i] = argTypes[i].getName();
		}
		return paramSig;
	}

	public abstract RpcClientFactory getClientFactory();
}
