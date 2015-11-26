package com.jingcai.apps.commonrpc.tcp.server.handler;

import com.jingcai.apps.commonrpc.core.filter.RpcFilter;

public interface RpcServerHandler {

    /**
     * 注册服务
     *
     * @param instanceName
     * @param instance
     */
    void registerProcessor(String instanceName, Object instance, RpcFilter rpcFilter);

    /**
     * 清除
     */
    void clear();
}
