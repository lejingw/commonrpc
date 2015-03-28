package com.cross.plateform.common.rpc.zk.manager.client.factory;

import org.springframework.beans.factory.FactoryBean;

import com.cross.plateform.common.rpc.zk.manager.client.CommonRpcManagerClient;

public class CommonRpcManagerClientFactory implements FactoryBean {
	
	private String host;
	
	private int timeout;
	
	@Override
	public Object getObject() throws Exception {
		// TODO Auto-generated method stub
		if(host==null){
			throw new RuntimeException("host can not be null");
		}
		CommonRpcManagerClient commonRpcManagerClient=new CommonRpcManagerClient(host, timeout);
		commonRpcManagerClient.connectServer();
		return commonRpcManagerClient;
	}

	@Override
	public Class<?> getObjectType() {
		// TODO Auto-generated method stub
		return CommonRpcManagerClient.class;
	}

	@Override
	public boolean isSingleton() {
		// TODO Auto-generated method stub
		return true;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	
}
