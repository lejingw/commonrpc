package com.jingcai.apps.commonrpc.core.client;

import com.jingcai.apps.commonrpc.core.client.factory.RpcClientFactory;

public interface RpcClient {
    /**
     * 动态调用
     *
     * @param targetInstanceName
     * @param methodName
     * @param argTypes
     * @param args
     * @param timeout
     * @param codecType
     * @param protocolType
     * @return
     * @throws Exception
     */
    Object invokeImpl(String targetInstanceName, String methodName,
                      String[] argTypes, Object[] args, int timeout, int codecType, int protocolType)
            throws Throwable;

    /**
     * server address
     *
     * @return String
     */
    String getServerIP();

    /**
     * server port
     *
     * @return int
     */
    int getServerPort();

    /**
     * Get factory
     */
    RpcClientFactory getClientFactory();
}
