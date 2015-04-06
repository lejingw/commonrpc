/**
 * 
 */
package com.cross.plateform.common.rpc.service.client.impl;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.data.Stat;

import com.cross.plateform.common.rpc.service.client.ICommonServiceClient;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;

/**
 * @author liubing
 *
 */
public class CommonServiceClientImpl implements ICommonServiceClient {
	
	private static Map<String, Set<InetSocketAddress>> servers=new ConcurrentHashMap<String, Set<InetSocketAddress>>();
	
	private static final Log LOGGER = LogFactory.getLog(CommonServiceClientImpl.class);
	
	/**
	 * 客户端
	 */
	private  CuratorFramework client;
	
	public static final int TYPE = 0;
	
	private ConcurrentHashMap<String, Boolean> flag=new ConcurrentHashMap<String, Boolean>();
	/* (non-Javadoc)
	 * @see com.cross.plateform.common.rpc.service.client.ICommonServiceClient#getServersByGroup(java.lang.String)
	 */
	@Override
	public Set<InetSocketAddress> getServersByGroup(final String group) throws Exception {
		// TODO Auto-generated method stub
		if(!flag.containsKey(group)){
			
		     ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);
			 
			 @SuppressWarnings("resource")
			 PathChildrenCache childrenCache = new PathChildrenCache(client,"/"+group, true);
		     childrenCache.start(StartMode.POST_INITIALIZED_EVENT);
		     childrenCache.getListenable().addListener(
		            new PathChildrenCacheListener() {
		                @Override
		                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
		                        throws Exception {
		                    if(event.getType()==PathChildrenCacheEvent.Type.CHILD_REMOVED){//监听子节点被删除的情况
		                   
		                    	String path=event.getData().getPath();
		                    	String[] nodes=path.split("/");// 1:group 2:address
		                    	if(nodes.length>0&&nodes.length==3){
		                    		updateServerList(nodes[1], nodes[2]);
		                    	}
		                    	
		                    }else if(event.getType()==PathChildrenCacheEvent.Type.CHILD_ADDED){//监听增加
		                    	String path=event.getData().getPath();
		                    	String[] nodes=path.split("/");// 1:group 2:address
		                    	if(nodes.length>0&&nodes.length==3){
		                    		Map<String, String> valueMap=listChildrenDetail(path);
		                    		for(String value:valueMap.values()){
		                    			String[] nodes1=value.split("/");
		                    			InetSocketAddress socketAddress=new InetSocketAddress(nodes1[0], Integer.parseInt(nodes1[1]));
			            				Set<InetSocketAddress> addresses=servers.get(group);
			            				addresses.add(socketAddress);
			            				servers.put(group, addresses);
		                    		}
		                    		
		            				
		                    	}
		                    }
		                }
		            },
		            pool
		        );
		     flag.put(group, true);
		}
		
		if(servers.containsKey(group)){
			return servers.get(group);
		}
		Set<InetSocketAddress> addresses=new HashSet<InetSocketAddress>();
		Map<String, String> maps=listChildrenDetail("/"+group);
		if(maps!=null&&maps.values().size()>0){
			for(String value:maps.values()){
				String[] host=value.split(":");
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
		client.close();
	}

	/* (non-Javadoc)
	 * @see com.cross.plateform.common.rpc.service.client.ICommonServiceClient#connectZookeeper(java.lang.String, int)
	 */
	@Override
	public void connectZookeeper(final String server, final int timeout) throws Exception {
		
		client = CuratorFrameworkFactory.builder()
	            .connectString(server)
	            .sessionTimeoutMs(timeout)
	            .connectionTimeoutMs(timeout)
	            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
	            .build();
	    client.start();
	    
	     
	}
	/**
	 * 更新本地化的server
	 * @param group
	 * @param server
	 * @throws Exception
	 */
	private void updateServerList(String group,String server) throws Exception{
		if(servers.containsKey(group)){
			Set<InetSocketAddress> rpcservers=servers.get(group);
			Set<InetSocketAddress> newrpcservers=new HashSet<InetSocketAddress>();
			for(InetSocketAddress socketAddress:rpcservers){
				String server1=socketAddress.getAddress().toString()+":"+socketAddress.getPort();
				String server2=server1.substring(1, server1.length());
				if(!server.startsWith(server2)){//更新不包括
					newrpcservers.add(socketAddress);
				}else{//删除包括
					deleteNode(server1);
				}
			}
			servers.put(group, newrpcservers);
		}
	}

	/**
     * 找到指定节点下所有子节点的名称与值
     * 
     * @param node
     * @return
     */
    private Map<String, String> listChildrenDetail(String node) {
        Map<String, String> map = Maps.newHashMap();
        try {
            GetChildrenBuilder childrenBuilder = getClient().getChildren();
            List<String> children = childrenBuilder.forPath(node);
            GetDataBuilder dataBuilder = getClient().getData();
            if (children != null) {
                for (String child : children) {
                    String propPath = ZKPaths.makePath(node, child);
                    map.put(child, new String(dataBuilder.forPath(propPath), Charsets.UTF_8));
                }
            }
        }
        catch (Exception e) {
            LOGGER.error("listChildrenDetail fail",e);
        }
        return map;
    }
	
	/**
	 * 删除节点
	 * @param path
	 * @throws Exception
	 */
	private  void deleteNode(String path) throws Exception {
		// TODO Auto-generated method stub
		try {
			Stat stat = getClient().checkExists().forPath(path);
			if (stat != null) {
				 getClient().delete().deletingChildrenIfNeeded().forPath(path);
			}
        }catch (Exception e) {
        	//LOGGER.error("deleteNode fail", e);
        }
	}
	
	/**
	 * @return the client
	 */
	public CuratorFramework getClient() {
		return client;
	}

	/**
	 * @param client the client to set
	 */
	public void setClient(CuratorFramework client) {
		this.client = client;
	}
	
}
