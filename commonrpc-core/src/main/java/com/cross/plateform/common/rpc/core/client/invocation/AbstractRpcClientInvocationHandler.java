package com.cross.plateform.common.rpc.core.client.invocation;

import com.cross.plateform.common.rpc.core.client.RpcClient;
import com.cross.plateform.common.rpc.core.client.factory.RpcClientFactory;
import com.cross.plateform.common.rpc.core.route.bean.RpcRouteServer;
import com.cross.plateform.common.rpc.core.util.SocketAddressUtil;
import com.cross.plateform.common.rpc.service.factory.CommonRpcServiceFactory;
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
	private String group;
	private int timeout;
	private String targetInstanceName;
	private int codecType;
	private int protocolType;
	private Random random;

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
		String[] groups = group.split(",");
		int i = random.nextInt(groups.length);
		Set<InetSocketAddress> addresses = CommonRpcServiceFactory.getCommonServiceClient().getServersByGroup(group);
		if (null == addresses || addresses.size() < 1) {
			throw new RuntimeException("count not find remote server, please check the server side");
		}
		List<RpcRouteServer> servers = SocketAddressUtil.getInetSocketAddress(addresses);
		int j = random.nextInt(servers.size());
		InetSocketAddress server = servers.get(j).getServer();
//        InetSocketAddress server = new ArrayList<InetSocketAddress>(addresses).get(random.nextInt(addresses.size()));
		String ip = server.getAddress().getHostAddress();
		int port = server.getPort();
		logger.debug("call [{}-{}:{}] method {}", groups[i], ip, port, method);
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
		for (int x = 0; x < argTypes.length; x++) {
			paramSig[x] = argTypes[x].getName();
		}
		return paramSig;
	}

	public abstract RpcClientFactory getClientFactory();
}
