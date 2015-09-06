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
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRpcClient.class);

    @Override
    public Object invokeImpl(String targetInstanceName, String methodName,
                             String[] argTypes, Object[] args, int timeout, int codecType,
                             int protocolType) throws Exception {
        byte[][] argTypeBytes = new byte[argTypes.length][];
        for (int i = 0; i < argTypes.length; i++) {
            argTypeBytes[i] = argTypes[i].getBytes();
        }

        CommonRpcRequest wrapper = new CommonRpcRequest(targetInstanceName.getBytes(),
                methodName.getBytes(), argTypeBytes, args, timeout, codecType, protocolType);
        return invokeImplIntern(wrapper);
    }

    private Object invokeImplIntern(CommonRpcRequest rocketRPCRequest) throws Exception {
        long beginTime = System.currentTimeMillis();
        LinkedBlockingQueue<Object> responseQueue = new LinkedBlockingQueue<Object>(1);
        getClientFactory().putResponse(rocketRPCRequest.getId(), responseQueue);


        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("client ready to send message,request id: " + rocketRPCRequest.getId());
            }
            sendRequest(rocketRPCRequest);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("client write message to send buffer,wait for response,request id: " + rocketRPCRequest.getId());
            }
        } catch (Exception e) {
            LOGGER.error("send request to os sendbuffer error", e);
            throw new RuntimeException("send request to os sendbuffer error", e);
        }

        Object result = null;
        try {
            result = responseQueue.poll(
                    rocketRPCRequest.getTimeout() - (System.currentTimeMillis() - beginTime),
                    TimeUnit.MILLISECONDS);
            //System.out.println("pool时间:"+(System.currentTimeMillis() - beginTime));
        } finally {
            getClientFactory().removeResponse(rocketRPCRequest.getId());
        }

        CommonRpcResponse commonRPCResponse = null;
        if (result == null && (System.currentTimeMillis() - beginTime) <= rocketRPCRequest.getTimeout()) {//返回结果集为null
            commonRPCResponse = new CommonRpcResponse(rocketRPCRequest.getId(), rocketRPCRequest.getCodecType(), rocketRPCRequest.getProtocolType());
        } else if (result == null && (System.currentTimeMillis() - beginTime) > rocketRPCRequest.getTimeout()) {//结果集超时
            String errorMsg = "receive response timeout("
                    + rocketRPCRequest.getTimeout() + " ms),server is: "
                    + getServerIP() + ":" + getServerPort()
                    + " request id is:" + rocketRPCRequest.getId();
            LOGGER.error(errorMsg);
            commonRPCResponse = new CommonRpcResponse(rocketRPCRequest.getId(), rocketRPCRequest.getCodecType(), rocketRPCRequest.getProtocolType());
            commonRPCResponse.setException(new Throwable(errorMsg));
        } else if (result != null) {
            commonRPCResponse = (CommonRpcResponse) result;
        }

        try {
            if (commonRPCResponse.getResponse() instanceof byte[]) {
                String responseClassName = null;
                if (commonRPCResponse.getResponseClassName() != null) {
                    responseClassName = new String(commonRPCResponse.getResponseClassName());
                }
                if (((byte[]) commonRPCResponse.getResponse()).length == 0) {
                    commonRPCResponse.setResponse(null);
                } else {
                    Object responseObject = CommonRpcCodecs.getDecoder(commonRPCResponse.getCodecType()).decode(
                            responseClassName, (byte[]) commonRPCResponse.getResponse());
                    if (responseObject instanceof Throwable) {
                        commonRPCResponse.setException((Throwable) responseObject);
                    } else {
                        commonRPCResponse.setResponse(responseObject);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Deserialize response object error", e);
            throw new Exception("Deserialize response object error", e);
        }

        if (!StringUtils.isNullOrEmpty(commonRPCResponse.getException())) {
            Throwable t = commonRPCResponse.getException();
            //t.fillInStackTrace();
            String errorMsg = "server error,server is: " + getServerIP()
                    + ":" + getServerPort() + " request id is:"
                    + rocketRPCRequest.getId();
            LOGGER.error(errorMsg, t);
            //destroy();
            //throw new Exception(errorMsg, t);
            return null;
        }

        return commonRPCResponse.getResponse();
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
