package com.jingcai.apps.commonrpc.zk.zk.client;

import java.net.InetSocketAddress;
import java.util.Set;

public interface CommonServiceClient {
	/**
	 * 连接zk
	 *
	 * @param server
	 * @param timeout
	 * @throws Exception
	 */
	void connectZookeeper(String server, int timeout) throws Exception;

	/**
	 * 获取服务端 group 对应的server
	 *
	 * @param group
	 * @return
	 */
	Set<InetSocketAddress> getServersByGroup(String group) throws Exception;

	/**
	 * 关闭服务
	 */
	void close() throws Exception;


}
