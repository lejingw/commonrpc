package com.jingcai.apps.commonrpc.zk.service.server.impl;

import com.jingcai.apps.commonrpc.zk.service.server.CommonServiceServer;
import com.google.common.base.Charsets;
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
						   switch (newState) {
							   case RECONNECTED: {
								   try {
									   logger.debug("[stateChanged]register server when state change to RECONNECTED");
									   registerServer(group, server);
								   } catch (Exception e) {
									   logger.error("registerServer fail when reconnect");
								   }
								   break;
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
		public void registerClient ( final String group, final String server, final String client)throws Exception {
			createNode("/" + group + "/client", group, CreateMode.PERSISTENT);
			createNode("/" + group + "/client/" + client + "->" + server, client, CreateMode.EPHEMERAL);
		}

		@Override
		public void unregisterClient ( final String group, final String server, final String client)throws Exception {
			deleteNode("/" + group + "/client/" + client + "->" + server);
		}

		/**
		 * 创建node
		 *
		 * @param nodeName
		 * @param value
		 * @return
		 */

	private void createNode(String nodeName, String value, CreateMode createMode) {
		try {
			Stat stat = zookeeperClient.checkExists().forPath(nodeName);
			if (stat == null) {
				if (Strings.isNullOrEmpty(value)) {
					zookeeperClient.create().creatingParentsIfNeeded().withMode(createMode).forPath(nodeName);
				} else {
					zookeeperClient.create().creatingParentsIfNeeded().withMode(createMode).forPath(nodeName, value.getBytes(Charsets.UTF_8));
				}
			}
		} catch (Exception e) {
			logger.error("createNode fail, path:" + nodeName, e);
		}
	}

	private void deleteNode(String nodeName) {
		try {
			Stat stat = zookeeperClient.checkExists().forPath(nodeName);
			if (null != stat) {
				zookeeperClient.delete().inBackground().forPath(nodeName);
			}
		} catch (Exception e) {
			logger.error("deleteNode fail, path:" + nodeName, e);
		}
	}
}
