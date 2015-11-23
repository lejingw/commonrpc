package com.cross.plateform.common.rpc.service.factory;

import com.cross.plateform.common.rpc.service.client.CommonServiceClient;
import com.cross.plateform.common.rpc.service.client.impl.CommonServiceClientImpl;
import com.cross.plateform.common.rpc.service.server.CommonServiceServer;
import com.cross.plateform.common.rpc.service.server.impl.CommonServiceServerImpl;

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
