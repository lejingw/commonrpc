package com.cross.plateform.common.rpc.service.server.impl;

import com.cross.plateform.common.rpc.service.server.CommonServiceServer;
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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CommonServiceServerImpl implements CommonServiceServer {
	private static final Logger logger = LoggerFactory.getLogger(CommonServiceServerImpl.class);
	private CuratorFramework zookeeperClient;
	private List<String> serverRegistMap = new CopyOnWriteArrayList<>();
	private List<String> clientRegistMap = new CopyOnWriteArrayList<>();

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
	}

	@Override
	public void registerServer(final String group, final String server) throws Exception {
		createNode("/" + group + "/server", group, CreateMode.PERSISTENT);

		String path = "/" + group + "/server/" + server;
		createNode(path, server, CreateMode.EPHEMERAL_SEQUENTIAL);
		if (!serverRegistMap.contains(path)) {
			zookeeperClient.getConnectionStateListenable().addListener(new ConnectionStateListener() {
				@Override
				public void stateChanged(CuratorFramework client, ConnectionState newState) {
					if (newState == ConnectionState.RECONNECTED) {
						try {
							logger.debug("[stateChanged]register server when state change to RECONNECTED");
							registerServer(group, server);
						} catch (Exception e) {
							logger.error("registerServer fail when reconnect");
						}
					}
				}
			});
//            zookeeperClient.getCuratorListenable().addListener(new CuratorListener() {
//                @Override
//                public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
//                    if(Watcher.Event.KeeperState.SyncConnected == event.getWatchedEvent().getState()){
//                        try {
//                            logger.debug("[eventReceived]register server when state change to RECONNECTED");
//                            registerServer(group, server);
//                        } catch (Exception e) {
//                            logger.error("registerServer fail when reconnect");
//                        }
//                    }
//                }
//            });
			serverRegistMap.add(path);
		}
	}

	@Override
	public void registerClient(final String group, final String server, final String client) throws Exception {
		createNode("/" + group + "/client", group, CreateMode.PERSISTENT);

		String path = "/" + group + "/client/" + client;
		createNode(path, client + "->" + server, CreateMode.EPHEMERAL_SEQUENTIAL);
		if (!clientRegistMap.contains(path)) {
			zookeeperClient.getConnectionStateListenable().addListener(new ConnectionStateListener() {
				@Override
				public void stateChanged(CuratorFramework curatorFramework, ConnectionState newState) {
					switch (newState) {
						case RECONNECTED: {
							try {
								logger.debug("[stateChanged]register client when state change to RECONNECTED");
								registerClient(group, server, client);
							} catch (Exception e) {
								logger.error("registerClient fail when reconnect");
							}
						}
					}
				}
			});
			clientRegistMap.add(path);
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
					opResult = zookeeperClient.create().creatingParentsIfNeeded().withMode(createMode).forPath(nodeName, value.getBytes(Charsets.UTF_8));
				}
				suc = Objects.equal(nodeName, opResult);
			}
		} catch (Exception e) {
			logger.error("createNode fail,path:" + nodeName, e);
		}
		return suc;
	}
}
