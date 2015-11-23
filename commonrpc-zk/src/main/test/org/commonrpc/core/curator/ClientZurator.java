package org.commonrpc.core.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by lejing on 15/11/5.
 */
public class ClientZurator {

	private static CuratorFramework client = null;
	private static String path = "/example/server";
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
		CuratorListener listener = new CuratorListener() {
			@Override
			public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
				// examine event for details
				System.out.println("----------------");
				System.out.println(event);
			}
		};
		client.getCuratorListenable().addListener(listener);

		client.getChildren().watched().forPath(path);
		Thread.sleep(1000 * 1000);
	}
}
