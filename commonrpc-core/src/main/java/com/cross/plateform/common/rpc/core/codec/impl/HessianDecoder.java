package com.cross.plateform.common.rpc.core.codec.impl;

import java.io.ByteArrayInputStream;

import com.caucho.hessian.io.Hessian2Input;
import com.cross.plateform.common.rpc.core.codec.CommonRpcDecoder;

public class HessianDecoder implements CommonRpcDecoder {

	@Override
	public Object decode(String className, byte[] bytes) throws Exception {
		Hessian2Input input = new Hessian2Input(new ByteArrayInputStream(bytes));
		Object resultObject = input.readObject();
		input.close();
		return resultObject;
	}

}
