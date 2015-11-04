package com.cross.plateform.common.rpc.tcp.netty4.spring.config.support;

import com.cross.plateform.common.rpc.core.util.StringUtils;
import com.cross.plateform.common.rpc.service.server.service.CommonRpcServerService;
import com.cross.plateform.common.rpc.tcp.netty4.server.CommonRpcTcpServer;
import com.cross.plateform.common.rpc.tcp.netty4.util.NetworkKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class CommonRpcRegistery implements InitializingBean, DisposableBean {
	private static final Logger logger = LoggerFactory.getLogger(CommonRpcRegistery.class);
	private String ip;//暴露的ip
	private int port;//端口号
	private int timeout;
	private int procotolType;//协议名称
	private int codecType;//编码类型
	private String group;//组
	private int threadCount;//线程数

	@Override
	public void destroy() throws Exception {
		CommonRpcTcpServer.getInstance().stop();//停止
		logger.debug("CommonRpcRegistery has been destroyed !!!");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (port == 0) {
			throw new Exception("parameter port can not be null");
		}
		CommonRpcServerService.getInstance().registerService(group, getLocalhost() + ":" + port);
		CommonRpcTcpServer.getInstance().setCodecType(codecType);
		CommonRpcTcpServer.getInstance().setProcotolType(procotolType);
		CommonRpcTcpServer.getInstance().setThreadCount(threadCount);
		CommonRpcTcpServer.getInstance().start(group, getLocalhost(), port, timeout);
	}

	private String getLocalhost() {
		return StringUtils.isNullOrEmpty(ip) ? NetworkKit.getLocalIp() : ip;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setProcotolType(int procotolType) {
		this.procotolType = procotolType;
	}

	public void setCodecType(int codecType) {
		this.codecType = codecType;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}
}
