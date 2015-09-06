package com.cross.plateform.common.rpc.service.client.impl;

import com.cross.plateform.common.rpc.service.client.ICommonServiceClient;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class CommonServiceClientImpl implements ICommonServiceClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonServiceClientImpl.class);
    private ReentrantLock lock = new ReentrantLock();
    private CuratorFramework zookeeperClient;//客户端
    private Map<String, Boolean> flag = new ConcurrentHashMap<>();
    private Map<String, Set<InetSocketAddress>> servers = new ConcurrentHashMap<>();

    @Override
    public Set<InetSocketAddress> getServersByGroup(String group) throws Exception {
        if (!flag.containsKey(group)) {
            PathChildrenCache childrenCache = new PathChildrenCache(zookeeperClient, "/" + group, true);
            childrenCache.start(StartMode.POST_INITIALIZED_EVENT);
            childrenCache.getListenable().addListener(
                    new PathChildrenCacheListener() {
                        @Override
                        public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                            if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {//监听子节点被删除的情况
                                String path = event.getData().getPath();
                                String[] nodes = path.split("/");// 1:group 2:address
                                if (nodes.length > 0 && nodes.length == 3) {
                                    if (servers.containsKey(nodes[1])) {
                                        updateAddress(nodes[1]);
                                    }
                                }
                            } else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {//监听增加
                                String path = event.getData().getPath();
                                String[] nodes = path.split("/");// 1:group 2:address
                                if (nodes.length > 0 && nodes.length == 3) {
                                    updateAddress(nodes[1]);
                                }
                            }
                        }
                    },
                    Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2));
            flag.put(group, true);
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
            Map<String, String> valueMap = listChildrenDetail("/" + group);
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
//
//    /**
//     * 更新本地化的server
//     *
//     * @param group
//     * @param server
//     * @throws Exception
//     */
//    private void updateServerList(String group, String server) throws Exception {
//        if (servers.containsKey(group)) {
//            Set<InetSocketAddress> rpcservers = servers.get(group);
//            Set<InetSocketAddress> newrpcservers = new HashSet<InetSocketAddress>();
//            for (InetSocketAddress socketAddress : rpcservers) {
//                String server1 = socketAddress.getAddress().toString() + ":" + socketAddress.getPort();
//                String server2 = server1.substring(1, server1.length());
//
//                if (!server.startsWith(server2)) {//更新不包括
//                    newrpcservers.add(socketAddress);
//                } else {//删除包括
//                    deleteNode(server1);
//                }
//            }
//            servers.put(group, newrpcservers);
//        }
//    }
//    /**
//     * 删除节点
//     *
//     * @param path
//     * @throws Exception
//     */
//    private void deleteNode(String path) throws Exception {
//        try {
//            Stat stat = getClient().checkExists().forPath(path);
//            if (stat != null) {
//                getClient().delete().deletingChildrenIfNeeded().forPath(path);
//            }
//        } catch (Exception e) {
//        }
//    }

    @Override
    public void close() throws Exception {
        lock.lock();
        try {
            servers.clear();
        } finally {
            lock.unlock();
        }
        zookeeperClient.close();
    }

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
            List<String> children = childrenBuilder.forPath(node);
            GetDataBuilder dataBuilder = zookeeperClient.getData();
            if (children != null) {
                for (String child : children) {
                    String propPath = ZKPaths.makePath(node, child);
                    map.put(child, new String(dataBuilder.forPath(propPath), Charsets.UTF_8));
                }
            }
        } catch (Exception e) {
            LOGGER.error("listChildrenDetail fail", e);
        }
        return map;
    }
}
