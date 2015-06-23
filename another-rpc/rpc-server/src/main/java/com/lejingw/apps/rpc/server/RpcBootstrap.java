package com.lejingw.apps.rpc.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RpcBootstrap {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring-context-server.xml");
    }
}