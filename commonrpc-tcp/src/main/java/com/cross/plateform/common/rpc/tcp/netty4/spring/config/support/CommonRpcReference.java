/**
 * 
 */
package com.cross.plateform.common.rpc.tcp.netty4.spring.config.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

import com.cross.plateform.common.rpc.core.util.StringUtils;
import com.cross.plateform.common.rpc.service.client.service.CommonRpcClientService;
import com.cross.plateform.common.rpc.tcp.netty4.client.factory.CommonRpcTcpClientFactory;
import com.cross.plateform.common.rpc.tcp.netty4.client.proxy.CommonRpcTcpClientProxy;
/**
 * @author liubing1
 *
 */
public class CommonRpcReference implements FactoryBean, DisposableBean {
	
	/**
	 * 接口名称
	 */
	private String interfacename;
	
	/**
	 * 超时时间
	 */
	private int timeout;
	
	/**
	 * 编码类型
	 */
	private int codecType;
	/**
	 * 协议类型
	 */
	private int protocolType;
	
	/**
	 * 组名
	 */
	private String group;
	
	private static final Log LOGGER = LogFactory.getLog(CommonRpcReference.class);
	

	@Override
	public void destroy() throws Exception {
		CommonRpcClientService.getInstance().close();
		CommonRpcTcpClientFactory.getInstance().stopClient();
	}

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
