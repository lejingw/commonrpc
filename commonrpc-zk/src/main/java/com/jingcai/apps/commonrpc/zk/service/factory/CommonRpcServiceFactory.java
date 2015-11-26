package com.jingcai.apps.commonrpc.zk.service.factory;

import com.jingcai.apps.commonrpc.zk.service.client.CommonServiceClient;
import com.jingcai.apps.commonrpc.zk.service.client.impl.CommonServiceClientImpl;
import com.jingcai.apps.commonrpc.zk.service.server.CommonServiceServer;
import com.jingcai.apps.commonrpc.zk.service.server.impl.CommonServiceServerImpl;

public class CommonRpcServiceFactory {
    private static CommonServiceServer[] serverHandlers = new CommonServiceServer[1];
    private static CommonServiceClient[] clientHandlers = new CommonServiceClient[1];

    static {
        serverHandlers[0] = new CommonServiceServerImpl();
        clientHandlers[0] = new CommonServiceClientImpl();
    }

    public static CommonServiceServer getCommonServiceServer() {
        return serverHandlers[0];
    }

    public static CommonServiceClient getCommonServiceClient() {
        return clientHandlers[0];
    }
}
