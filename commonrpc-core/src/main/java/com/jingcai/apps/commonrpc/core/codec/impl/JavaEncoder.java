package com.jingcai.apps.commonrpc.core.codec.impl;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import com.jingcai.apps.commonrpc.core.codec.CommonRpcEncoder;

/**
 * jdk 反序列化
 */
public class JavaEncoder implements CommonRpcEncoder {

	@Override
	public byte[] encode(Object object) throws Exception {
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		ObjectOutputStream output = new ObjectOutputStream(byteArray);
		output.writeObject(object);
		output.flush();
		output.close();
		return byteArray.toByteArray(); 
	}

}
