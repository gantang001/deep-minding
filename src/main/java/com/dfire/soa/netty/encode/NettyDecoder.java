package com.dfire.soa.netty.encode;

import com.dfire.soa.netty.romoting.Cmd;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author gantang
 * @Date 2017/7/18
 */
public class NettyDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < Cmd.MIN_LENGTH) {
            return;
        }
        in.markReaderIndex();
        byte command = in.readByte();
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] body = new byte[dataLength];
        in.readBytes(body);
        Cmd cmd = new Cmd();
        cmd.setBody(body);
        cmd.setCode(command);
        out.add(cmd);
    }
}
