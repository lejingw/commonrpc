/**
 * 
 */
package com.cross.plateform.common.rpc.service.factory;

import com.cross.plateform.common.rpc.service.client.ICommonServiceClient;
import com.cross.plateform.common.rpc.service.client.impl.CommonServiceClientImpl;
import com.cross.plateform.common.rpc.service.server.ICommonServiceServer;
import com.cross.plateform.common.rpc.service.server.impl.CommonServiceServerImpl;

/**
 * @author liubing
 *
 */
public class CommonRpcServiceFactory {
	
	private static ICommonServiceServer[] serverHandlers = new ICommonServiceServer[1];
	
	private static ICommonServiceClient[] clientHandlers = new ICommonServiceClient[1];

	static{
		registerProtocol(CommonServiceServerImpl.TYPE, new CommonServiceServerImpl(),CommonServiceClientImpl.TYPE,new CommonServiceClientImpl());
	}
	
	private static void registerProtocol(int type,ICommonServiceServer serverHandler,int httptype,ICommonServiceClient clientHandler){
		if(type > serverHandlers.length){
			ICommonServiceServer[] newServerHandlers = new ICommonServiceServer[type + 1];
			System.arraycopy(serverHandlers, 0, newServerHandlers, 0, serverHandlers.length);
			serverHandlers = newServerHandlers;
		}
		serverHandlers[type] = serverHandler;
		
		if(httptype > clientHandlers.length){
			ICommonServiceClient[] newServerHandlers = new ICommonServiceClient[type + 1];
			System.arraycopy(serverHandlers, 0, newServerHandlers, 0, serverHandlers.length);
			clientHandlers = newServerHandlers;
		}
		clientHandlers[type] = clientHandler;
	}
	
	public static ICommonServiceServer getCommonServiceServer(){
		return serverHandlers[0];
	}
	
	public static ICommonServiceClient getCommonServiceClient(){
		return clientHandlers[0];
	}
}
