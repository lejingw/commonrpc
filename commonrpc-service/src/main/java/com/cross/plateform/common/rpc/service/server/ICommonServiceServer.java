package com.cross.plateform.common.rpc.service.server;

public interface ICommonServiceServer {
	
	/**
	 * 注册客户端
	 * @param group
	 * @param server
	 * @return
	 */
	public void registerClient(String group, String server) throws Exception;
	/**
	 * 关闭服务
	 */
	public void close() throws Exception;
	
	/**
	 * 连接zk
	 * @param server
	 * @param timeout
	 * @throws Exception
	 */
	public void connectZookeeper(String server,int timeout) throws Exception;
	
	/**
	 * 注册服务 
	 * @param group 组名
	 * @param server 机器
	 */
	public void registerServer(String group ,String server) throws Exception;
}
