package com.cross.plateform.common.rpc.core.client.factory;


import java.util.concurrent.LinkedBlockingQueue;
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
	
	public void putResponse(Integer key,LinkedBlockingQueue<Object> queue) throws Exception;
	/**
	 * 接受消息
	 * @param response
	 * @throws Exception
	 */
	public void receiveResponse(CommonRpcResponse response) throws Exception;
	
	
	
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
