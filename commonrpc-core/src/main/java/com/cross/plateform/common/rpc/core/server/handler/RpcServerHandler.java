package com.cross.plateform.common.rpc.core.server.handler;

import com.cross.plateform.common.rpc.core.filter.RpcFilter;

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
