package com.cross.plateform.common.rpc.client.proxy;

public interface ClientProxy {

	<T> T getProxyService(Class<T> clazz, int timeout, int codecType,
						  int protocolType, String targetInstanceName, String group);
}
