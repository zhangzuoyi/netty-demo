package com.zzy.study.netty.shenlan.codec;

import com.zzy.study.netty.shenlan.message.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public abstract class NettyMessageEncoder extends MessageToByteEncoder<NettyMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, NettyMessage msg, ByteBuf out) throws Exception {
        msg.encode(out, getSecretKey(ctx));
    }

    protected abstract byte[] getSecretKey(ChannelHandlerContext ctx);
}
