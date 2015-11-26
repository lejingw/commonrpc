package com.jingcai.apps.commonrpc.tcp.server.handler;

import com.jingcai.apps.commonrpc.core.all.message.CommonRpcRequest;
import com.jingcai.apps.commonrpc.core.all.message.CommonRpcResponse;

public abstract class AbstractRpcTcpServerHandler implements RpcServerHandler {

    public abstract CommonRpcResponse handleRequest(CommonRpcRequest request, int codecType, int procotolType);

}
