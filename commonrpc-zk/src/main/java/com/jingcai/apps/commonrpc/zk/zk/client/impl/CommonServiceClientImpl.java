package com.jingcai.apps.commonrpc.zk.zk.client.impl;

import com.jingcai.apps.commonrpc.zk.zk.client.CommonServiceClient;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.utils.ZKPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class CommonServiceClientImpl implements CommonServiceClient {
	private static final Logger logger = LoggerFactory.getLogger(CommonServiceClientImpl.class);
	private final ReentrantLock lock = new ReentrantLock();
	private CuratorFramework zookeeperClient;//客户端
	private final Map<String, PathChildrenCache> groupMap = new ConcurrentHashMap<>();
	private final Map<String, Set<InetSocketAddress>> servers = new ConcurrentHashMap<>();

	@Override
	public void connectZookeeper(final String server, final int timeout) throws Exception {
		zookeeperClient = CuratorFrameworkFactory.builder()
				.connectString(server)
				.sessionTimeoutMs(timeout)
				.connectionTimeoutMs(timeout)
				.retryPolicy(new ExponentialBackoffRetry(2000, 3))
				.build();
		zookeeperClient.start();
	}

	@Override
	public Set<InetSocketAddress> getServersByGroup(final String group) throws Exception {
		if (!groupMap.containsKey(group)) {
			PathChildrenCache childrenCache = new PathChildrenCache(zookeeperClient, "/" + group + "/server", true);
			childrenCache.getListenable().addListener(
					new PathChildrenCacheListener() {
						@Override
						public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
							switch (event.getType()) {
								case CHILD_ADDED:
								case CHILD_REMOVED: {
									updateAddress(group);
								}
							}
						}
					}
			);
			childrenCache.start(StartMode.POST_INITIALIZED_EVENT);
			groupMap.put(group, childrenCache);
		}

		if (servers.containsKey(group)) {
			return servers.get(group);
		}

		updateAddress(group);
		return servers.get(group);
	}

	private void updateAddress(String group) {
		lock.lock();
		try {
			Set<InetSocketAddress> addresses = servers.get(group);
			if (null == addresses) {
				addresses = new HashSet<>();
				servers.put(group, addresses);
			} else {
				addresses.clear();
			}
			Map<String, String> valueMap = listChildrenDetail("/" + group + "/server");
			if (valueMap != null && valueMap.values().size() > 0) {
				for (String value : valueMap.values()) {
					String[] nodes1 = value.split(":");
					InetSocketAddress socketAddress = new InetSocketAddress(nodes1[0], Integer.parseInt(nodes1[1]));
					addresses.add(socketAddress);
				}
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void close() throws Exception {
		lock.lock();
		try {
			servers.clear();
			for (Map.Entry<String, PathChildrenCache> entry : groupMap.entrySet()) {
				CloseableUtils.closeQuietly(entry.getValue());
			}
			groupMap.clear();
		} finally {
			lock.unlock();
		}
		CuratorFrameworkState state = zookeeperClient.getState();
		if (state != CuratorFrameworkState.STOPPED) {
			zookeeperClient.close();
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
			GetChildrenBuilder childrenBuilder = zookeeperClient.getChildren();
			GetDataBuilder dataBuilder = zookeeperClient.getData();

			List<String> children = childrenBuilder.forPath(node);
			if (children != null) {
				for (String child : children) {
					String propPath = ZKPaths.makePath(node, child);
					byte[] data = dataBuilder.forPath(propPath);
					map.put(child, new String(data, Charsets.UTF_8));
				}
			}
		} catch (Exception e) {
			logger.error("listChildrenDetail fail", e);
		}
		return map;
	}
}
