/**
 * 
 */
package com.cross.plateform.common.rpc.core.client.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cross.plateform.common.rpc.core.all.message.CommonRpcResponse;
import com.cross.plateform.common.rpc.core.client.AbstractRpcClient;
import com.cross.plateform.common.rpc.core.client.RpcClient;
/**
 * @author liubing1
 *
 */
public abstract class AbstractRpcClientFactory implements RpcClientFactory {
		
	protected static ConcurrentHashMap<Integer, LinkedBlockingQueue<Object>> responses = 
			new ConcurrentHashMap<Integer, LinkedBlockingQueue<Object>>();
	
	
	protected static Map<String, AbstractRpcClient> rpcClients = 
			new ConcurrentHashMap<String, AbstractRpcClient>();
	
	private static final Log LOGGER = LogFactory.getLog(AbstractRpcClientFactory.class);
	/* (non-Javadoc)
	 * @see com.cross.plateform.common.rpc.core.client.factory.RpcClientFactory#getClient(java.lang.String, int, int, boolean)
	 */
	@Override
	public RpcClient getClient(String host, int port) throws Exception {
		// TODO Auto-generated method stub
		String key="/"+host+":"+port;
		if(rpcClients.containsKey(key)){
			return rpcClients.get(key);
		}
		return createClient(host, port);
	}

	protected abstract RpcClient createClient(String targetIP, int targetPort) throws Exception;
	
	
	@Override
	public void putResponse(Integer key, LinkedBlockingQueue<Object> queue)
			throws Exception {
		// TODO Auto-generated method stub
		responses.put(key, queue);
	}
	/**
	 * 停止客户端
	 */
	public abstract void stopClient() throws Exception;
	
	@Override
	public void receiveResponse(CommonRpcResponse response) throws Exception {
		// TODO Auto-generated method stub
		if (!responses.containsKey(response.getRequestId())) {
			LOGGER.error("give up the response,request id is:" + response.getRequestId() + ",maybe because timeout!");
			return;
		}
		try {
			
			if(responses.containsKey(response.getRequestId())){
				
				LinkedBlockingQueue<Object> queue = responses.get(response.getRequestId());
				if (queue != null) {
					queue.put(response);
				} else {
					LOGGER.warn("give up the response,request id is:"
							+ response.getRequestId() + ",because queue is null");
				}
			}
			
		} catch (InterruptedException e) {
			LOGGER.error("put response error,request id is:" + response.getRequestId(), e);
		}
		
		
	}
	
	@Override
	public void removeResponse(Integer key) {
		// TODO Auto-generated method stub
		responses.remove(key);
	}
	
	public void clearClients(){
		rpcClients.clear();
	}
	
	@Override
	public void putRpcClient(String key, AbstractRpcClient rpcClient) {
		// TODO Auto-generated method stub
		rpcClients.put(key, rpcClient);
	}
	
	@Override
	public boolean containClient(String key){
		return rpcClients.containsKey(key);
	}
	
	@Override
	public void removeRpcClient(String key) {
		// TODO Auto-generated method stub
		if(rpcClients.containsKey(key)){
			rpcClients.remove(key);
		}
	}
}
