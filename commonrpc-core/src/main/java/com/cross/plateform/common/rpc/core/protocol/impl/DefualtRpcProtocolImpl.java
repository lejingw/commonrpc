package com.cross.plateform.common.rpc.core.protocol.impl;

import com.cross.plateform.common.rpc.core.all.message.CommonRpcRequest;
import com.cross.plateform.common.rpc.core.all.message.CommonRpcResponse;
import com.cross.plateform.common.rpc.core.bytebuffer.RpcByteBuffer;
import com.cross.plateform.common.rpc.core.codec.all.CommonRpcCodecs;
import com.cross.plateform.common.rpc.core.protocol.RpcProtocol;
import com.cross.plateform.common.rpc.core.protocol.all.CommonRpcProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DefualtRpcProtocolImpl implements RpcProtocol {
	private static final Logger logger = LoggerFactory.getLogger(DefualtRpcProtocolImpl.class);
	public static final int PROTOCOL_TYPE = 1;

	private static final int REQUEST_HEADER_LEN = 1 * 6 + 5 * 4;
	private static final int RESPONSE_HEADER_LEN = 1 * 6 + 3 * 4;
	private static final byte VERSION = (byte) 1;
	private static final byte REQUEST = (byte) 0;
	private static final byte RESPONSE = (byte) 1;

	@Override
	public RpcByteBuffer encode(Object message, RpcByteBuffer bytebufferWrapper) throws Exception {
		if (!(message instanceof CommonRpcRequest) && !(message instanceof CommonRpcResponse)) {
			throw new Exception("only support send RequestWrapper && ResponseWrapper");
		}
		if (message instanceof CommonRpcRequest) {
			try {
				CommonRpcRequest wrapper = (CommonRpcRequest) message;

				int requestArgTypesLen = 0;
				List<byte[]> requestArgTypes = new ArrayList<>();
				byte[][] requestArgTypeStrings = wrapper.getArgTypes();
				for (byte[] requestArgType : requestArgTypeStrings) {
					requestArgTypes.add(requestArgType);
					requestArgTypesLen += requestArgType.length;
				}

				int requestArgsLen = 0;
				List<byte[]> requestArgs = new ArrayList<>();
				Object[] requestObjects = wrapper.getRequestObjects();
				if (requestObjects != null) {
					for (Object requestArg : requestObjects) {
						byte[] requestArgByte = CommonRpcCodecs.getEncoder(wrapper.getCodecType()).encode(requestArg);
						requestArgs.add(requestArgByte);
						requestArgsLen += requestArgByte.length;
					}
				}
				byte[] targetInstanceNameByte = wrapper.getTargetInstanceName();
				byte[] methodNameByte = wrapper.getMethodName();

				int timeout = wrapper.getTimeout();
				int capacity = CommonRpcProtocol.HEADER_LEN + REQUEST_HEADER_LEN
						+ requestArgs.size() * 4 * 2
						+ targetInstanceNameByte.length + methodNameByte.length
						+ requestArgTypesLen + requestArgsLen;

				//--------------HEADER start----------------
				RpcByteBuffer byteBuffer = bytebufferWrapper.get(capacity);
				byteBuffer.writeByte(CommonRpcProtocol.CURRENT_VERSION);
				byteBuffer.writeByte((byte) PROTOCOL_TYPE);
				//--------------HEADER end----------------

				//---------------REQUEST_HEADER start----------
				byteBuffer.writeByte(VERSION);//1B
				byteBuffer.writeByte(REQUEST);//1B
				byteBuffer.writeByte((byte) wrapper.getCodecType());//1B
				byteBuffer.writeByte((byte) 0);//1B
				byteBuffer.writeByte((byte) 0);//1B
				byteBuffer.writeByte((byte) 0);//1B

				byteBuffer.writeInt(wrapper.getId());//4B
				byteBuffer.writeInt(timeout);//4B
				byteBuffer.writeInt(targetInstanceNameByte.length);//4B
				byteBuffer.writeInt(methodNameByte.length);//4B
				byteBuffer.writeInt(requestArgs.size());//4B
				//---------------REQUEST_HEADER end----------

				for (byte[] requestArgType : requestArgTypes) {
					byteBuffer.writeInt(requestArgType.length);
				}
				for (byte[] requestArg : requestArgs) {
					byteBuffer.writeInt(requestArg.length);
				}
				byteBuffer.writeBytes(targetInstanceNameByte);
				byteBuffer.writeBytes(methodNameByte);
				for (byte[] requestArgType : requestArgTypes) {
					byteBuffer.writeBytes(requestArgType);
				}
				for (byte[] requestArg : requestArgs) {
					byteBuffer.writeBytes(requestArg);
				}
				return byteBuffer;
			} catch (Exception e) {
				logger.error("encode request object error", e);
				throw e;
			}
		} else {
			CommonRpcResponse wrapper = (CommonRpcResponse) message;
			byte[] className = new byte[0];
			byte[] body = new byte[0];
			try {
				// no return object
				if (wrapper.getResponse() != null) {
					className = wrapper.getResponse().getClass().getName().getBytes();
					body = CommonRpcCodecs.getEncoder(wrapper.getCodecType()).encode(wrapper.getResponse());
				}
				if (wrapper.isError()) {
					className = wrapper.getException().getClass().getName().getBytes();
					body = CommonRpcCodecs.getEncoder(wrapper.getCodecType()).encode(wrapper.getException());
				}
			} catch (Exception e) {
				logger.error("encode response object error", e);
				// still create responsewrapper,so client can get exception
				Exception response = new Exception("serialize response object error", e);
				wrapper.setResponse(response);
				className = response.getClass().getName().getBytes();
				body = CommonRpcCodecs.getEncoder(wrapper.getCodecType()).encode(response);
			}
			int capacity = CommonRpcProtocol.HEADER_LEN + RESPONSE_HEADER_LEN + body.length;
			if (wrapper.getCodecType() == CommonRpcCodecs.PB_CODEC) {
				capacity += className.length;
			}
			RpcByteBuffer byteBuffer = bytebufferWrapper.get(capacity);
			//--------------HEADER start----------------
			byteBuffer.writeByte(CommonRpcProtocol.CURRENT_VERSION);
			byteBuffer.writeByte((byte) PROTOCOL_TYPE);
			//--------------HEADER end----------------

			//---------------REQUEST_HEADER start----------
			byteBuffer.writeByte(VERSION);//1B
			byteBuffer.writeByte(RESPONSE);//1B
			byteBuffer.writeByte((byte) wrapper.getCodecType());//1B
			byteBuffer.writeByte((byte) 0);//1B
			byteBuffer.writeByte((byte) 0);//1B
			byteBuffer.writeByte((byte) 0);//1B

			byteBuffer.writeInt(wrapper.getRequestId());
			byteBuffer.writeInt(wrapper.getCodecType() == CommonRpcCodecs.PB_CODEC ? className.length : 0);
			byteBuffer.writeInt(body.length);
			//---------------REQUEST_HEADER end----------
			if (wrapper.getCodecType() == CommonRpcCodecs.PB_CODEC) {
				byteBuffer.writeBytes(className);
			}
			byteBuffer.writeBytes(body);
			return byteBuffer;
		}
	}


	@Override
	public Object decode(RpcByteBuffer wrapper, Object errorObject, int... originPosArray) throws Exception {
		final int originPos;
		if (originPosArray != null && originPosArray.length == 1) {
			originPos = originPosArray[0];
		} else {
			originPos = wrapper.readerIndex();
		}
		if (wrapper.readableBytes() < 2) {
			wrapper.setReaderIndex(originPos);
			return errorObject;
		}
		byte version = wrapper.readByte();
		if (version != (byte) 1) {
			throw new UnsupportedOperationException("protocol version :" + version + " is not supported!");
		}
		byte type = wrapper.readByte();
		if (type == REQUEST) {
			if (wrapper.readableBytes() < REQUEST_HEADER_LEN - 2) {
				wrapper.setReaderIndex(originPos);
				return errorObject;
			}
			int codecType = wrapper.readByte();

			wrapper.readByte();
			wrapper.readByte();
			wrapper.readByte();

			int requestId = wrapper.readInt();
			int timeout = wrapper.readInt();
			int targetInstanceLen = wrapper.readInt();
			int methodNameLen = wrapper.readInt();
			int argsCount = wrapper.readInt();

			int argInfosLen = argsCount * 4 * 2;
			int expectedLenInfoLen = argInfosLen + targetInstanceLen + methodNameLen;
			if (wrapper.readableBytes() < expectedLenInfoLen) {
				wrapper.setReaderIndex(originPos);
				return errorObject;
			}
			int expectedLen = 0;
			int[] argsTypeLen = new int[argsCount];
			for (int i = 0; i < argsCount; i++) {
				argsTypeLen[i] = wrapper.readInt();
				expectedLen += argsTypeLen[i];
			}
			int[] argsLen = new int[argsCount];
			for (int i = 0; i < argsCount; i++) {
				argsLen[i] = wrapper.readInt();
				expectedLen += argsLen[i];
			}
			byte[] targetInstanceByte = new byte[targetInstanceLen];
			wrapper.readBytes(targetInstanceByte);

			byte[] methodNameByte = new byte[methodNameLen];
			wrapper.readBytes(methodNameByte);

			if (wrapper.readableBytes() < expectedLen) {
				wrapper.setReaderIndex(originPos);
				return errorObject;
			}
			byte[][] argTypes = new byte[argsCount][];
			for (int i = 0; i < argsCount; i++) {
				byte[] argTypeByte = new byte[argsTypeLen[i]];
				wrapper.readBytes(argTypeByte);
				argTypes[i] = argTypeByte;
			}
			Object[] args = new Object[argsCount];
			for (int i = 0; i < argsCount; i++) {
				byte[] argByte = new byte[argsLen[i]];
				wrapper.readBytes(argByte);
				args[i] = argByte;
			}

			CommonRpcRequest rpcRequest = new CommonRpcRequest(
					targetInstanceByte, methodNameByte, argTypes, args,
					timeout, requestId, codecType, PROTOCOL_TYPE);
			int messageLen = CommonRpcProtocol.HEADER_LEN + REQUEST_HEADER_LEN + expectedLenInfoLen + expectedLen;
			rpcRequest.setMessageLen(messageLen);
			return rpcRequest;
		} else if (type == RESPONSE) {
			if (wrapper.readableBytes() < RESPONSE_HEADER_LEN - 2) {
				wrapper.setReaderIndex(originPos);
				return errorObject;
			}
			int codecType = wrapper.readByte();
			wrapper.readByte();
			wrapper.readByte();
			wrapper.readByte();

			int requestId = wrapper.readInt();
			int classNameLen = wrapper.readInt();
			int bodyLen = wrapper.readInt();
			if (wrapper.readableBytes() < classNameLen + bodyLen) {
				wrapper.setReaderIndex(originPos);
				return errorObject;
			}

			byte[] classNameBytes = null;
			if (codecType == CommonRpcCodecs.PB_CODEC) {
				classNameBytes = new byte[classNameLen];
				wrapper.readBytes(classNameBytes);
			}
			byte[] bodyBytes = new byte[bodyLen];
			wrapper.readBytes(bodyBytes);

			CommonRpcResponse responseWrapper = new CommonRpcResponse(requestId, codecType, PROTOCOL_TYPE);
			responseWrapper.setResponse(bodyBytes);
			responseWrapper.setResponseClassName(classNameBytes);
			int messageLen = CommonRpcProtocol.HEADER_LEN + RESPONSE_HEADER_LEN + classNameLen + bodyLen;
			responseWrapper.setMessageLen(messageLen);
			return responseWrapper;
		} else {
			throw new UnsupportedOperationException("protocol type : " + type + " is not supported!");
		}
	}

}
