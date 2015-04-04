/**
 * 
 */
package com.cross.plateform.common.rpc.tcp.netty4.codec;

import com.cross.plateform.common.rpc.core.protocol.all.CommonRpcProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
/**
 * @author liubing1
 *
 */
public class CommonRpcEncoderHandler extends MessageToByteEncoder<Object> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf out)
			throws Exception {
		// TODO Auto-generated method stub
		CommonRpcByteBuffer byteBufferWrapper = new CommonRpcByteBuffer(ctx);
		CommonRpcProtocol.encode(message, byteBufferWrapper);
	    ctx.write(byteBufferWrapper.getBuffer());
	}
	
}
