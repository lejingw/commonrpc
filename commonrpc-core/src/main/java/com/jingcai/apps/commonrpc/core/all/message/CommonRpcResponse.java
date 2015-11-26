package com.jingcai.apps.commonrpc.core.all.message;

import java.io.Serializable;

import com.jingcai.apps.commonrpc.core.codec.all.CommonRpcCodecs;

public class CommonRpcResponse implements Serializable {
	private static final long serialVersionUID = 4590846966635080090L;

	private int requestId;
	private Object response = null;
	private Throwable exception = null;
	private int codecType = CommonRpcCodecs.JAVA_CODEC;
	private int protocolType;
	private int messageLen;
	private byte[] responseClassName;

	public CommonRpcResponse(int requestId, int codecType, int protocolType) {
		this.requestId = requestId;
		this.codecType = codecType;
		this.protocolType = protocolType;
	}

	public void setCodecType(int codecType) {
		this.codecType = codecType;
	}

	public int getMessageLen() {
		return messageLen;
	}

	public void setMessageLen(int messageLen) {
		this.messageLen = messageLen;
	}

	public int getProtocolType() {
		return protocolType;
	}

	public int getCodecType() {
		return codecType;
	}

	public int getRequestId() {
		return requestId;
	}

	public Object getResponse() {
		return response;
	}

	public void setResponse(Object response) {
		this.response = response;
	}

	public boolean isError() {
		return null != exception;
	}

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}

	public byte[] getResponseClassName() {
		return responseClassName;
	}

	public void setResponseClassName(byte[] responseClassName) {
		this.responseClassName = responseClassName;
	}
}
