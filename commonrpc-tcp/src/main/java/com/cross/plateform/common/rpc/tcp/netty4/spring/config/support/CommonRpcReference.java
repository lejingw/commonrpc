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
public class CommonRpcReference implements FactoryBean,
		DisposableBean {
	
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
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		CommonRpcClientService.getInstance().close();
		CommonRpcTcpClientFactory.getInstance().stopClient();
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public Object getObject() throws Exception {
		// TODO Auto-generated method stub
		return CommonRpcTcpClientProxy.getInstance().getProxyService(getObjectType(), timeout, codecType, protocolType, getObjectType().getName(), group);
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<?> getObjectType() {
		// TODO Auto-generated method stub
		try {
			if (StringUtils.isNullOrEmpty(interfacename)){
				LOGGER.warn("interfacename is null");
				return null;
			} else {
				return Thread.currentThread().getContextClassLoader().loadClass(interfacename);
			}
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			LOGGER.error("spring 解析失败", e);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	@Override
	public boolean isSingleton() {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * @return the interfacename
	 */
	public String getInterfacename() {
		return interfacename;
	}

	/**
	 * @param interfacename the interfacename to set
	 */
	public void setInterfacename(String interfacename) {
		this.interfacename = interfacename;
	}

	/**
	 * @return the timeout
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return the codecType
	 */
	public int getCodecType() {
		return codecType;
	}

	/**
	 * @param codecType the codecType to set
	 */
	public void setCodecType(int codecType) {
		this.codecType = codecType;
	}

	/**
	 * @return the protocolType
	 */
	public int getProtocolType() {
		return protocolType;
	}

	/**
	 * @param protocolType the protocolType to set
	 */
	public void setProtocolType(int protocolType) {
		this.protocolType = protocolType;
	}

	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}
	
	
}
