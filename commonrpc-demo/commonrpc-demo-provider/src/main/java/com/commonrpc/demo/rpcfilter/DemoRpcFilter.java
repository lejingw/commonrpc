package com.commonrpc.demo.rpcfilter;

import java.lang.reflect.Method;

import com.jingcai.apps.commonrpc.core.filter.RpcFilter;

public class DemoRpcFilter implements RpcFilter {
	@Override
	public boolean doBeforeRequest(Method method, Object processor,
								   Object[] requestObjects) {
		System.out.println("----------------拦截开始----------------");
		return true;
	}

	@Override
	public void doAfterRequest(Object processor) {
		System.out.println("----------------拦截结束----------------");
	}
}
