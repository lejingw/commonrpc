package com.cross.plateform.common.rpc.service.server.impl;

import com.cross.plateform.common.rpc.service.server.ICommonServiceServer;
import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonServiceServerImpl implements ICommonServiceServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonServiceServerImpl.class);
    private CuratorFramework zookeeperClient;
    private boolean registReconnectListenerFlag = false;

    @Override
    public void close() throws Exception {
        CuratorFrameworkState state = zookeeperClient.getState();
        if (state != CuratorFrameworkState.STOPPED) {
            zookeeperClient.close();
        }
    }

    @Override
    public void connectZookeeper(String server, int timeout) throws Exception {
        zookeeperClient = CuratorFrameworkFactory.builder()
                .connectString(server)
                .sessionTimeoutMs(timeout)
                .connectionTimeoutMs(timeout)
                .retryPolicy(new ExponentialBackoffRetry(2000, 3))
                .build();
        zookeeperClient.start();
        registReconnectListenerFlag = false;
    }

    @Override
    public void registerServer(final String group, final String server) throws Exception {
        createNode("/" + group, group, CreateMode.PERSISTENT);
        createNode("/" + group + "/" + server, server, CreateMode.EPHEMERAL_SEQUENTIAL);
        if (!registReconnectListenerFlag) {
            zookeeperClient.getConnectionStateListenable().addListener(new ConnectionStateListener() {
                @Override
                public void stateChanged(CuratorFramework client, ConnectionState newState) {
                    if (newState == ConnectionState.RECONNECTED) {
                        try {
                            LOGGER.debug("[stateChanged]register server when state change to RECONNECTED");
                            registerServer(group, server);
                        } catch (Exception e) {
                            LOGGER.error("registerServer fail when reconnect");
                        }
                    }
                }
            });
//            zookeeperClient.getCuratorListenable().addListener(new CuratorListener() {
//                @Override
//                public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
//                    if(Watcher.Event.KeeperState.SyncConnected == event.getWatchedEvent().getState()){
//                        try {
//                            LOGGER.debug("[eventReceived]register server when state change to RECONNECTED");
//                            registerServer(group, server);
//                        } catch (Exception e) {
//                            LOGGER.error("registerServer fail when reconnect");
//                        }
//                    }
//                }
//            });
            registReconnectListenerFlag = true;
        }
    }

    @Override
    public void registerClient(final String server, final String client) throws Exception {
        //createNode("/" + server, server,CreateMode.PERSISTENT);
        createNode("/" + server + client, client, CreateMode.EPHEMERAL_SEQUENTIAL);
        if (!registReconnectListenerFlag) {
            zookeeperClient.getConnectionStateListenable().addListener(new ConnectionStateListener() {
                @Override
                public void stateChanged(CuratorFramework curatorFramework, ConnectionState newState) {
                    if (newState == ConnectionState.RECONNECTED) {
                        try {
                            LOGGER.debug("[stateChanged]register client when state change to RECONNECTED");
                            registerClient(server, client);
                        } catch (Exception e) {
                            LOGGER.error("registerClient fail when reconnect");
                        }
                    }
                }
            });
            registReconnectListenerFlag = true;
        }
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
            Stat stat = zookeeperClient.checkExists().forPath(nodeName);
            if (stat == null) {
                String opResult = null;
                if (Strings.isNullOrEmpty(value)) {
                    opResult = zookeeperClient.create().creatingParentsIfNeeded().withMode(createMode).forPath(nodeName);
                } else {
                    opResult =
                            zookeeperClient.create().creatingParentsIfNeeded().withMode(createMode)
                                    .forPath(nodeName, value.getBytes(Charsets.UTF_8));
                }
                suc = Objects.equal(nodeName, opResult);
            }
        } catch (Exception e) {
            LOGGER.error("createNode fail,path:" + nodeName, e);
        }
        return suc;
    }
}
