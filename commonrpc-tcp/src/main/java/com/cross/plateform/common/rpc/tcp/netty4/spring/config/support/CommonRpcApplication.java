package com.cross.plateform.common.rpc.tcp.netty4.spring.config.support;

import com.cross.plateform.common.rpc.core.util.StringUtils;
import com.cross.plateform.common.rpc.service.client.service.CommonRpcClientService;
import com.cross.plateform.common.rpc.service.server.service.CommonRpcServerService;
import com.cross.plateform.common.rpc.tcp.netty4.client.factory.CommonRpcTcpClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class CommonRpcApplication implements InitializingBean, DisposableBean {
	private static final Logger logger = LoggerFactory.getLogger(CommonRpcApplication.class);
	private String address = null;
	private boolean providerFlag;
	private int timeout;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (StringUtils.isNullOrEmpty(address)) {
			throw new RuntimeException("parameter 'address can not be null or empty");
		}
		if (timeout <= 0) {
			throw new RuntimeException("parameter 'timeout' should be positive");
		}
		if (providerFlag) {//服务端
			CommonRpcServerService.getInstance().connectZookeeper(address, timeout);
		} else {//客户端
			CommonRpcClientService.getInstance().connectZookeeper(address, timeout);
			CommonRpcTcpClientFactory.getInstance().startClientFactory(timeout);//客户端启动
		}
	}

	@Override
	public void destroy() throws Exception {
		if (providerFlag) {
			CommonRpcServerService.getInstance().close();
		} else {
			CommonRpcClientService.getInstance().close();
			CommonRpcTcpClientFactory.getInstance().stopClientFactory();
		}
		logger.info("CommonRpcApplication has been closed !!!");
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setProviderFlag(boolean providerFlag) {
		this.providerFlag = providerFlag;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
}
