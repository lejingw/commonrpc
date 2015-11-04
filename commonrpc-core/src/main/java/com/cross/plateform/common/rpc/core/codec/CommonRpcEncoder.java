package com.cross.plateform.common.rpc.core.codec;

/**
 * 编码
 */
public interface CommonRpcEncoder {

	byte[] encode(Object object) throws Exception;
}
