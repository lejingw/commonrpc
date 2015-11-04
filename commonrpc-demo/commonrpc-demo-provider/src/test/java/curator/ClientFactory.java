package curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Created by lejing on 15/11/2.
 */
public class ClientFactory {

	//TODO 参考 http://supben.iteye.com/blog/2094077

	public static CuratorFramework newClient() {
		CuratorFramework client = CuratorFrameworkFactory.builder()
				.connectString("192.168.0.11:2181,192.168.0.18:2181,192.168.0.19:2181")
				.sessionTimeoutMs(30000)
				.connectionTimeoutMs(30000)
				.canBeReadOnly(false)
				.retryPolicy(new ExponentialBackoffRetry(1000, Integer.MAX_VALUE))
//				.namespace(namespace)
				.defaultData(null)
				.build();
		return client;
	}

}
