package com.cross.plateform.common.rpc.core.client;

import com.cross.plateform.common.rpc.core.all.message.CommonRpcRequest;
import com.cross.plateform.common.rpc.core.all.message.CommonRpcResponse;
import com.cross.plateform.common.rpc.core.codec.all.CommonRpcCodecs;
import com.cross.plateform.common.rpc.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class AbstractRpcClient implements RpcClient {
	private static final Logger logger = LoggerFactory.getLogger(AbstractRpcClient.class);

	@Override
	public Object invokeImpl(String targetInstanceName, String methodName,
							 String[] argTypes, Object[] args, int timeout, int codecType,
							 int protocolType) throws Exception {
		byte[][] argTypeBytes = new byte[argTypes.length][];
		for (int i = 0; i < argTypes.length; i++) {
			argTypeBytes[i] = argTypes[i].getBytes();
		}

		CommonRpcRequest wrapperRequest = new CommonRpcRequest(targetInstanceName.getBytes(),
				methodName.getBytes(), argTypeBytes, args, timeout, codecType, protocolType);
		return invokeImplIntern(wrapperRequest);
	}

	private Object invokeImplIntern(CommonRpcRequest request) throws Exception {
		long beginTime = System.currentTimeMillis();
		LinkedBlockingQueue<Object> responseQueue = new LinkedBlockingQueue<Object>(1);
		getClientFactory().putResponse(request.getId(), responseQueue);
		try {
			sendRequest(request);
			logger.debug("client ready to send message,request id: " + request.getId());
		} catch (Exception e) {
			throw new RuntimeException("send request to os sendbuffer error", e);
		}

		Object result = null;
		try {
			result = responseQueue.poll(
					request.getTimeout() - (System.currentTimeMillis() - beginTime),
					TimeUnit.MILLISECONDS);
		} finally {
			getClientFactory().removeResponse(request.getId());
		}

		CommonRpcResponse response = null;
		if (result == null) {
			if ((System.currentTimeMillis() - beginTime) <= request.getTimeout()) {//返回null
				response = new CommonRpcResponse(request.getId(), request.getCodecType(), request.getProtocolType());
			} else {//超时
				String errorMsg = "receive response timeout(" + request.getTimeout() + " ms), server is: "
						+ getServerIP() + ":" + getServerPort() + " request id is:" + request.getId();
				logger.error(errorMsg);
				response = new CommonRpcResponse(request.getId(), request.getCodecType(), request.getProtocolType());
				response.setException(new Throwable(errorMsg));
			}
		} else if (result != null) {
			response = (CommonRpcResponse) result;
		}
		try {
			if (response.getResponse() instanceof byte[]) {
				String responseClassName = null;
				if (response.getResponseClassName() != null) {
					responseClassName = new String(response.getResponseClassName());
				}
				if (((byte[]) response.getResponse()).length == 0) {
					response.setResponse(null);
				} else {
					Object responseObject = CommonRpcCodecs.getDecoder(response.getCodecType()).decode(
							responseClassName, (byte[]) response.getResponse());
					if (responseObject instanceof Throwable) {
						response.setException((Throwable) responseObject);
					} else {
						response.setResponse(responseObject);
					}
				}
			}
		} catch (Exception e) {
			throw new Exception("Deserialize response object error", e);
		}
		if (!StringUtils.isNullOrEmpty(response.getException())) {
			Throwable t = response.getException();
			String errorMsg = "server error,server is: " + getServerIP() + ":" + getServerPort() + " request id is:" + request.getId();
			logger.error(errorMsg, t);
			return null;
		}
		return response.getResponse();
	}

	/**
	 * 发送请求
	 *
	 * @param commonRpcRequest
	 * @throws Exception
	 */
	public abstract void sendRequest(CommonRpcRequest commonRpcRequest) throws Exception;

	/**
	 * 销毁消息
	 *
	 * @throws Exception
	 */
	public abstract void destroy() throws Exception;
}
