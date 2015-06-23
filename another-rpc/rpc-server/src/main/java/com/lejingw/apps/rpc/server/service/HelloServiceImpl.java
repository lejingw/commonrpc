package com.lejingw.apps.rpc.server.service;

import com.lejingw.apps.rpc.annotation.RpcService;
import com.lejingw.apps.rpc.annotation.RpcService;
import com.lejingw.apps.rpc.service.HelloService;

@RpcService(HelloService.class) // 指定远程接口
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String name) {
        return "Hello! " + name;
    }
}