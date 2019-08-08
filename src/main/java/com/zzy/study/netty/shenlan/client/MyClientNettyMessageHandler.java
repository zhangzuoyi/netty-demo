package com.zzy.study.netty.shenlan.client;

import com.zzy.study.netty.shenlan.codec.ClientDH;
import com.zzy.study.netty.shenlan.codec.JdkDHUtils;
import com.zzy.study.netty.shenlan.message.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.time.LocalDateTime;


public class MyClientNettyMessageHandler extends SimpleChannelInboundHandler<NettyMessage>{
    private ServerListener listener;

    public MyClientNettyMessageHandler(ServerListener listener){
        this.listener=listener;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage msg) throws Exception {
        //服务端的远程地址
        System.out.println(ctx.channel().remoteAddress());
        Header header=msg.header;
        System.out.println(header.command+":"+header.receiverID);
        Body body=msg.body;
        short command=header.command;
        if(command == MessageType.LOGIN.value()){
            LoginMessage.LoginBody loginBody=(LoginMessage.LoginBody)body;
            System.out.println(loginBody.userName+":"+loginBody.authentication);
            //登录成功，触发消息发送任务
            listener.loginSuccess();
        }else if(command == MessageType.HEARTBEAT.value()){
            System.out.println(LocalDateTime.now()+": 收到服务端心跳");
        }else if(command == MessageType.SECRET_KEY.value()){
            SecretKeyMessage.SecretKeyBody keyBody=(SecretKeyMessage.SecretKeyBody) body;
            ClientDH.genSecretKey(keyBody.getPubkey());
            System.out.println("生成密钥："+ JdkDHUtils.encodeHex(ClientDH.secrectKey));
            //交换密钥后登录
            ctx.channel().writeAndFlush(LoginMessage.Builder("admin","admin123"));
        }
//        ctx.writeAndFlush("from client: "+ LocalDateTime.now());
    }

    /**
     * 当服务器端与客户端进行建立连接的时候会触发，如果没有触发读写操作，则客户端和客户端之间不会进行数据通信，也就是channelRead0不会执行，
     * 当通道连接的时候，触发channelActive方法向服务端发送数据触发服务器端的handler的channelRead0回调，然后
     * 服务端向客户端发送数据触发客户端的channelRead0，依次触发。
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        ctx.writeAndFlush("来自与客户端的问题!");
        ctx.fireChannelActive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("掉线了...");
        super.channelInactive(ctx);
    }
}