package com.zzy.study.netty.shenlan.codec;

import com.zzy.study.netty.shenlan.server.MyServerHandler;
import io.netty.channel.ChannelHandlerContext;

public class ServerEncoder extends NettyMessageEncoder {
    @Override
    protected byte[] getSecretKey(ChannelHandlerContext ctx) {

        return ctx.channel().attr(MyServerHandler.SECRET_KEY).get();
    }
}
