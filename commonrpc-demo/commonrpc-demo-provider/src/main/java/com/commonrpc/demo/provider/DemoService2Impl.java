package com.commonrpc.demo.provider;

import com.commonrpc.demo.sdk.DemoService;

public class DemoService2Impl implements DemoService {

	@Override
	public String sayHello(String name) {
		String str = "---------------2----";
		System.out.println(str);
		return "Hello 2 " + name + str;
	}

}
