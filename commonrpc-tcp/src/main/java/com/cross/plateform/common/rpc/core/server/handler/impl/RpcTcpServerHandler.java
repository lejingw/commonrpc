package com.cross.plateform.common.rpc.core.server.handler.impl;

import com.cross.plateform.common.rpc.core.all.message.CommonRpcRequest;
import com.cross.plateform.common.rpc.core.all.message.CommonRpcResponse;
import com.cross.plateform.common.rpc.core.codec.all.CommonRpcCodecs;
import com.cross.plateform.common.rpc.core.server.handler.AbstractRpcTcpServerHandler;
import com.cross.plateform.common.rpc.server.filter.RpcFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcTcpServerHandler extends AbstractRpcTcpServerHandler {
	private static final Logger logger = LoggerFactory.getLogger(RpcTcpServerHandler.class);
	private static final Map<String, RpcFilterServerBean> processors = new ConcurrentHashMap<>();
	private static final Map<String, Method> cacheMethods = new ConcurrentHashMap<String, Method>();

	private RpcTcpServerHandler(){}

	static class Holder{
		private static RpcTcpServerHandler instance = new RpcTcpServerHandler();
	}

	public static RpcTcpServerHandler getInstance(){
		return Holder.instance;
	}

	@Override
	public void registerProcessor(String instanceName, Object instance, RpcFilter rpcFilter) {
		RpcFilterServerBean filterServerBean = new RpcFilterServerBean(instance, rpcFilter);
		processors.put(instanceName, filterServerBean);

		Class<?> instanceClass = instance.getClass();
		Method[] methods = instanceClass.getDeclaredMethods();
		for (Method method : methods) {
			Class<?>[] argTypes = method.getParameterTypes();
			StringBuilder methodKeyBuilder = new StringBuilder();
			methodKeyBuilder.append(instanceName).append("#");
			methodKeyBuilder.append(method.getName()).append("$");
			for (Class<?> argClass : argTypes) {
				methodKeyBuilder.append(argClass.getName()).append("_");
			}
			method.setAccessible(true);
			cacheMethods.put(methodKeyBuilder.toString(), method);
		}
	}

	@Override
	public CommonRpcResponse handleRequest(CommonRpcRequest request, int codecType, int procotolType) {
		CommonRpcResponse respWrapper = new CommonRpcResponse(request.getId(), codecType, procotolType);

		String targetInstanceName = new String(request.getTargetInstanceName());
		RpcFilterServerBean rpcFilterServerBean = processors.get(targetInstanceName);
		if (rpcFilterServerBean == null) {
			respWrapper.setException(new Exception("no " + targetInstanceName + " instance exists on the server"));
			return respWrapper;
		}

		String methodName = new String(request.getMethodName());
		byte[][] argTypeBytes = request.getArgTypes();
		String[] argTypes = new String[argTypeBytes.length];
		for (int i = 0; i < argTypeBytes.length; i++) {
			argTypes[i] = new String(argTypeBytes[i]);
		}


		Object[] requestObjects = new Object[argTypes.length];
		StringBuilder methodKey = new StringBuilder();
		methodKey.append(targetInstanceName).append("#").append(methodName).append("$");
		if (argTypes.length > 0) {
			Object[] tmprequestObjects = request.getRequestObjects();
			for (int i = 0; i < argTypes.length; i++) {
				methodKey.append(argTypes[i]).append("_");
				try {
					requestObjects[i] = CommonRpcCodecs.getDecoder(request.getCodecType()).decode(argTypes[i], (byte[]) tmprequestObjects[i]);
				} catch (Exception e) {
					respWrapper.setException(new Exception(String.format("decode request arg:%s error", methodKey.toString())));
					return respWrapper;
				}
			}
		}

		Method method = null;
		if ((method = cacheMethods.get(methodKey.toString())) == null) {
			respWrapper.setException(new Exception(String.format("no method:%s find in %s on the server", methodKey.toString(), targetInstanceName)));
			return respWrapper;
		}

		try {
			RpcFilter rpcFilter = rpcFilterServerBean.getRpcFilter();
			if (null != rpcFilter) {
				if (!rpcFilter.doBeforeRequest(method, rpcFilterServerBean.getObject(), requestObjects)) {
					respWrapper.setException(new Exception("unvalid request, server has rejected"));
					return respWrapper;
				}
			}
			respWrapper.setResponse(method.invoke(rpcFilterServerBean.getObject(), requestObjects));
			if (null != rpcFilter) {
				rpcFilter.doAfterRequest(respWrapper.getResponse());
			}
		} catch (InvocationTargetException e) {
			logger.error("server handle request error", e.getTargetException());
			respWrapper.setException(e.getTargetException());
		} catch (Exception e) {
			logger.error("server handle request error", e.getCause());
			respWrapper.setException(e.getCause());
		}
		return respWrapper;
	}


	@Override
	public void clear() {
		processors.clear();
		cacheMethods.clear();
	}


	private class RpcFilterServerBean {
		private Object object;
		private RpcFilter rpcFilter;

		public Object getObject() {
			return object;
		}

		public RpcFilter getRpcFilter() {
			return rpcFilter;
		}

		public RpcFilterServerBean(Object object, RpcFilter rpcFilter) {
			this.object = object;
			this.rpcFilter = rpcFilter;
		}
	}
}
