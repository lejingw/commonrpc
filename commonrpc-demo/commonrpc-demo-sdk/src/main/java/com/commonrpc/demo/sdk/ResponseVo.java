package com.commonrpc.demo.sdk;

/**
 * Created by lejing on 15/11/4.
 */
public class ResponseVo {
	private String str1;

	public String getStr1() {
		return str1;
	}

	public void setStr1(String str1) {
		this.str1 = str1;
	}

	@Override
	public String toString() {
		return "toString:" + str1;
	}
}
