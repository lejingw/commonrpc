package com.commonrpc.demo.provider;

import com.commonrpc.demo.myenum.UserType;
import com.commonrpc.demo.sdk.DemoService;

public class DemoServiceImpl implements DemoService {

	@Override
	public void testNone() {
		System.out.println("--testNone--");
	}

	@Override
	public String genId() {
		String str = "--genId--";
		System.out.println(str);
		return "this id is 100001";
	}

	@Override
	public String sayHello(String name) {
		String str = "--sayHello--";
		System.out.println(str);
		return "Hello " + name;
	}

	@Override
	public UserType testEnum(UserType userType) {
		System.out.println("receive:" + userType.name() + "\t val:" + userType.val());
		return UserType.game_merchant;
	}

}
