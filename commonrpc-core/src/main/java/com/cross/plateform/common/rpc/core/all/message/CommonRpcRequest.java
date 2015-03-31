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
	
	//秘钥
	private byte[] token;
	

	public CommonRpcRequest(byte[] targetInstanceName,byte[] methodName,byte[][] argTypes,
						  Object[] requestObjects,int timeout,int codecType,int protocolType,byte[] token){
		this(targetInstanceName,methodName,argTypes,requestObjects,timeout,get(),codecType,protocolType,token);
	}

	public CommonRpcRequest(byte[] targetInstanceName,byte[] methodName,byte[][] argTypes,
						  Object[] requestObjects,int timeout,int id,int codecType,int protocolType,byte[] token){
		this.requestObjects = requestObjects;
		this.id = id;
		this.timeout = timeout;
		this.targetInstanceName = targetInstanceName;
		this.methodName = methodName;
		this.argTypes = argTypes;
		this.codecType = codecType;
		this.protocolType = protocolType;
		this.token=token;
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
	
	private static int get(){
		Random random=new Random();
		
		return random.nextInt();
	}
	
	public static void main(String[] args) {
		Random random=new Random();
		int index=random.nextInt();
		System.out.print(index);
	}
	/**
	 * @return the token
	 */
	public byte[] getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(byte[] token) {
		this.token = token;
	}
	
	
}
