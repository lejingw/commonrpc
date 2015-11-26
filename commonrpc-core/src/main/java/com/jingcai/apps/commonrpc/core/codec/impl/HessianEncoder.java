package com.jingcai.apps.commonrpc.core.codec.impl;

import java.io.ByteArrayOutputStream;

import com.caucho.hessian.io.Hessian2Output;
import com.jingcai.apps.commonrpc.core.codec.CommonRpcEncoder;

public class HessianEncoder implements CommonRpcEncoder {

	@Override
	public byte[] encode(Object object) throws Exception {
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		Hessian2Output output = new Hessian2Output(byteArray);
		output.writeObject(object);
		output.close();
		byte[] bytes = byteArray.toByteArray();
		return bytes;
	}

}
