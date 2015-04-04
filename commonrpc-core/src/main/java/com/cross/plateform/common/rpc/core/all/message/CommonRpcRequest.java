/**
 * 
 */
package com.cross.plateform.common.rpc.core.all.message;

import java.io.Serializable;
import java.util.Random;
import com.cross.plateform.common.rpc.core.codec.all.CommonRpcCodecs;
/**
 * @author liubing1
 *
 */
public class CommonRpcRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3554311529871950375L;
	
	
	private byte[] targetInstanceName;
	
	private byte[] methodName;
	
	private byte[][] argTypes;

	private Object[] requestObjects = null;
	
	private Object message = null;
	
	private int timeout = 0;
	
	private int id = 0;
	
	private int protocolType;
	
	private int codecType = CommonRpcCodecs.JAVA_CODEC;
	
	private int messageLen;
	
	public CommonRpcRequest(byte[] targetInstanceName,byte[] methodName,byte[][] argTypes,
						  Object[] requestObjects,int timeout,int codecType,int protocolType){
		this(targetInstanceName,methodName,argTypes,requestObjects,timeout,get(),codecType,protocolType);
	}

	public CommonRpcRequest(byte[] targetInstanceName,byte[] methodName,byte[][] argTypes,
						  Object[] requestObjects,int timeout,int id,int codecType,int protocolType){
		this.requestObjects = requestObjects;
		this.id = id;
		this.timeout = timeout;
		this.targetInstanceName = targetInstanceName;
		this.methodName = methodName;
		this.argTypes = argTypes;
		this.codecType = codecType;
		this.protocolType = protocolType;

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
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	public byte[][] getArgTypes() {
		return argTypes;
	}
	
	public static int get(){
		Random random=new Random();
		
		return random.nextInt();
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	
}
