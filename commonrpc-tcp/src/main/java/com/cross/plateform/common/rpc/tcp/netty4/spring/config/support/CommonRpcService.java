package com.cross.plateform.common.rpc.tcp.netty4.spring.config.support;

import com.cross.plateform.common.rpc.core.filter.RpcFilter;
import com.cross.plateform.common.rpc.tcp.netty4.server.CommonRpcTcpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class CommonRpcService implements InitializingBean {
	private static final Logger logger = LoggerFactory.getLogger(CommonRpcService.class);

	private Object ref;//服务类bean value
	private RpcFilter filterRef;//拦截器类

	@Override
	public void afterPropertiesSet() throws Exception {
		if (null == ref) {
			logger.error("could not found the ref class");
			return;
		}
		Class<?>[] interfaces = ref.getClass().getInterfaces();
		if (null == interfaces || 1 != interfaces.length) {
			logger.error("could not found the only one interface from class:" + ref.getClass());
			return;
		}
		String interfacename = interfaces[0].getName();
		CommonRpcTcpServer.getInstance().registerProcessor(interfacename, ref, filterRef);//filterRef 允许为null
	}

	public void setRef(Object ref) {
		this.ref = ref;
	}

	public void setFilterRef(RpcFilter filterRef) {
		this.filterRef = filterRef;
	}
}
