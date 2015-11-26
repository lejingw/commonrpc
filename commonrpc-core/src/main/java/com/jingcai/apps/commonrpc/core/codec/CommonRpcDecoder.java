package com.jingcai.apps.commonrpc.core.codec;

/**
 * 解码
 */
public interface CommonRpcDecoder {

	Object decode(String className, byte[] bytes) throws Exception;
}
