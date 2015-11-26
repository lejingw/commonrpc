package com.jingcai.apps.commonrpc.core.all.message;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import com.jingcai.apps.commonrpc.core.codec.all.CommonRpcCodecs;

public class CommonRpcRequest implements Serializable {
	private static final long serialVersionUID = -3554311529871950375L;

	private byte[] targetInstanceName;
	private byte[] methodName;
	private byte[][] argTypes;
	private Object[] requestObjects = null;
	private Object message = null;
	private int timeout = 0;
	private int id;
	private int protocolType;
	private int codecType = CommonRpcCodecs.JAVA_CODEC;
	private int messageLen;
	private static final AtomicInteger requestIdSeq = new AtomicInteger();

	public CommonRpcRequest(byte[] targetInstanceName, byte[] methodName, byte[][] argTypes,
							Object[] requestObjects, int timeout, int codecType, int protocolType) {
		this(targetInstanceName, methodName, argTypes, requestObjects, timeout, get(), codecType, protocolType);
	}

	public CommonRpcRequest(byte[] targetInstanceName, byte[] methodName, byte[][] argTypes,
							Object[] requestObjects, int timeout, int id, int codecType, int protocolType) {
		this.requestObjects = requestObjects;
		this.id = id;
		this.timeout = timeout;
		this.targetInstanceName = targetInstanceName;
		this.methodName = methodName;
		this.argTypes = argTypes;
		this.codecType = codecType;
		this.protocolType = protocolType;

	}

	public static Integer get() {
		return requestIdSeq.incrementAndGet();
	}

	public int getMessageLen() {
		return messageLen;
	}

	public void setMessageLen(int messageLen) {
		this.messageLen = messageLen;
	}

	public void setArgTypes(byte[][] argTypes) {
		this.argTypes = argTypes;
	}

	public int getProtocolType() {
		return protocolType;
	}

	public int getCodecType() {
		return codecType;
	}

	public Object getMessage() {
		return message;
	}

	public byte[] getTargetInstanceName() {
		return targetInstanceName;
	}

	public byte[] getMethodName() {
		return methodName;
	}

	public int getTimeout() {
		return timeout;
	}

	public Object[] getRequestObjects() {
		return requestObjects;
	}

	public int getId() {
		return id;
	}

	public byte[][] getArgTypes() {
		return argTypes;
	}

	public void setId(int id) {
		this.id = id;
	}
}
