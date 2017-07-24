package com.dfire.soa.netty.encode;

import com.dfire.soa.netty.romoting.Cmd;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author gantang
 * @Date 2017/7/18
 */
public class NettyEncoder extends MessageToByteEncoder<Cmd> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Cmd msg, ByteBuf out) throws Exception {
        out.writeByte(msg.getCode());
        out.writeInt(msg.getBody().length);
        out.writeBytes(msg.getBody());
    }
}
