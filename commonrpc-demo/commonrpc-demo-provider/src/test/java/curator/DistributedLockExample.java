package curator;

import com.google.common.collect.Lists;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.utils.CloseableUtils;

import java.util.List;

/**
 * 分布式锁实例
 * 
 * @author shencl
 */
public class DistributedLockExample {
	private static CuratorFramework client = ClientFactory.newClient();
	private static final String PATH = "/locks";

	// 进程内部（可重入）读写锁
	private static InterProcessReadWriteLock lock;
	// 读锁
	private static InterProcessLock readLock;
	// 写锁
	private static InterProcessLock writeLock;


	public static void main(String[] args) {
		try {
			client.start();
			lock = new InterProcessReadWriteLock(client, PATH);
			readLock = lock.readLock();
			writeLock = lock.writeLock();
			Thread.sleep(1000);

			List<Thread> jobs = Lists.newArrayList();
			for (int i = 0; i < 10; i++) {
				Thread t = new Thread(new ParallelJob("Parallel任务" + i, readLock));
				jobs.add(t);
			}

			for (int i = 0; i < 10; i++) {
				Thread t = new Thread(new MutexJob("Mutex任务" + i, writeLock));
				jobs.add(t);
			}

			for (Thread t : jobs) {
				t.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseableUtils.closeQuietly(client);
		}
	}
}
