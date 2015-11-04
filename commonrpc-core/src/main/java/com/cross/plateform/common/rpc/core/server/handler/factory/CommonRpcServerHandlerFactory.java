package com.cross.plateform.common.rpc.core.server.handler.factory;

import com.cross.plateform.common.rpc.core.server.handler.AbstractRpcTcpServerHandler;
import com.cross.plateform.common.rpc.core.server.handler.impl.RpcTcpServerHandlerImpl;

public class CommonRpcServerHandlerFactory {

	private static AbstractRpcTcpServerHandler[] serverHandlers = new AbstractRpcTcpServerHandler[1];

	static {
		registerProtocol(RpcTcpServerHandlerImpl.TYPE, new RpcTcpServerHandlerImpl());
	}

	private static void registerProtocol(int type, AbstractRpcTcpServerHandler customServerHandler) {
		if (type >= serverHandlers.length) {
			AbstractRpcTcpServerHandler[] newServerHandlers = new AbstractRpcTcpServerHandler[type + 1];
			System.arraycopy(serverHandlers, 0, newServerHandlers, 0, serverHandlers.length);
			serverHandlers = newServerHandlers;
		}
		serverHandlers[type] = customServerHandler;
	}

	public static AbstractRpcTcpServerHandler getServerHandler() {
		return serverHandlers[0];
	}
}
