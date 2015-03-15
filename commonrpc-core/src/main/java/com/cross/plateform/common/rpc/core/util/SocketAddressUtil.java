package com.cross.plateform.common.rpc.core.util;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.cross.plateform.common.rpc.core.route.bean.RpcRouteServer;

public class SocketAddressUtil {
	
	public static List<RpcRouteServer> getInetSocketAddress(Set<InetSocketAddress>addresses){
		List<RpcRouteServer> routeServers=new ArrayList<RpcRouteServer>();
		
		for(InetSocketAddress inetSocketAddress:addresses){
			RpcRouteServer rpcRouteServer=new RpcRouteServer(inetSocketAddress, 1);
			routeServers.add(rpcRouteServer);
		}
		return routeServers;
	}
}
