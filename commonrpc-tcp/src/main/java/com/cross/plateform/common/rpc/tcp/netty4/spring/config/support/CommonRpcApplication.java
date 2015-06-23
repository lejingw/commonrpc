/**
 * 
 */
package com.cross.plateform.common.rpc.tcp.netty4.spring.config.support;


import org.springframework.beans.factory.InitializingBean;
import com.cross.plateform.common.rpc.core.util.StringUtils;
import com.cross.plateform.common.rpc.service.client.service.CommonRpcClientService;
import com.cross.plateform.common.rpc.service.server.service.CommonRpcServerService;
import com.cross.plateform.common.rpc.tcp.netty4.client.factory.CommonRpcTcpClientFactory;

/**
 * @author liubing1
 *
 */
public class CommonRpcApplication implements InitializingBean{

	private String address = null;
	
	private String clientid = null;
	
	/**
	 * server :1
	 * client :2
	 */
	private Integer flag;
	
	private int timeout;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		if(StringUtils.isNullOrEmpty(address)){
			throw new RuntimeException("address   can not be null or empty");
		}
		if(StringUtils.isNullOrEmpty(flag)){
			throw new RuntimeException("flag   can not be null or empty");
		}
		if(flag!=1&&flag!=2){
			throw new RuntimeException("flag only be 1 or 2");
		}
		
		
		
		if(flag==1){//服务端
			CommonRpcServerService.getInstance().connectZookeeper(address, timeout);
		}else if(flag==2){//客户端
			CommonRpcClientService.getInstance().connectZookeeper(address, timeout);
			CommonRpcTcpClientFactory.getInstance().startClient(timeout);//客户端启动
		}
		
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
	 * @param flag the flag to set
	 */
	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public String getClientid()
	{
		return clientid;
	}

	public void setClientid(String clientid)
	{
		this.clientid = clientid;
	}
	
	
}
