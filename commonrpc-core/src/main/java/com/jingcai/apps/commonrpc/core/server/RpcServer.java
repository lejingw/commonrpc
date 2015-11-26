package com.jingcai.apps.commonrpc.core.server;

import com.jingcai.apps.commonrpc.core.filter.RpcFilter;

public interface RpcServer {

	/**
	 * 注册服务
	 *
	 * @param serviceName     服务名称
	 * @param serviceInstance 服务实例
	 */
	void registerProcessor(String serviceName, Object serviceInstance, RpcFilter rpcFilter, int codecType);

	/**
	 * @param port
	 * @param timeout
	 * @throws Exception
	 */
	void start(final String group, final String ip, final int port, int timeout) throws Exception;

	/**
	 * 停止
	 */
	void stop() throws Exception;
}
