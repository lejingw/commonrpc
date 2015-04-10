/**
 * 
 */
package com.cross.plateform.common.rpc.core.protocol.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cross.plateform.common.rpc.core.all.message.CommonRpcRequest;
import com.cross.plateform.common.rpc.core.all.message.CommonRpcResponse;
import com.cross.plateform.common.rpc.core.bytebuffer.RpcByteBuffer;
import com.cross.plateform.common.rpc.core.codec.all.CommonRpcCodecs;
import com.cross.plateform.common.rpc.core.protocol.RpcProtocol;
import com.cross.plateform.common.rpc.core.protocol.all.CommonRpcProtocol;

/**
 * Common RPC Protocol
 * 
 * Protocol Header
 * 	VERSION(1B): Protocol Version
 *  TYPE(1B):    Protocol Type,so u can custom your protocol
 *  Request Protocol
 * 	VERSION(1B):   
 *  TYPE(1B):      request/response 
 *  CODECTYPE(1B):  serialize/deserialize type
 *  KEEPED(1B):    
 *  KEEPED(1B):    
 *  KEEPED(1B):    
 *  ID(24B):       request id
 *  TIMEOUT(4B):   request timeout
 *  TARGETINSTANCELEN(4B):  target service name length
 *  METHODNAMELEN(4B):      method name length
 *  ARGSCOUNT(4B):          method args count
 *  ARG1TYPELEN(4B):        method arg1 type len
 *  ARG2TYPELEN(4B):        method arg2 type len
 *  ...
 *  ARG1LEN(4B):            method arg1 len
 *  ARG2LEN(4B):            method arg2 len
 *  ...
 *  TARGETINSTANCENAME
 *  METHODNAME
 *  ARG1TYPENAME
 *  ARG2TYPENAME
 *  ...
 *  ARG1
 *  ARG2
 *  ...
 * 
 *  Protocol Header
 * 	VERSION(1B): Protocol Version
 *  TYPE(1B):    Protocol Type,so u can custom your protocol
 *  Response Protocol
 *  VERSION(1B):   
 *  TYPE(1B):      request/response 
 *  DATATYPE(1B):  serialize/deserialize type
 *  KEEPED(1B):    
 *  KEEPED(1B):    
 *  KEEPED(1B):    
 *  ID(24B):        request id
 *  BodyClassNameLen(4B): body className Len
 *  LENGTH(4B):    body length
 *  BodyClassName
 *  BODY if need than set
 *  
 * @author liubing1 
 */
public class DefualtRpcProtocolImpl implements RpcProtocol {

	/**
	 * request id length
	 */
	public static final int REQUEST_ID_LENGTH = 24;
	
	public static final int TYPE = 1;

	private static final Log LOGGER = LogFactory
			.getLog(DefualtRpcProtocolImpl.class);
	
	private static final int REQUEST_HEADER_LEN = 1 * 6 + 4 * 4 + REQUEST_ID_LENGTH;

	private static final int RESPONSE_HEADER_LEN = 1 * 6 + 2 * 4 + REQUEST_ID_LENGTH;

	private static final byte VERSION = (byte) 1;

	private static final byte REQUEST = (byte) 0;

	private static final byte RESPONSE = (byte) 1;

	
	@Override
	public RpcByteBuffer encode(Object message,
			RpcByteBuffer bytebufferWrapper) throws Exception {
		// TODO Auto-generated method stub
		if (!(message instanceof CommonRpcRequest)
				&& !(message instanceof CommonRpcResponse)) {
			throw new Exception(
					"only support send RequestWrapper && ResponseWrapper");
		}
		String id = null;
		byte type = REQUEST;
		if (message instanceof CommonRpcRequest) {
			try {
				int requestArgTypesLen = 0;
				int requestArgsLen = 0;
				List<byte[]> requestArgTypes = new ArrayList<byte[]>();
				List<byte[]> requestArgs = new ArrayList<byte[]>();
				CommonRpcRequest wrapper = (CommonRpcRequest) message;
				byte[][] requestArgTypeStrings = wrapper.getArgTypes();
				for (byte[] requestArgType : requestArgTypeStrings) {
					requestArgTypes.add(requestArgType);
					requestArgTypesLen += requestArgType.length;
				}
				Object[] requestObjects = wrapper.getRequestObjects();
				if (requestObjects != null) {
					for (Object requestArg : requestObjects) {
						byte[] requestArgByte = CommonRpcCodecs.getEncoder(
								wrapper.getCodecType()).encode(requestArg);
						requestArgs.add(requestArgByte);
						requestArgsLen += requestArgByte.length;
					}
				}
				byte[] targetInstanceNameByte = wrapper.getTargetInstanceName();
				byte[] methodNameByte = wrapper.getMethodName();

				
				id = wrapper.getId();
				int timeout = wrapper.getTimeout();
				int capacity = CommonRpcProtocol.HEADER_LEN + REQUEST_HEADER_LEN
						+ requestArgs.size() * 4 * 2
						+ targetInstanceNameByte.length + methodNameByte.length
						+ requestArgTypesLen + requestArgsLen;

				RpcByteBuffer byteBuffer = bytebufferWrapper
						.get(capacity);
				byteBuffer.writeByte(CommonRpcProtocol.CURRENT_VERSION);
				byteBuffer.writeByte((byte) TYPE);
				//--------------HEADER_LEN----------------
				byteBuffer.writeByte(VERSION);//1B
				byteBuffer.writeByte(type);//1B
				byteBuffer.writeByte((byte) wrapper.getCodecType());//1B
				byteBuffer.writeByte((byte) 0);//1B
				byteBuffer.writeByte((byte) 0);//1B
				byteBuffer.writeByte((byte) 0);//1B
				byteBuffer.writeBytes(id.getBytes());
				byteBuffer.writeInt(timeout);//4B
				byteBuffer.writeInt(targetInstanceNameByte.length);//4B

				byteBuffer.writeInt(methodNameByte.length);//4B
				
				byteBuffer.writeInt(requestArgs.size());//4B
				//---------------REQUEST_HEADER_LEN----------
				
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
				LOGGER.error("encode request object error", e);
				throw e;
			}
		} else {
			CommonRpcResponse wrapper = (CommonRpcResponse) message;
			byte[] body = new byte[0];
			byte[] className = new byte[0];
			try {
				// no return object
				if (wrapper.getResponse() != null) {
					className = wrapper.getResponse().getClass().getName()
							.getBytes();
					body = CommonRpcCodecs.getEncoder(wrapper.getCodecType())
							.encode(wrapper.getResponse());
				}
				if (wrapper.isError()) {
					className = wrapper.getException().getClass().getName()
							.getBytes();
					body = CommonRpcCodecs.getEncoder(wrapper.getCodecType())
							.encode(wrapper.getException());
				}
				id = wrapper.getRequestId();
			} catch (Exception e) {
				LOGGER.error("encode response object error", e);
				// still create responsewrapper,so client can get exception
				wrapper.setResponse(new Exception(
						"serialize response object error", e));
				className = Exception.class.getName().getBytes();
				body = CommonRpcCodecs.getEncoder(wrapper.getCodecType())
						.encode(wrapper.getResponse());
			}
			type = RESPONSE;
			int capacity = CommonRpcProtocol.HEADER_LEN + RESPONSE_HEADER_LEN
					+ body.length;
			if (wrapper.getCodecType() == CommonRpcCodecs.PB_CODEC) {
				capacity += className.length;
			}
			RpcByteBuffer byteBuffer = bytebufferWrapper.get(capacity);
			byteBuffer.writeByte(CommonRpcProtocol.CURRENT_VERSION);
			byteBuffer.writeByte((byte) TYPE);
			byteBuffer.writeByte(VERSION);
			byteBuffer.writeByte(type);
			byteBuffer.writeByte((byte) wrapper.getCodecType());
			byteBuffer.writeByte((byte) 0);
			byteBuffer.writeByte((byte) 0);
			byteBuffer.writeByte((byte) 0);
			byteBuffer.writeBytes(id.getBytes());
			if (wrapper.getCodecType() == CommonRpcCodecs.PB_CODEC) {
				byteBuffer.writeInt(className.length);
			} else {
				byteBuffer.writeInt(0);
			}
			byteBuffer.writeInt(body.length);
			if (wrapper.getCodecType() == CommonRpcCodecs.PB_CODEC) {
				byteBuffer.writeBytes(className);
			}
			byteBuffer.writeBytes(body);
			return byteBuffer;
		}
	}

	
	@Override
	public Object decode(RpcByteBuffer wrapper, Object errorObject,
			int... originPosArray) throws Exception {
		// TODO Auto-generated method stub
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
		if (version == (byte) 1) {
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
				
				byte[] requestIdBytes = new byte[REQUEST_ID_LENGTH];
				wrapper.readBytes(requestIdBytes);
				String requestId = new String(requestIdBytes);
				int timeout = wrapper.readInt();
				int targetInstanceLen = wrapper.readInt();

				int methodNameLen = wrapper.readInt();
				int argsCount = wrapper.readInt();
				int argInfosLen = argsCount * 4 * 2;
				int expectedLenInfoLen = argInfosLen + targetInstanceLen
						+ methodNameLen;
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

				CommonRpcRequest rocketRPCRequest = new CommonRpcRequest(
						targetInstanceByte, methodNameByte, argTypes, args,
						timeout, requestId, codecType, TYPE);
				
				int messageLen = CommonRpcProtocol.HEADER_LEN + REQUEST_HEADER_LEN
						+ expectedLenInfoLen + expectedLen;
				rocketRPCRequest.setMessageLen(messageLen);
				return rocketRPCRequest;
				
			} else if (type == RESPONSE) {
				if (wrapper.readableBytes() < RESPONSE_HEADER_LEN - 2) {
					wrapper.setReaderIndex(originPos);
					return errorObject;
				}
				int codecType = wrapper.readByte();
				wrapper.readByte();
				wrapper.readByte();
				wrapper.readByte();
				
				byte[] requestIdBytes = new byte[REQUEST_ID_LENGTH];
				wrapper.readBytes(requestIdBytes);
				String requestId = new String(requestIdBytes);
				
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
				
				CommonRpcResponse responseWrapper = new CommonRpcResponse(
						requestId, codecType, TYPE);
				responseWrapper.setResponse(bodyBytes);
				responseWrapper.setResponseClassName(classNameBytes);
				int messageLen = CommonRpcProtocol.HEADER_LEN + RESPONSE_HEADER_LEN
						+ classNameLen + bodyLen;
				responseWrapper.setMessageLen(messageLen);
				return responseWrapper;
			} else {
				throw new UnsupportedOperationException("protocol type : "
						+ type + " is not supported!");
			}
		} else {
			throw new UnsupportedOperationException("protocol version :"
					+ version + " is not supported!");
		}
	}

}
