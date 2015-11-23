package com.cross.plateform.common.rpc.core.client.factory;

import com.cross.plateform.common.rpc.core.all.message.CommonRpcResponse;
import com.cross.plateform.common.rpc.core.client.AbstractRpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractRpcClientFactory<T extends AbstractRpcClient> implements RpcClientFactory<T> {
	private static final Logger logger = LoggerFactory.getLogger(AbstractRpcClientFactory.class);
	private Map<Integer, BlockingQueue<Object>> responses = new ConcurrentHashMap<>();
	private Map<String, T> rpcClients = new ConcurrentHashMap<>();
	private ReentrantLock lock = new ReentrantLock();

	@Override
	public T getClient(String host, int port) throws Exception {
		String key = getKey(host, port);
		if (rpcClients.containsKey(key)) {
			return rpcClients.get(key);
		}
		lock.lock();
		try {
			T client = null;
			if (null != (client = createClient(host, port))) {
				rpcClients.put(key, client);
			}
			return client;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void removeRpcClient(String host, int port) throws Exception {
		String key = getKey(host, port);
		if (rpcClients.containsKey(key)) {
			lock.lock();
			try {
				T cli = rpcClients.remove(key);
				if (null != cli) {
					destroyClient(cli);
				}
			} finally {
				lock.unlock();
			}
		}
	}

	private String getKey(String host, int port) {
		return host + ":" + port;
	}

	protected abstract T createClient(String targetIP, int targetPort) throws Exception;

	protected abstract void destroyClient(T client) throws Exception;

	@Override
	public void receiveResponse(CommonRpcResponse response) throws Exception {
		if (!responses.containsKey(response.getRequestId())) {
			logger.error("give up the response, request id is:" + response.getRequestId() + ", maybe because timeout!");
			return;
		} else {
			try {
				BlockingQueue<Object> queue = responses.get(response.getRequestId());
				if (queue != null) {
					queue.put(response);
				} else {
					logger.error("give up the response, request id is:" + response.getRequestId() + ", because queue is null");
				}
			} catch (InterruptedException e) {
				logger.error("put response error, request id is:" + response.getRequestId(), e);
			}
		}
	}

	@Override
	public void putResponse(int key, BlockingQueue<Object> queue)
			throws Exception {
		responses.put(key, queue);
	}

	@Override
	public void removeResponse(int key) {
		responses.remove(key);
	}

	public void clearClients() {
		lock.lock();
		try {
			for (Map.Entry<String, T> entry : rpcClients.entrySet()) {
				T cli = entry.getValue();
				try {
					destroyClient(cli);
				} catch (Exception e) {
					logger.error("destroy client[" + cli.getServerIP() + ":" + cli.getServerPort() + "] error", e);
				}
			}
			rpcClients.clear();
		} finally {
			lock.unlock();
		}
	}
}
