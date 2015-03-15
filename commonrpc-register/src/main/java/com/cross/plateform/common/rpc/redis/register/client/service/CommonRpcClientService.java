package com.cross.plateform.common.rpc.redis.register.client.service;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.cross.plateform.common.rpc.redis.register.factory.CommonRpcRegisterFactory;


public class CommonRpcClientService {
	
	private static Map<String, Set<InetSocketAddress>> servers=new ConcurrentHashMap<String, Set<InetSocketAddress>>();
	
	public CommonRpcClientService() {

	}

	private static class SingletonHolder {
		static final CommonRpcClientService instance = new CommonRpcClientService();
	}

	public static CommonRpcClientService getInstance() {
		return SingletonHolder.instance;
	}
	
	public Set<InetSocketAddress> getServersByGroup(String group){
		
		if(servers.containsKey(group)){
			
			return servers.get(group);
		}else{
			Set<InetSocketAddress> addresses=new HashSet<InetSocketAddress>();
			Set<String> strings=CommonRpcRegisterFactory.getCommonRpcRegisterClient().getServersByGroup(group);
			for(String server:strings){
				String[] host=server.split(":");
				InetSocketAddress socketAddress=new InetSocketAddress(host[0], Integer.parseInt(host[1]));
				addresses.add(socketAddress);
			}
			servers.put(group, addresses);
			
			return addresses;
		}
	}
	
	public void removeCommonRpcServer(String group, String server){
		if(servers.containsKey(group)){
			Set<InetSocketAddress> addresses=servers.get(group);
			for(InetSocketAddress address:addresses){
				String server1=address.getHostName()+":"+address.getPort();
				if(server1.equals(server)){
					servers.remove(address);
					break;
				}
			}
		}
	}
	
	public void registerClient(String group, String server){
		
		server=server.substring(1, server.length());
		CommonRpcRegisterFactory.getCommonRpcRegisterClient().registerClient(group, server);
	}
	
	
	public void deleteRpcClient(String group, String server){
		
		server=server.substring(1, server.length());
		CommonRpcRegisterFactory.getCommonRpcRegisterClient().deleteRpcClient(group, server);
	}
	
	public void removeAll(){
		CommonRpcRegisterFactory.getCommonRpcRegisterClient().close();
		servers.clear();
	}
	
	public void putRpcServers(String group,Set<String> serverIps){
		Set<InetSocketAddress> addresses=new HashSet<InetSocketAddress>();
		for(String server:serverIps){
			String[] host=server.split(":");
			InetSocketAddress socketAddress=new InetSocketAddress(host[0], Integer.parseInt(host[1]));
			addresses.add(socketAddress);
		}
		servers.put(group, addresses);
	}
}
