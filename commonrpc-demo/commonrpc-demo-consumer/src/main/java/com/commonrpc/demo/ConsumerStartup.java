package com.commonrpc.demo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.commonrpc.demo.sdk.DemoService2;
import com.commonrpc.demo.sdk.RequestVo;
import com.commonrpc.demo.sdk.ResponseVo;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.commonrpc.demo.sdk.DemoService;

public class ConsumerStartup {

	public static void main(String[] args) throws IOException, InterruptedException {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"spring-context.xml"});
		context.registerShutdownHook();

		DemoService demoService = (DemoService) context.getBean("demoService");
		DemoService2 demoService2 = (DemoService2) context.getBean("demoService2");

		System.out.println("------------------call------" + demoService.sayHello("world"));
		RequestVo req = new RequestVo();
		req.setStr("hello world !");
		ResponseVo res = demoService2.sayHello(req);
		System.out.println("------------------call------" + res.getStr1());
		Thread.sleep(10 * 1000);
		context.close();
	}
}
