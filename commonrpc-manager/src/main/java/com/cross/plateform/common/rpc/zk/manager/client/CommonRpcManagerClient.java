/**
 * 
 */
package com.cross.plateform.common.rpc.zk.manager.client;

import java.util.ArrayList;
import java.util.List;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
/**
 * @author liubing
 *
 */
public class CommonRpcManagerClient {
  
	private String host;
	
	private int timeout;
	
	private  ZooKeeper zk;
	
	public CommonRpcManagerClient(String host, int timeout) {
		super();
		this.host = host;
		this.timeout = timeout;
		
	}
	
	public void connectServer() throws Exception{
		zk = new ZooKeeper(host, timeout, new Watcher() {
			public void process(WatchedEvent event) {
				// 不做处理
			}
		});
	}
	
	/**
	 * 根据组获取服务名称
	 * @param group
	 * @return
	 * @throws Exception
	 */
	public List<String> getServersByGroup(String group) throws Exception{
		List<String> result=new ArrayList<String>();
		List<String> subList = zk.getChildren("/" + group, true);
		Stat stat = new Stat();
		if(subList!=null){
			for (String subNode : subList) {
				// 获取每个子节点下关联的server地址
				byte[] data = zk.getData("/" + group + "/" + subNode, false, stat);
				String server=new String(data, "utf-8");
				
				result.add(server);
			}
		}
		return result;
	}
	
	/**
	 * 根据server  获取对应client
	 * @param server
	 * @return
	 * @throws Exception
	 */
	public List<String> getClientsByServer(String server) throws Exception{
		List<String> result=new ArrayList<String>();
		List<String> subList = zk.getChildren("/" + server, true);
		Stat stat = new Stat();
		if(subList!=null){
			for (String subNode : subList) {
				// 获取每个子节点下关联的server地址
				byte[] data = zk.getData("/" + server + "/" + subNode, false, stat);
				String client=new String(data, "utf-8");
				
				result.add(client);
			}
		}
		return result;
	}
	
	
}
