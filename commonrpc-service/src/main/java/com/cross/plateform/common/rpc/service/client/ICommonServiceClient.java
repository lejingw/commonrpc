/**
 * 
 */
package com.cross.plateform.common.rpc.service.client;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * @author liubing
 *
 */
public interface ICommonServiceClient {
	
	
	
	/**
	 * 获取服务端 group 对应的server
	 * @param group
	 * @return
	 */
	Set<InetSocketAddress> getServersByGroup(String group) throws Exception;
	
	/**
	 * 关闭服务
	 */
	void close() throws Exception;
	
	/**
	 * 连接zk
	 * @param server
	 * @param timeout
	 * @throws Exception
	 */
	void connectZookeeper(String server,int timeout) throws Exception;
	
	
}
