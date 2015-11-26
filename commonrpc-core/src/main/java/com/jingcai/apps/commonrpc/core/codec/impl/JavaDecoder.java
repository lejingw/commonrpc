package com.jingcai.apps.commonrpc.core.codec.impl;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import com.jingcai.apps.commonrpc.core.codec.CommonRpcDecoder;

/**
 * jdk 序列化
 */
public class JavaDecoder implements CommonRpcDecoder {

	@Override
	public Object decode(String className, byte[] bytes) throws Exception {
		ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(bytes));
		Object resultObject = objectIn.readObject();
		objectIn.close();
		return resultObject;
	}

}
