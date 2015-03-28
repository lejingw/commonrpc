/**
 * 
 */
package com.cross.plateform.common.rpc.service.client.impl;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.data.Stat;

import com.cross.plateform.common.rpc.service.client.ICommonServiceClient;

/**
 * @author liubing
 *
 */
public class CommonServiceClientImpl implements ICommonServiceClient {
	
	private static Map<String, Set<InetSocketAddress>> servers=new ConcurrentHashMap<String, Set<InetSocketAddress>>();
	
	private ZooKeeper zk;
	
	public static final int TYPE = 0;
	

	/* (non-Javadoc)
	 * @see com.cross.plateform.common.rpc.service.client.ICommonServiceClient#getServersByGroup(java.lang.String)
	 */
	@Override
	public Set<InetSocketAddress> getServersByGroup(String group) throws Exception {
		// TODO Auto-generated method stub
		if(servers.containsKey(group)){
			return servers.get(group);
		}
		Set<InetSocketAddress> addresses=new HashSet<InetSocketAddress>();
		List<String> subList = zk.getChildren("/" + group, true);
		Stat stat = new Stat();
		if(subList!=null){
			for (String subNode : subList) {
				// 获取每个子节点下关联的server地址
				byte[] data = zk.getData("/" + group + "/" + subNode, false, stat);
				String server=new String(data, "utf-8");
				
				String[] host=server.split(":");
				InetSocketAddress socketAddress=new InetSocketAddress(host[0], Integer.parseInt(host[1]));
				addresses.add(socketAddress);
			}
			servers.put(group, addresses);
		}
		
		return addresses;
	}

	/* (non-Javadoc)
	 * @see com.cross.plateform.common.rpc.service.client.ICommonServiceClient#close()
	 */
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		servers.clear();
		zk.close();
	}

	/* (non-Javadoc)
	 * @see com.cross.plateform.common.rpc.service.client.ICommonServiceClient#connectZookeeper(java.lang.String, int)
	 */
	@Override
	public void connectZookeeper(String server, int timeout) throws Exception {
		// TODO Auto-generated method stub
		zk = new ZooKeeper(server, timeout, new Watcher() {
			
			public void process(WatchedEvent event) {
				// 如果发生了"/sgroup"节点下的子节点变化事件, 更新server列表, 并重新注册监听
				if (event.getType() == EventType.NodeChildrenChanged ) {
					try {
						updateServerList();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	private void updateServerList() throws Exception{
		Stat stat = new Stat();
		
		Map<String, Set<InetSocketAddress>> newservers=new HashMap<String, Set<InetSocketAddress>>();
		for(String group:servers.keySet()){
			Set<InetSocketAddress> addresses=new HashSet<InetSocketAddress>();
			List<String> subList = zk.getChildren("/" + group, true);
			if(subList!=null&&!subList.isEmpty()){
				for (String subNode : subList) {
					// 获取每个子节点下关联的server地址
					byte[] data = zk.getData("/" + group + "/" + subNode, false, stat);
					String server=new String(data, "utf-8");
					String[] host=server.split(":");
					InetSocketAddress socketAddress=new InetSocketAddress(host[0], Integer.parseInt(host[1]));
					addresses.add(socketAddress);
				}
				newservers.put(group, addresses);
				this.deleteServerNode(servers.get(group), addresses);
			}else{
				this.deleteServerNode(servers.get(group), addresses);
				servers.remove(group);
			}
			
		}
		servers.putAll(newservers);
	}
	
	private void deleteServerNode(Set<InetSocketAddress> allSets,Set<InetSocketAddress> sets) throws Exception{
		Set<InetSocketAddress> result = new HashSet<InetSocketAddress>();
		result.clear();
		result.addAll(allSets);
		result.removeAll(sets);
		for(InetSocketAddress address:result){
			String host=address.getHostString();
			int port=address.getPort();
			String path="/"+host+":"+port;
			zk.delete(path, -1);
		}
	}
	
}
