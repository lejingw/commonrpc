/**
 * 
 */
package com.cross.plateform.common.rpc.redis.register.server.service;

import com.cross.plateform.common.rpc.redis.register.factory.CommonRpcRegisterFactory;

/**
 * @author liubing1
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
	
	public boolean registerService(String group, String server){
		return CommonRpcRegisterFactory.getCommonRpcRegisterServer().registerService(group, server);
	}
	
	public void deleteRpcServiceServer(String group, String serverIp) {
		CommonRpcRegisterFactory.getCommonRpcRegisterServer().close();
		CommonRpcRegisterFactory.getCommonRpcRegisterServer().deleteRpcServiceServer(group, serverIp);
	}
}
