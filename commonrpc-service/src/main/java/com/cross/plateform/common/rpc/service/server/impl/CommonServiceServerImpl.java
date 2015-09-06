package com.cross.plateform.common.rpc.service.server.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.listen.Listenable;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import com.cross.plateform.common.rpc.service.server.ICommonServiceServer;
import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.base.Strings;

public class CommonServiceServerImpl implements ICommonServiceServer {

    private CuratorFramework client;

    public static final int TYPE = 0;

    private static final Log LOGGER = LogFactory.getLog(CommonServiceServerImpl.class);


    @Override
    public void close() throws Exception {
        CuratorFrameworkState state = client.getState();
        if(state != CuratorFrameworkState.STOPPED) {
            client.close();
        }
    }

    @Override
    public void connectZookeeper(String server, int timeout) throws Exception {
        client = CuratorFrameworkFactory.builder()
                .connectString(server)
                .sessionTimeoutMs(timeout)
                .connectionTimeoutMs(timeout)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
//        client.getCuratorListenable().addListener(new CuratorListener() {
//            @Override
//            public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
//
//            }
//        });
        client.start();
    }

    @Override
    public void registerServer(final String group, final String server) throws Exception {
        this.createNode("/" + group, group, CreateMode.PERSISTENT);
        this.createNode("/" + group + "/" + server, server, CreateMode.EPHEMERAL_SEQUENTIAL);

        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                if (newState == ConnectionState.RECONNECTED) {
                    try {
                        LOGGER.debug("register server when state change to RECONNECTED");
                        registerServer(group, server);
                    } catch (Exception e) {
                        LOGGER.error("create \"/group+server\" node fail when reconnect");
                    }
                }
            }
        });
    }

    @Override
    public void registerClient(String server, String client) throws Exception {
        // TODO Auto-generated method stub
        //this.createNode("/" + server, server,CreateMode.PERSISTENT);
        this.createNode("/" + server + client, client, CreateMode.EPHEMERAL_SEQUENTIAL);
    }


    /**
     * 创建node
     *
     * @param nodeName
     * @param value
     * @return
     */
    public boolean createNode(String nodeName, String value, CreateMode createMode) {
        boolean suc = false;
        try {
            Stat stat = getClient().checkExists().forPath(nodeName);
            if (stat == null) {
                String opResult = null;
                if (Strings.isNullOrEmpty(value)) {
                    opResult = getClient().create().creatingParentsIfNeeded().withMode(createMode).forPath(nodeName);
                } else {
                    opResult =
                            getClient().create().creatingParentsIfNeeded().withMode(createMode)
                                    .forPath(nodeName, value.getBytes(Charsets.UTF_8));
                }
                suc = Objects.equal(nodeName, opResult);
            }
        } catch (Exception e) {
            LOGGER.error("createNode fail,path:" + nodeName, e);
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
