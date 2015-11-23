package com.commonrpc.demo.provider;

import com.commonrpc.demo.exception.MyException;
import com.commonrpc.demo.sdk.DemoService2;
import com.commonrpc.demo.sdk.RequestVo;
import com.commonrpc.demo.sdk.ResponseVo;

public class DemoService2Impl implements DemoService2 {


	@Override
	public ResponseVo testObj(RequestVo req) {
		System.out.println(req.getStr());
		ResponseVo res = new ResponseVo();
		String str = "--testObj--";
		res.setStr1(str);
		return res;
	}

	@Override
	public ResponseVo testException(RequestVo req) {
		System.out.println(req.getStr());
		if (true)
			throw new MyException("!!!this is exception info!!!");
		return new ResponseVo();
	}
}
