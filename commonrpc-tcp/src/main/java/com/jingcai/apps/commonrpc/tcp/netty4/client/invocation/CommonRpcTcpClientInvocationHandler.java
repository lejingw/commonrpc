package com.jingcai.apps.commonrpc.tcp.netty4.client.invocation;

import com.jingcai.apps.commonrpc.core.client.factory.RpcClientFactory;
import com.jingcai.apps.commonrpc.core.client.invocation.AbstractRpcClientInvocationHandler;
import com.jingcai.apps.commonrpc.tcp.netty4.client.factory.CommonRpcTcpClientFactory;

public class CommonRpcTcpClientInvocationHandler extends AbstractRpcClientInvocationHandler {

	public CommonRpcTcpClientInvocationHandler(String group, int timeout,  String targetInstanceName, int codecType, int protocolType) {
		super(group, timeout, targetInstanceName, codecType, protocolType);
	}

	@Override
	public RpcClientFactory getClientFactory() {
		return CommonRpcTcpClientFactory.getInstance();
	}
}
