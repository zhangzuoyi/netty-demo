package com.zzy.study.netty.shenlan.server;

import com.zzy.study.netty.shenlan.codec.JdkDHUtils;
import com.zzy.study.netty.shenlan.message.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

import java.security.KeyPair;
import java.time.LocalTime;
import java.util.UUID;

public class MyServerHandler extends SimpleChannelInboundHandler<NettyMessage>{
    private NettyMessage heartBeatMsg=HeartBeatMessage.Builder();
    public static final AttributeKey<Boolean> LOGIN = AttributeKey.valueOf("login");
    public static final AttributeKey<byte[]> SECRET_KEY = AttributeKey.valueOf("secretKey");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage msg) throws Exception {
        //打印出客户端地址
        System.out.println(ctx.channel().remoteAddress());
        Header header=msg.header;
        short command=header.command;
        System.out.println(header.command+":"+header.receiverID);
        Body body=msg.body;
        Boolean isLogin=ctx.channel().attr(LOGIN).get();
        if(command == MessageType.LOGIN.value()){
            LoginMessage.LoginBody loginBody=(LoginMessage.LoginBody)body;
            System.out.println(loginBody.userName+":"+loginBody.authentication);
            ctx.channel().attr(LOGIN).setIfAbsent(Boolean.TRUE);
            ctx.channel().writeAndFlush(LoginMessage.Builder("test","test123"));
        }else if(command == MessageType.HEARTBEAT.value()){
            System.out.println(LocalTime.now()+": 收到心跳");
            ctx.channel().writeAndFlush(heartBeatMsg);
        }else if(command == MessageType.TASK_START.value()){
            if(isLogin != null && isLogin){
                System.out.println(LocalTime.now()+": 收到开始任务");
                StartTaskMessage.TaskBody taskBody=(StartTaskMessage.TaskBody) body;
                System.out.println(taskBody.taskID+":"+taskBody.startTime);
            }else{
                throw new Exception("未登录");
            }
        }else if(command == MessageType.SECRET_KEY.value()){
            SecretKeyMessage.SecretKeyBody keyBody=(SecretKeyMessage.SecretKeyBody) body;
            KeyPair keyPair=JdkDHUtils.initKey(keyBody.getPubkey());
            byte[] secretKey=JdkDHUtils.getSecretKey(keyBody.getPubkey(), JdkDHUtils.getPrivateKey(keyPair));
            System.out.println("生成密钥："+ JdkDHUtils.encodeHex(secretKey));
            ctx.channel().attr(SECRET_KEY).set(secretKey);
            ctx.channel().writeAndFlush(SecretKeyMessage.Builder(JdkDHUtils.getPublicKey(keyPair)));
        }
//        ctx.channel().writeAndFlush("form server 收到消息: "+ UUID.randomUUID());

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}