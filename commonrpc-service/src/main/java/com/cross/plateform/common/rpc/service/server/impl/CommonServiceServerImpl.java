/**
 * 
 */
package com.cross.plateform.common.rpc.service.server.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import com.cross.plateform.common.rpc.service.server.ICommonServiceServer;
import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.base.Strings;

/**
 * @author liubing
 *
 */
public class CommonServiceServerImpl implements ICommonServiceServer {
	
	private  CuratorFramework client;
	
	public static final int TYPE = 0;
	
	private static final Log LOGGER = LogFactory.getLog(CommonServiceServerImpl.class);
	/* (non-Javadoc)
	 * @see com.cross.plateform.common.rpc.service.server.ICommonServiceServer#close()
	 */
	@Override
	public void close() throws Exception{
		// TODO Auto-generated method stub
		client.close();
	}

	/* (non-Javadoc)
	 * @see com.cross.plateform.common.rpc.service.server.ICommonServiceServer#connectZookeeper(java.lang.String, int)
	 */
	@Override
	public void connectZookeeper(String server, int timeout) throws Exception {
		// TODO Auto-generated method stub
		client = CuratorFrameworkFactory.builder()
	            .connectString(server)
	            .sessionTimeoutMs(timeout)
	            .connectionTimeoutMs(timeout)
	            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
	            .build();
	    client.start();
		
	}

	/* (non-Javadoc)
	 * @see com.cross.plateform.common.rpc.service.server.ICommonServiceServer#registerServer(java.lang.String, java.lang.String)
	 */
	@Override
	public void registerServer(String group, String server) throws Exception{
		// TODO Auto-generated method stub
		this.createNode("/" + group, group,CreateMode.PERSISTENT);
		this.createNode("/" + group + "/"+server, server, CreateMode.EPHEMERAL_SEQUENTIAL);
	}
	
	/* (non-Javadoc)
	 * @see com.cross.plateform.common.rpc.service.client.ICommonServiceClient#registerClient(java.lang.String, java.lang.String)
	 */
	@Override
	public void registerClient(String server, String client) throws Exception {
		// TODO Auto-generated method stub

		this.createNode("/" + server, server,CreateMode.PERSISTENT);
		this.createNode("/" + server +client, client, CreateMode.EPHEMERAL_SEQUENTIAL);
	}
	

    /**
     * 创建node
     * 
     * @param nodeName
     * @param value
     * @return
     */
    public boolean createNode(String nodeName, String value,CreateMode createMode ) {
        boolean suc = false;
        try {
            Stat stat = getClient().checkExists().forPath(nodeName);
            if (stat == null) {
                String opResult = null;
                if (Strings.isNullOrEmpty(value)) {
                    opResult = getClient().create().creatingParentsIfNeeded().withMode(createMode).forPath(nodeName);
                }else {
                    opResult =
                            getClient().create().creatingParentsIfNeeded()
                                .forPath(nodeName, value.getBytes(Charsets.UTF_8));
                }
                suc = Objects.equal(nodeName, opResult);
            }
        }
        catch (Exception e) {
        	 LOGGER.error("createNode fail",e);
        }
        return suc;
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
