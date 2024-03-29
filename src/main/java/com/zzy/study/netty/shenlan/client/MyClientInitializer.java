package com.zzy.study.netty.shenlan.client;

import com.zzy.study.netty.shenlan.codec.ClientDecoder;
import com.zzy.study.netty.shenlan.codec.ClientEncoder;
import com.zzy.study.netty.shenlan.codec.NettyMessageEncoder;
import com.zzy.study.netty.shenlan.codec.TcpMessageDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class MyClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

//        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
//        pipeline.addLast(new LengthFieldPrepender(4));
        pipeline.addLast(new ClientDecoder());
        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
//        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
        pipeline.addLast(new ClientEncoder());
        pipeline.addLast(new MyClientHandler());
//        pipeline.addLast(new MyClientNettyMessageHandler());
    }
}