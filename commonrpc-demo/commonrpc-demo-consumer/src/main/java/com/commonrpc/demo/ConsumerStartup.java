package com.commonrpc.demo;

import com.commonrpc.demo.sdk.DemoService2;
import com.commonrpc.demo.sdk.RequestVo;
import com.commonrpc.demo.sdk.ResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class ConsumerStartup {
	private static Logger logger = LoggerFactory.getLogger(ConsumerStartup.class);

	public static void main(String[] args) throws IOException, InterruptedException {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"spring-context.xml"});
		context.registerShutdownHook();

//		DemoService demoService = (DemoService) context.getBean("demoService");
		DemoService2 demoService2 = (DemoService2) context.getBean("demoService2");
		try {
			RequestVo req = new RequestVo();
			req.setStr("hello world !");

			for (int i = 0; i < 10; i++) {
				if (i % 2 == 0) {
					new Thread(new Task1(demoService2, req)).start();
				} else {
					System.out.println(demoService2.testObj(req));
				}
				Thread.sleep(5 * 1000);
			}
		} finally {
			context.close();
		}
	}

	static class Task1 implements Runnable {
		private DemoService2 demoService2;
		private RequestVo req;

		public Task1(DemoService2 demoService2, RequestVo req) {
			this.demoService2 = demoService2;
			this.req = req;
		}

		@Override
		public void run() {
			try {
				//System.out.println("------------------call----1--" + demoService.sayHello("world"));
				ResponseVo res = demoService2.testException(req);
				System.out.println("------------------call----2--" + res.getStr1());
			} catch (Exception e) {
				logger.error("consumer error:", e);
			}
		}
	}
}
