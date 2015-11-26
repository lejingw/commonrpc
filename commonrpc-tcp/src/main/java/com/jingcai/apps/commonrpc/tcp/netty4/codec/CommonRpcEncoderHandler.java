package com.jingcai.apps.commonrpc.tcp.netty4.codec;

import com.jingcai.apps.commonrpc.core.protocol.all.CommonRpcProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class CommonRpcEncoderHandler extends MessageToByteEncoder<Object> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf out) throws Exception {
		CommonRpcByteBuffer byteBufferWrapper = new CommonRpcByteBuffer(ctx);
		CommonRpcProtocol.encode(message, byteBufferWrapper);
		ctx.write(byteBufferWrapper.getBuffer());
	}

}
