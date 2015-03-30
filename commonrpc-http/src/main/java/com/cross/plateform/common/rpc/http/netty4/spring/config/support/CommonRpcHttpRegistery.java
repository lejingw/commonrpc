/**
 * 
 */
package com.cross.plateform.common.rpc.http.netty4.spring.config.support;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.cross.plateform.common.rpc.http.netty4.server.CommonRpcHttpServer;

/**
 * @author liubing1
 *
 */
public class CommonRpcHttpRegistery implements InitializingBean, DisposableBean {
	
	private int port;//端口号
	
	private int timeout;
	/**
	 * 1:java 多线程
	 * 2：disruptor
	 */
	private int handType;
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		CommonRpcHttpServer.getInstance().stop();
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		CommonRpcHttpServer.getInstance().setHandType(handType);
		CommonRpcHttpServer.getInstance().start(port, timeout);
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
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

	public int getHandType() {
		return handType;
	}

	public void setHandType(int handType) {
		this.handType = handType;
	}
	
	
}
