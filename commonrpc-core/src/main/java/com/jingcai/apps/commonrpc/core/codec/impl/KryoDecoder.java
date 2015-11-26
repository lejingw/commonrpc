package com.jingcai.apps.commonrpc.core.codec.impl;

import com.jingcai.apps.commonrpc.core.codec.CommonRpcDecoder;
import com.jingcai.apps.commonrpc.core.util.KryoUtils;
import com.esotericsoftware.kryo.io.Input;

/**
 * Kryo 解码
 */
public class KryoDecoder implements CommonRpcDecoder {

	@Override
	public Object decode(String className, byte[] bytes) throws Exception {
		Input input = new Input(bytes);
		return KryoUtils.getKryo().readClassAndObject(input);
	}

}
