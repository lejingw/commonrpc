/**
 * 
 */
package com.cross.plateform.common.rpc.redis.register.factory;

import com.cross.plateform.common.rpc.redis.register.client.ICommonRpcRegisterClient;
import com.cross.plateform.common.rpc.redis.register.client.impl.CommonRpcRegisterClientImpl;
import com.cross.plateform.common.rpc.redis.register.server.ICommonRpcRegisterServer;
import com.cross.plateform.common.rpc.redis.register.server.impl.CommonRpcRegisterServerImpl;


/**
 * @author liubing1
 *
 */
public class CommonRpcRegisterFactory {
	
	private static ICommonRpcRegisterServer[] serverHandlers = new ICommonRpcRegisterServer[1];
	
	private static ICommonRpcRegisterClient[] clientHandlers = new ICommonRpcRegisterClient[1];

	static{
		registerProtocol(CommonRpcRegisterServerImpl.TYPE, new CommonRpcRegisterServerImpl(),CommonRpcRegisterClientImpl.TYPE,new CommonRpcRegisterClientImpl());
	}
	
	private static void registerProtocol(int type,ICommonRpcRegisterServer serverHandler,int httptype,ICommonRpcRegisterClient clientHandler){
		if(type > serverHandlers.length){
			ICommonRpcRegisterServer[] newServerHandlers = new ICommonRpcRegisterServer[type + 1];
			System.arraycopy(serverHandlers, 0, newServerHandlers, 0, serverHandlers.length);
			serverHandlers = newServerHandlers;
		}
		serverHandlers[type] = serverHandler;
		
		if(httptype > clientHandlers.length){
			ICommonRpcRegisterClient[] newServerHandlers = new ICommonRpcRegisterClient[type + 1];
			System.arraycopy(serverHandlers, 0, newServerHandlers, 0, serverHandlers.length);
			clientHandlers = newServerHandlers;
		}
		clientHandlers[type] = clientHandler;
	}
	
	public static ICommonRpcRegisterServer getCommonRpcRegisterServer(){
		return serverHandlers[0];
	}
	
	public static ICommonRpcRegisterClient getCommonRpcRegisterClient(){
		return clientHandlers[0];
	}
}
