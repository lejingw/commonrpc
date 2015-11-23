package com.cross.plateform.common.rpc.core.client.factory;

import com.cross.plateform.common.rpc.core.all.message.CommonRpcResponse;
import com.cross.plateform.common.rpc.core.client.RpcClient;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public interface RpcClientFactory<T extends RpcClient> {

	/**
	 * 创建客户端服务
	 *
	 * @param timeout 连接超时时间
	 */
	void startClientFactory(int timeout);

	/**
	 * 停止客户端服务
	 *
	 * @throws Exception
	 */
	void stopClientFactory() throws Exception;

	/**
	 * 获取调用客户端
	 *
	 * @param host
	 * @param port
	 */
	T getClient(String host, int port) throws Exception;

	/**
	 * 销毁客户端
	 *
	 * @param host
	 * @param port
	 */
	void removeRpcClient(String host, int port) throws Exception;


	/**
	 * 接收消息
	 *
	 * @param response
	 * @throws Exception
	 */
	void receiveResponse(CommonRpcResponse response) throws Exception;


	void putResponse(int key, BlockingQueue<Object> queue) throws Exception;

	/**
	 * 删除消息
	 *
	 * @param key
	 */
	void removeResponse(int key);
}