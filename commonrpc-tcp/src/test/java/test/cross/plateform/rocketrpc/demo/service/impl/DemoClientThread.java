/**
 * 
 */
package test.cross.plateform.rocketrpc.demo.service.impl;
import java.util.concurrent.CountDownLatch;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import test.cross.plateform.rocketrpc.demo.service.IDemoService;

public class DemoClientThread {
	public static void main(String[] args) throws Exception {
		testRpc();
	}

	public static void testRpc() throws Exception {

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"CommonRpcClient.xml");

		final IDemoService demoService = (IDemoService) context
				.getBean("demoServiceClient");
		long time1 = System.currentTimeMillis();
		final int count = 200000;
		final int threadcount = 2;
		final java.util.concurrent.CountDownLatch countDownLatch = new CountDownLatch(
				threadcount);

		for (int j = 0; j < threadcount; j++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < count; i++) {
						String ret = demoService.sayDemo("demo_" + i);
						//System.out.println(ret);
					}
					countDownLatch.countDown();
				}
			}).start();
		}
		countDownLatch.await();
		long end1 = System.currentTimeMillis();
		System.out.println("完成时间:" + (end1 - time1) + ",平均时间："
				+ ((double) (end1 - time1) / (double) (count * threadcount)));
	}
}
