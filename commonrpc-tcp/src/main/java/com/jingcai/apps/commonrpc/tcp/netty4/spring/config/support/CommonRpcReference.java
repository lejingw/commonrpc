package com.jingcai.apps.commonrpc.tcp.netty4.spring.config.support;

import com.jingcai.apps.commonrpc.tcp.netty4.client.invocation.CommonRpcTcpClientInvocationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

public class CommonRpcReference implements FactoryBean {
	private static final Logger logger = LoggerFactory.getLogger(CommonRpcReference.class);

	private Class intfType;//接口名称
	private int timeout;//超时时间
	private int codecType;//编码类型
	private int protocolType;//协议类型
	private String group;//组名，如果属于多个组，则用逗号分隔

	@Override
	public Object getObject() throws Exception {
		return Proxy.newProxyInstance(
				Thread.currentThread().getContextClassLoader(),
				new Class[]{intfType},
				new CommonRpcTcpClientInvocationHandler(group, timeout, getObjectType().getName(), codecType, protocolType));
	}

	@Override
	public Class<?> getObjectType() {
		return intfType;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setIntfType(Class intfType) {
		this.intfType = intfType;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setCodecType(int codecType) {
		this.codecType = codecType;
	}

	public void setProtocolType(int protocolType) {
		this.protocolType = protocolType;
	}

	public void setGroup(String group) {
		this.group = group;
	}
}
