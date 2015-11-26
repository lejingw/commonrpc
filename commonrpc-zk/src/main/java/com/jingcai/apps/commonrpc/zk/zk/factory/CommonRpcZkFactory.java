package com.jingcai.apps.commonrpc.zk.zk.factory;

import com.jingcai.apps.commonrpc.zk.zk.client.CommonServiceClient;
import com.jingcai.apps.commonrpc.zk.zk.client.impl.CommonServiceClientImpl;
import com.jingcai.apps.commonrpc.zk.zk.server.CommonServiceServer;
import com.jingcai.apps.commonrpc.zk.zk.server.impl.CommonServiceServerImpl;

public class CommonRpcZkFactory {
    private static CommonServiceServer[] serverHandlers = new CommonServiceServer[1];
    private static CommonServiceClient[] clientHandlers = new CommonServiceClient[1];

    static {
        serverHandlers[0] = new CommonServiceServerImpl();
        clientHandlers[0] = new CommonServiceClientImpl();
    }

    public static CommonServiceServer getServer() {
        return serverHandlers[0];
    }

    public static CommonServiceClient getClient() {
        return clientHandlers[0];
    }
}
