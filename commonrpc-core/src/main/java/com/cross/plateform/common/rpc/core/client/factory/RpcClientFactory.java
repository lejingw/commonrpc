package com.cross.plateform.common.rpc.core.client.factory;

import java.util.concurrent.CountDownLatch;

import com.cross.plateform.common.rpc.core.all.message.CommonRpcResponse;
import com.cross.plateform.common.rpc.core.client.AbstractRpcClient;
import com.cross.plateform.common.rpc.core.client.RpcClient;

public interface RpcClientFactory {
	
	/**
	 * 
	 * @param host
	 * @param port
	 * @param connectiontimeout
	 * @param keepalive
	 */
	public RpcClient getClient(String host,int port) throws Exception;
	
	/**
	 * 创建客户端
	 * @param connectiontimeout  连接超时时间
	 */
	public void startClient(int connectiontimeout);
	
	
	/**
	 * 接受消息
	 * @param response
	 * @throws Exception
	 */
	public void receiveResponse(CommonRpcResponse response) throws Exception;
	/**
	 * 获取消息
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public CommonRpcResponse getResponse(Integer key) throws Exception;
	
	/**
	 * 放入消息
	 * @param response
	 */
	public void putObject(Integer key,CountDownLatch object) throws Exception;
	/**
	 * 
	 * @param key
	 * @return
	 */
	public boolean checkIdByKey(Integer key);
	/**
	 * 删除消息
	 * @param key
	 */
	public void removeResponse(Integer key);
	/**
	 *
	 * @param key
	 * @param rpcClient
	 */
	public void putRpcClient(String key,AbstractRpcClient rpcClient);
	
	/**
	 * 
	 * @param key
	 */
	public void removeRpcClient(String key);
	
	public boolean containClient(String key);
	
}
