package org.commonrpc.core.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by lejing on 15/11/5.
 */
public class ServerZurator {

	private static CuratorFramework client = null;
	private static String path1 = "/example/server";
	private static String path2 = "/example/client";
	private static byte[] data = "test".getBytes();

	@BeforeClass
	public static void start() {
		client = CreateClientExample.createWithOptions(CreateClientExample.getConnectString, new ExponentialBackoffRetry(1000, 3), 1000, 1000);
		client.start();
	}

	@AfterClass
	public static void shutdown() {
		CloseableUtils.closeQuietly(client);
	}

	@Test
	public void test1() throws Exception {
//		client.create().withMode(CreateMode.PERSISTENT).forPath(path2, data);
		client.create().withMode(CreateMode.EPHEMERAL).forPath(path1 +"/bb", data);
		client.create().withMode(CreateMode.EPHEMERAL).forPath(path1 +"/bb/cc", data);
//		client.create().withMode(CreateMode.EPHEMERAL).forPath(path1, data);
	}
}
