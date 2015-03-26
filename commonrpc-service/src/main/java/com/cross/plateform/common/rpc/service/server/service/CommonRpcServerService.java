/**
 * 
 */
package com.cross.plateform.common.rpc.service.server.service;

import com.cross.plateform.common.rpc.service.factory.CommonRpcServiceFactory;

/**
 * @author liubing
 *
 */
public class CommonRpcServerService {
	
	public CommonRpcServerService() {

	}

	private static class SingletonHolder {
		static final CommonRpcServerService instance = new CommonRpcServerService();
	}

	public static CommonRpcServerService getInstance() {
		return SingletonHolder.instance;
	}
	
	public void registerService(String group, String server) throws Exception{
		 CommonRpcServiceFactory.getCommonServiceServer().registerServer(group, server);
	}
	
	public void close() throws Exception{
		CommonRpcServiceFactory.getCommonServiceServer().close();
	}
	
	public void connectZookeeper(String server, int timeout) throws Exception{
		CommonRpcServiceFactory.getCommonServiceServer().connectZookeeper(server, timeout);
	}
	
	public void registerClient(String server, String client) throws Exception{
		CommonRpcServiceFactory.getCommonServiceServer().registerClient(server, client);
	}
}
