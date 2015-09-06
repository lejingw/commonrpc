package com.cross.plateform.common.rpc.service.client.service;

import java.net.InetSocketAddress;
import java.util.Set;

import com.cross.plateform.common.rpc.service.factory.CommonRpcServiceFactory;

public class CommonRpcClientService {

    public CommonRpcClientService() {
    }

    private static class SingletonHolder {
        static final CommonRpcClientService instance = new CommonRpcClientService();
    }

    public static CommonRpcClientService getInstance() {
        return SingletonHolder.instance;
    }

    public Set<InetSocketAddress> getServersByGroup(String group) throws Exception {
        return CommonRpcServiceFactory.getCommonServiceClient().getServersByGroup(group);
    }

    public void close() throws Exception {
        CommonRpcServiceFactory.getCommonServiceClient().close();
    }

    public void connectZookeeper(String server, int timeout) throws Exception {
        CommonRpcServiceFactory.getCommonServiceClient().connectZookeeper(server, timeout);
    }
}
