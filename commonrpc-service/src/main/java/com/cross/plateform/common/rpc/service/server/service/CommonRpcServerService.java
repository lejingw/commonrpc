package com.cross.plateform.common.rpc.service.server.service;

import com.cross.plateform.common.rpc.service.factory.CommonRpcServiceFactory;

public class CommonRpcServerService {

    public CommonRpcServerService() {

    }

    private static class SingletonHolder {
        static final CommonRpcServerService instance = new CommonRpcServerService();
    }

    public static CommonRpcServerService getInstance() {
        return SingletonHolder.instance;
    }


    public void connectZookeeper(String server, int timeout) throws Exception {
        CommonRpcServiceFactory.getCommonServiceServer().connectZookeeper(server, timeout);
    }

    public void close() throws Exception {
        CommonRpcServiceFactory.getCommonServiceServer().close();
    }

    public void registerService(String group, String server) throws Exception {
        CommonRpcServiceFactory.getCommonServiceServer().registerServer(group, server);
    }

    public void registerClient(String group, String server, String client) throws Exception {
        CommonRpcServiceFactory.getCommonServiceServer().registerClient(group, server, client);
    }

    public void unregisterClient(String group, String server, String client) throws Exception {
        CommonRpcServiceFactory.getCommonServiceServer().unregisterClient(group, server, client);
    }
}
