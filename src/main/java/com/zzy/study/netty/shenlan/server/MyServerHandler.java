package com.zzy.study.netty.shenlan.server;

import com.zzy.study.netty.shenlan.message.Body;
import com.zzy.study.netty.shenlan.message.Header;
import com.zzy.study.netty.shenlan.message.LoginMessage;
import com.zzy.study.netty.shenlan.message.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.UUID;

public class MyServerHandler extends SimpleChannelInboundHandler<NettyMessage>{

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage msg) throws Exception {
        //打印出客户端地址
        System.out.println(ctx.channel().remoteAddress());
        Header header=msg.header;
        System.out.println(header.command+":"+header.receiverID);
        Body body=msg.body;
        if(body instanceof LoginMessage.LoginBody){
            LoginMessage.LoginBody loginBody=(LoginMessage.LoginBody)body;
            System.out.println(loginBody.userName+":"+loginBody.authentication);
        }
        ctx.channel().writeAndFlush("form server 收到消息: "+ UUID.randomUUID());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}