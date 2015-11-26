package com.jingcai.apps.commonrpc.core.codec.impl;

import com.jingcai.apps.commonrpc.core.codec.CommonRpcEncoder;
import com.jingcai.apps.commonrpc.core.util.KryoUtils;
import com.esotericsoftware.kryo.io.Output;

public class KryoEncoder implements CommonRpcEncoder {

	@Override
	public byte[] encode(Object object) throws Exception {
		Output output = new Output(256, 256*1024);
		KryoUtils.getKryo().writeClassAndObject(output, object);
		return output.toBytes();
	}

}
