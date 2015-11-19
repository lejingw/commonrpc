package com.commonrpc.demo.provider;

import com.commonrpc.demo.MyException;
import com.commonrpc.demo.sdk.DemoService2;
import com.commonrpc.demo.sdk.RequestVo;
import com.commonrpc.demo.sdk.ResponseVo;

public class DemoService2Impl implements DemoService2 {

	@Override
	public ResponseVo sayHello(RequestVo req) {
		System.out.println(req.getStr());
		ResponseVo res = new ResponseVo();
		String str = "---------------2----";
		res.setStr1(str);
		if(true)
			throw new MyException("!!!this is exception info!!!");
		return res;
	}
}
