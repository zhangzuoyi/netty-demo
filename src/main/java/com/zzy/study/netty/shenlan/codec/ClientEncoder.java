package com.zzy.study.netty.shenlan.codec;

import io.netty.channel.ChannelHandlerContext;

public class ClientEncoder extends NettyMessageEncoder {
    @Override
    protected byte[] getSecretKey(ChannelHandlerContext ctx) {
        return ClientDH.secrectKey;
    }
}
