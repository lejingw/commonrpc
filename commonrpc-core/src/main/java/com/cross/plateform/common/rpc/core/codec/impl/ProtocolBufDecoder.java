package com.cross.plateform.common.rpc.core.codec.impl;

import java.util.concurrent.ConcurrentHashMap;
import com.cross.plateform.common.rpc.core.codec.CommonRpcDecoder;
import com.google.protobuf.Message;

public class ProtocolBufDecoder implements CommonRpcDecoder {
	
	private static ConcurrentHashMap<String, Message> messages = new ConcurrentHashMap<String, Message>();

	public static void addMessage(String className,Message message){
		messages.putIfAbsent(className, message);
	}

	@Override
	public Object decode(String className, byte[] bytes) throws Exception {
		Message message = messages.get(className);
		return message.newBuilderForType().mergeFrom(bytes).build();
	}

}
