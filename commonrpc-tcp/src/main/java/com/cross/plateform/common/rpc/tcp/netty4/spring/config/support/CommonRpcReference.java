package com.cross.plateform.common.rpc.tcp.netty4.spring.config.support;

import com.cross.plateform.common.rpc.core.util.StringUtils;
import com.cross.plateform.common.rpc.tcp.netty4.client.proxy.CommonRpcTcpClientProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

public class CommonRpcReference implements FactoryBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommonRpcReference.class);
	private String interfacename;//接口名称
	private int timeout;//超时时间
	private int codecType;//编码类型
	private int protocolType;//协议类型
	private String group;//组名，如果属于多个组，则用逗号分隔

	@Override
	public Object getObject() throws Exception {
		return CommonRpcTcpClientProxy.getInstance().getProxyService(getObjectType(), timeout, codecType, protocolType, getObjectType().getName(), group);
	}

	@Override
	public Class<?> getObjectType() {
		try {
			if (StringUtils.isNullOrEmpty(interfacename)){
				LOGGER.warn("interfacename is null");
				return null;
			} else {
				return Thread.currentThread().getContextClassLoader().loadClass(interfacename);
			}
		} catch (ClassNotFoundException e) {
			LOGGER.error("spring 解析失败", e);
		}
		return null;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public String getInterfacename() {
		return interfacename;
	}

	public void setInterfacename(String interfacename) {
		this.interfacename = interfacename;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getCodecType() {
		return codecType;
	}

	public void setCodecType(int codecType) {
		this.codecType = codecType;
	}

	public int getProtocolType() {
		return protocolType;
	}

	public void setProtocolType(int protocolType) {
		this.protocolType = protocolType;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
}
