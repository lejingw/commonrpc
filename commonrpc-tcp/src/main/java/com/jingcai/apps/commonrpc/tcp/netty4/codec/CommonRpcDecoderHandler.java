package com.jingcai.apps.commonrpc.tcp.netty4.codec;

import java.util.List;

import com.jingcai.apps.commonrpc.core.protocol.all.CommonRpcProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class CommonRpcDecoderHandler extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
		CommonRpcByteBuffer wrapper = new CommonRpcByteBuffer(buf);
		Object result = CommonRpcProtocol.decode(wrapper, null);

		if (result != null) {
			out.add(result);
		}
	}

}
