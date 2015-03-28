/**
 * 
 */
package com.cross.plateform.common.rpc.service.server.impl;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;

import com.cross.plateform.common.rpc.service.server.ICommonServiceServer;

/**
 * @author liubing
 *
 */
public class CommonServiceServerImpl implements ICommonServiceServer {
	
	private  ZooKeeper zk;
	
	public static final int TYPE = 0;
	/* (non-Javadoc)
	 * @see com.cross.plateform.common.rpc.service.server.ICommonServiceServer#close()
	 */
	@Override
	public void close() throws Exception{
		// TODO Auto-generated method stub
		zk.close();
	}

	/* (non-Javadoc)
	 * @see com.cross.plateform.common.rpc.service.server.ICommonServiceServer#connectZookeeper(java.lang.String, int)
	 */
	@Override
	public void connectZookeeper(String server, int timeout) throws Exception {
		// TODO Auto-generated method stub
		zk = new ZooKeeper(server, timeout, new Watcher() {
			public void process(WatchedEvent event) {
				// 不做处理
			}
		});
		
	}

	/* (non-Javadoc)
	 * @see com.cross.plateform.common.rpc.service.server.ICommonServiceServer#registerServer(java.lang.String, java.lang.String)
	 */
	@Override
	public void registerServer(String group, String server) throws Exception{
		// TODO Auto-generated method stub
		if(zk.exists("/"+group, true) == null){
			zk.create("/" + group, group.getBytes("utf-8"), 
					Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		
		zk.create("/" + group + "/"+server , server.getBytes("utf-8"), 
				Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
	}
	
	/* (non-Javadoc)
	 * @see com.cross.plateform.common.rpc.service.client.ICommonServiceClient#registerClient(java.lang.String, java.lang.String)
	 */
	@Override
	public void registerClient(String server, String client) throws Exception {
		// TODO Auto-generated method stub
		if(zk.exists("/"+server, true) == null){
			
			zk.create("/" + server, server.getBytes("utf-8"), 
					Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		
		zk.create("/" + server +client , client.getBytes("utf-8"), 
				Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
	}
}
