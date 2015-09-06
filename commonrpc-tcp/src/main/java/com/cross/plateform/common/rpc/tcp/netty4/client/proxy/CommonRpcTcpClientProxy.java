package com.cross.plateform.common.rpc.tcp.netty4.client.proxy;

import java.lang.reflect.Proxy;

import com.cross.plateform.common.rpc.client.proxy.ClientProxy;
import com.cross.plateform.common.rpc.tcp.netty4.client.invocation.CommonRpcTcpClientInvocationHandler;

public class CommonRpcTcpClientProxy implements ClientProxy {

    public CommonRpcTcpClientProxy() {
    }

    private static class SingletonHolder {
        static final CommonRpcTcpClientProxy instance = new CommonRpcTcpClientProxy();
    }

    public static CommonRpcTcpClientProxy getInstance() {
        return SingletonHolder.instance;
    }

    @Override
    public <T> T getProxyService(Class<T> clazz, int timeout, int codecType, int protocolType, String targetInstanceName, String group) {
        return (T) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{clazz},
                new CommonRpcTcpClientInvocationHandler(group, timeout, targetInstanceName, codecType, protocolType));
    }
}
