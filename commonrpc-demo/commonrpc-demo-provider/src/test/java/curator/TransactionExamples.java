package curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.data.Stat;

import java.util.Collection;

/**
 * 事务操作
 *
 * @author shencl
 */
public class TransactionExamples {
	private static CuratorFramework client = ClientFactory.newClient();

	public static void main(String[] args) {
		try {
			client.start();
			// 开启事务
			CuratorTransaction transaction = client.inTransaction();
			Stat stat = client.checkExists().forPath("/a");
			if(null != stat){
				System.out.println(stat);
				client.delete().forPath("/a/path");
				client.delete().forPath("/a");
				System.out.println("delete successfully!");
			}

			Collection<CuratorTransactionResult> results = transaction
					.create().forPath("/a")
					.and().create().forPath("/a/path", "some data".getBytes())
					.and().setData().forPath("/a/path", "other data".getBytes())
					.and().delete().forPath("/a/path")
					.and().delete().forPath("/a")
					.and().commit();

			for (CuratorTransactionResult result : results) {
				System.out.println(result.getForPath() + " - " + result.getType());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 释放客户端连接
			CloseableUtils.closeQuietly(client);
		}

	}
}