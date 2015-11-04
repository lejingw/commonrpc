package com.cross.plateform.common.rpc.service.factory;

import com.cross.plateform.common.rpc.service.client.CommonServiceClient;
import com.cross.plateform.common.rpc.service.client.impl.CommonServiceClientImpl;
import com.cross.plateform.common.rpc.service.server.CommonServiceServer;
import com.cross.plateform.common.rpc.service.server.impl.CommonServiceServerImpl;

public class CommonRpcServiceFactory {
    private static CommonServiceServer[] serverHandlers = new CommonServiceServer[1];
    private static CommonServiceClient[] clientHandlers = new CommonServiceClient[1];
    static {
        registerProtocol(0, new CommonServiceServerImpl(), 0, new CommonServiceClientImpl());
    }

    private static void registerProtocol(int type, CommonServiceServer serverHandler,
                                         int httptype, CommonServiceClient clientHandler) {
        if (type > serverHandlers.length) {
            CommonServiceServer[] newServerHandlers = new CommonServiceServer[type + 1];
            System.arraycopy(serverHandlers, 0, newServerHandlers, 0, serverHandlers.length);
            serverHandlers = newServerHandlers;
        }
        serverHandlers[type] = serverHandler;

        if (httptype > clientHandlers.length) {
            CommonServiceClient[] newServerHandlers = new CommonServiceClient[httptype + 1];
            System.arraycopy(clientHandlers, 0, newServerHandlers, 0, clientHandlers.length);
            clientHandlers = newServerHandlers;
        }
        clientHandlers[httptype] = clientHandler;
    }

    public static CommonServiceServer getCommonServiceServer() {
        return serverHandlers[0];
    }

    public static CommonServiceClient getCommonServiceClient() {
        return clientHandlers[0];
    }
}
