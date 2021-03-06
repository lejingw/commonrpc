package com.jingcai.apps.commonrpc.core.route.bean;

import java.io.Serializable;
import java.net.InetSocketAddress;

/**
 * 权重轮询server
 */
public class RpcRouteServer implements Serializable {
	private static final long serialVersionUID = -5830467756387170272L;

	public InetSocketAddress server;
	public int weight;
	public int effectiveWeight;
	public int currentWeight;
	public boolean down = false;

	public RpcRouteServer(InetSocketAddress server, int weight) {
		this.server = server;
		this.weight = weight;
		this.effectiveWeight = this.weight;
		this.currentWeight = 0;
		this.down = this.weight < 0;
	}

	public InetSocketAddress getServer() {
		return server;
	}

	public void setServer(InetSocketAddress server) {
		this.server = server;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getEffectiveWeight() {
		return effectiveWeight;
	}

	public void setEffectiveWeight(int effectiveWeight) {
		this.effectiveWeight = effectiveWeight;
	}

	public int getCurrentWeight() {
		return currentWeight;
	}

	public void setCurrentWeight(int currentWeight) {
		this.currentWeight = currentWeight;
	}

	public boolean isDown() {
		return down;
	}

	public void setDown(boolean down) {
		this.down = down;
	}
}