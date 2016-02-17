package com.jingcai.apps.commonrpc.tcp.spring.config.support;

import com.jingcai.apps.common.lang.string.StringUtils;
import com.jingcai.apps.commonrpc.core.filter.RpcFilter;
import com.jingcai.apps.commonrpc.tcp.netty4.server.CommonRpcTcpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class CommonRpcService implements InitializingBean {
	private static final Logger logger = LoggerFactory.getLogger(CommonRpcService.class);

	private Object ref;//服务类bean value
	private RpcFilter filterRef;//拦截器类
	private int codecType;//编码类型
	private String interfaceName;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (null == ref) {
			logger.error("could not found the ref class");
			return;
		}
		if(StringUtils.isEmpty(interfaceName)) {
			Class<?>[] interfaces = ref.getClass().getInterfaces();
			if (null == interfaces || 1 != interfaces.length) {
				logger.error("could not found the only one interface from class:" + ref.getClass());
				return;
			}
			interfaceName = interfaces[0].getName();
		}
		CommonRpcTcpServer.getInstance().registerProcessor(interfaceName, ref, filterRef, codecType);//filterRef 允许为null
	}

	public void setRef(Object ref) {
		this.ref = ref;
	}

	public void setFilterRef(RpcFilter filterRef) {
		this.filterRef = filterRef;
	}

	public void setCodecType(int codecType) {
		this.codecType = codecType;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
}
