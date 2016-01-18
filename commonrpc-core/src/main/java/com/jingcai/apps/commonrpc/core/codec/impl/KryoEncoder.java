package com.jingcai.apps.commonrpc.core.codec.impl;

import com.jingcai.apps.common.lang.serialize.KryoUtils;
import com.jingcai.apps.commonrpc.core.codec.CommonRpcEncoder;

public class KryoEncoder implements CommonRpcEncoder {

	@Override
	public byte[] encode(Object object) throws Exception {
		return KryoUtils.getKryo().writeClassAndObject(object);
	}

}
