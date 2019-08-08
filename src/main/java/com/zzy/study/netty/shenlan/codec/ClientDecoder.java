package com.zzy.study.netty.shenlan.codec;

import io.netty.channel.ChannelHandlerContext;

public class ClientDecoder extends TcpMessageDecoder {
    @Override
    protected byte[] getSecretKey(ChannelHandlerContext ctx) {
        return ClientDH.secrectKey;
    }
}
