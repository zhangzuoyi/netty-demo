package com.zzy.study.netty.shenlan.client;

import com.zzy.study.netty.shenlan.client.ChannelAware;
import com.zzy.study.netty.shenlan.message.NettyMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientMessageServer {
    private ChannelAware channelAware;
    private BlockingQueue<NettyMessage> messageQueue = new LinkedBlockingQueue<>();
    ExecutorService executorService= Executors.newFixedThreadPool(10);

    public ClientMessageServer(ChannelAware channelAware) {
        this.channelAware = channelAware;
    }

    public void startSend(){
        executorService.submit(() -> {
            while (true){
                if( ! channelAware.getChannel().isActive()){
                    break;
                }
                try {
                    NettyMessage message=messageQueue.take();
                    System.out.println("take message");
                    channelAware.getChannel().writeAndFlush(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void addSendMessage(NettyMessage message){
        System.out.println("message add");
        messageQueue.add(message);
    }
}
