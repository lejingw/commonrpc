package com.jingcai.apps.commonrpc.core.codec;

/**
 * 编码
 */
public interface CommonRpcEncoder {

	byte[] encode(Object object) throws Exception;
}
