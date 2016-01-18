package com.jingcai.apps.commonrpc.core.codec.impl;

import com.jingcai.apps.common.lang.serialize.KryoUtils;
import com.jingcai.apps.commonrpc.core.codec.CommonRpcDecoder;

/**
 * Kryo 解码
 */
public class KryoDecoder implements CommonRpcDecoder {

	@Override
	public Object decode(String className, byte[] bytes) throws Exception {
		return KryoUtils.getKryo().readClassAndObject(bytes);
	}

}
