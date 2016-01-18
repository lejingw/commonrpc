package com.commonrpc.demo.util;

import com.commonrpc.demo.exception.MyException;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.jingcai.apps.common.lang.serialize.KryoUtils;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by lejing on 15/11/23.
 */
public class KryoInit implements InitializingBean {
	@Override
	public void afterPropertiesSet() throws Exception {
		int nextId = 100;
		KryoUtils.registerClass(MyException.class, new Serializer<MyException>() {
			@Override
			public void write(Kryo kryo, Output output, MyException object) {
				kryo.writeObject(output, object.getMessage());
			}

			@Override
			public MyException read(Kryo kryo, Input input, Class<MyException> type) {
				return new MyException(kryo.readObject(input, String.class));
			}
		}, nextId++);
	}
}
