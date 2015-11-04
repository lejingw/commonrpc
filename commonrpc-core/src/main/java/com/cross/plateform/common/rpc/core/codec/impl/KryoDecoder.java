package com.cross.plateform.common.rpc.core.codec.impl;

import com.cross.plateform.common.rpc.core.codec.CommonRpcDecoder;
import com.cross.plateform.common.rpc.core.util.KryoUtils;
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
