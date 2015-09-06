package com.cross.plateform.common.rpc.core.client.factory;

import com.cross.plateform.common.rpc.core.all.message.CommonRpcResponse;
import com.cross.plateform.common.rpc.core.client.AbstractRpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class AbstractRpcClientFactory<T extends AbstractRpcClient> implements RpcClientFactory<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRpcClientFactory.class);
    protected static ConcurrentHashMap<Integer, LinkedBlockingQueue<Object>> responses = new ConcurrentHashMap<>();
    protected Map<String, T> rpcClients = new ConcurrentHashMap<>();

    @Override
    public T getClient(String host, int port) throws Exception {
        String key = getKey(host, port);
        if(containClient(key)){
            return getClient(key);
        }
        T client = createClient(host, port);
        if(null != client) {
            putRpcClient(key, client);
        }
        return client;
    }

    public String getKey(String host, int port) {
        return "/" + host + ":" + port;
    }

    protected abstract T createClient(String targetIP, int targetPort) throws Exception;

    @Override
    public void receiveResponse(CommonRpcResponse response) throws Exception {
        if (!responses.containsKey(response.getRequestId())) {
            LOGGER.error("give up the response,request id is:" + response.getRequestId() + ",maybe because timeout!");
            return;
        }
        try {
            if (responses.containsKey(response.getRequestId())) {
                LinkedBlockingQueue<Object> queue = responses.get(response.getRequestId());
                if (queue != null) {
                    queue.put(response);
                } else {
                    LOGGER.warn("give up the response,request id is:" + response.getRequestId() + ",because queue is null");
                }
            }
        } catch (InterruptedException e) {
            LOGGER.error("put response error,request id is:" + response.getRequestId(), e);
        }
    }

    @Override
    public void putResponse(int key, LinkedBlockingQueue<Object> queue)
            throws Exception {
        responses.put(key, queue);
    }

    @Override
    public void removeResponse(int key) {
        responses.remove(key);
    }

    public void clearClients() {
        for (Map.Entry<String, T> entry : rpcClients.entrySet()) {
            T cli = entry.getValue();
            try {
                cli.destroy();
            } catch (Exception e) {
                LOGGER.error("destroy client[" + cli.getServerIP() + ":" + cli.getServerPort() + "] error", e);
            }
        }
        rpcClients.clear();
    }

    public T getClient(String key){
        return rpcClients.get(key);
    }

    public void putRpcClient(String key, T rpcClient) {
        rpcClients.put(key, rpcClient);
    }

    @Override
    public boolean containClient(String key) {
        return rpcClients.containsKey(key);
    }

    @Override
    public void removeRpcClient(String key) {
        if (rpcClients.containsKey(key)) {
            T cli = rpcClients.remove(key);
            try {
                cli.destroy();
            } catch (Exception e) {
                LOGGER.error("remove client[" + cli.getServerIP() + ":" + cli.getServerPort() + "] error", e);
            }
        }
    }
}
