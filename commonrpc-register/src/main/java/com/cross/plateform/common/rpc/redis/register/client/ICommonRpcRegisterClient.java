package com.cross.plateform.common.rpc.redis.register.client;

import java.util.Set;

public interface ICommonRpcRegisterClient {
	
	public boolean registerClient(String group, String server);
	
	public void deleteRpcClient(String group, String server);
	
	public Set<String> getServersByGroup(String group);
	
	public void close();
}
