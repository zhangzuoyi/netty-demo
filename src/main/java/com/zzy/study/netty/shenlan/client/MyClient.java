package com.zzy.study.netty.shenlan.client;

import com.zzy.study.netty.shenlan.message.LoginMessage;
import com.zzy.study.netty.shenlan.message.NettyMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class MyClient {
    public static void main(String[] args) throws Exception{
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                    .handler(new MyClientInitializer());

            ChannelFuture channelFuture = bootstrap.connect("192.168.23.53",8888).sync();
            Channel channel=channelFuture.channel();
            NettyMessage msg= LoginMessage.Builder("admin","123456");
            channel.writeAndFlush(msg);
            channelFuture.channel().closeFuture().sync();
        }finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}