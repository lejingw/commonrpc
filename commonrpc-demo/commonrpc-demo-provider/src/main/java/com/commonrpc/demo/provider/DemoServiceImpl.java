package com.commonrpc.demo.provider;

import com.commonrpc.demo.sdk.DemoService;

public class DemoServiceImpl implements DemoService {

	@Override
	public String sayHello(String name) {

		return "Hello " + name + "";
	}

}
