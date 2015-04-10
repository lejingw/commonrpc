/**
 * 
 */
package com.cross.plateform.common.rpc.core.all.message;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

import com.cross.plateform.common.rpc.core.codec.all.CommonRpcCodecs;
import com.cross.plateform.common.rpc.core.protocol.impl.DefualtRpcProtocolImpl;
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
	
	private String id = null;
	
	private int protocolType;
	
	private int codecType = CommonRpcCodecs.JAVA_CODEC;
	
	private int messageLen;
	
	private static String clientid = "";                               //requestId的固定前缀， add by 谭耀武
	private static final AtomicLong requestIdSeq = new AtomicLong();   //用来和clientid一起组成requestid
	
	public CommonRpcRequest(byte[] targetInstanceName,byte[] methodName,byte[][] argTypes,
						  Object[] requestObjects,int timeout,int codecType,int protocolType){
		this(targetInstanceName,methodName,argTypes,requestObjects,timeout,get(),codecType,protocolType);
	}

	public CommonRpcRequest(byte[] targetInstanceName,byte[] methodName,byte[][] argTypes,
						  Object[] requestObjects,int timeout,String id,int codecType,int protocolType){
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
	public String getId() {
		return id;
	}

	public byte[][] getArgTypes() {
		return argTypes;
	}
	
	public static String get(){
//		Random random=new Random();
//		
//		return random.nextInt();
		
		String requestid = CommonRpcRequest.getClientid() + requestIdSeq.incrementAndGet();
		return com.cross.plateform.common.rpc.core.util.StringUtils.fixLength(requestid, DefualtRpcProtocolImpl.REQUEST_ID_LENGTH, '1');
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	public static String getClientid()
	{
		return clientid;
	
	}

	public static void setClientid(String clientid)
	{
		CommonRpcRequest.clientid = clientid;
	}
	
	
}
