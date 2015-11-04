package com.cross.plateform.common.rpc.service.server;

public interface CommonServiceServer {
	/**
	 * 注册服务
	 *
	 * @param group  组名
	 * @param server 机器
	 */
	void registerServer(String group, String server) throws Exception;

	/**
	 * 注册客户端
	 *
	 * @param group
	 * @param server
	 * @param client
	 * @return
	 */
	void registerClient(String group, String server, String client) throws Exception;

	/**
	 * 关闭服务
	 */
	void close() throws Exception;

	/**
	 * 连接zk
	 *
	 * @param server
	 * @param timeout
	 * @throws Exception
	 */
	void connectZookeeper(String server, int timeout) throws Exception;
}
