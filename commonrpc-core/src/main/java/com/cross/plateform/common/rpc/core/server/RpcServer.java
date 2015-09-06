package com.cross.plateform.common.rpc.core.server;

import com.cross.plateform.common.rpc.server.filter.RpcFilter;

public interface RpcServer {

    /**
     * 注册服务
     *
     * @param serviceName     服务名称
     * @param serviceInstance 服务实例
     */
    void registerProcessor(String serviceName, Object serviceInstance, RpcFilter rpcFilter);

    /**
     * 停止
     */
    void stop() throws Exception;

    /**
     * @param port
     * @param timeout
     * @throws Exception
     */
    void start(int port, int timeout) throws Exception;
}
