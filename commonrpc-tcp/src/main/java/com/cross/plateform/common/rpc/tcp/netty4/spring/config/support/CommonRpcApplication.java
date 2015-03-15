/**
 * 
 */
package com.cross.plateform.common.rpc.tcp.netty4.spring.config.support;

import org.springframework.beans.factory.InitializingBean;

import com.cross.plateform.common.rpc.core.util.StringUtils;
import com.cross.plateform.common.rpc.redis.register.CommonRpcRegister;

/**
 * @author liubing1
 *
 */
public class CommonRpcApplication implements InitializingBean{
	
	private String address;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		if(StringUtils.isNullOrEmpty(address)){
			throw new RuntimeException("address can not be null or empty");
		}
		CommonRpcRegister.getInstance().initServer(address);
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	
	
}
