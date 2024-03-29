package com.zzy.study.netty.shenlan.server;

import com.zzy.study.netty.shenlan.codec.NettyMessageEncoder;
import com.zzy.study.netty.shenlan.codec.ServerDecoder;
import com.zzy.study.netty.shenlan.codec.ServerEncoder;
import com.zzy.study.netty.shenlan.codec.TcpMessageDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;


public class MyServerInitializer extends ChannelInitializer<SocketChannel>{
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

//        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
//        pipeline.addLast(new LengthFieldPrepender(4));
        //字符串解码
//        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
        //字符串编码
        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
        pipeline.addLast(new ServerEncoder());

        pipeline.addLast(new ServerDecoder());
        //自己定义的处理器
        pipeline.addLast(new MyServerHandler());
    }
}