package com.commonrpc.demo.sdk;

import com.commonrpc.demo.myenum.UserType;

public interface DemoService {
	void testNone();
	String genId();
	String sayHello(String name);
	UserType testEnum(UserType userType);
}
