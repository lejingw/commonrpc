package com.cross.plateform.common.rpc.tcp.netty4.spring.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class CommonRpcNamespaceHandler extends NamespaceHandlerSupport {
	@Override
	public void init() {
		registerBeanDefinitionParser("reference", new CommonRpcReferenceParser());
		registerBeanDefinitionParser("service", new CommonRpcServiceParser());
		registerBeanDefinitionParser("registry", new CommonRpcRegisteryParser());
		registerBeanDefinitionParser("application", new CommonRpcApplicationParser());
	}
}
