package com.commonrpc.demo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class ProviderStartup {

	public static void main(String[] args) throws IOException, InterruptedException {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"spring-context.xml"});
		context.registerShutdownHook();

		Thread.sleep(1000 * 1000);
		context.close();
	}
}
