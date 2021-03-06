package com.jingcai.apps.commonrpc.core.route;

import com.jingcai.apps.commonrpc.core.route.bean.RpcRouteServer;

import java.util.List;

public class CommonRpcRoute {
	public static RpcRouteServer getBestServer(List<RpcRouteServer> serverList) {
		RpcRouteServer server = null;
		RpcRouteServer best = null;
		int total = 0;
		for (int i = 0, len = serverList.size(); i < len; i++) {
			//当前服务器对象
			server = serverList.get(i);
			//当前服务器已宕机，排除
			if (server.down) {
				continue;
			}
			server.currentWeight += server.effectiveWeight;
			total += server.effectiveWeight;
			if (server.effectiveWeight < server.weight) {
				server.effectiveWeight++;
			}
			if (best == null || server.currentWeight > best.currentWeight) {
				best = server;
			}
		}
		if (best == null) {
			return null;
		}
		best.currentWeight -= total;
		return best;
	}
}