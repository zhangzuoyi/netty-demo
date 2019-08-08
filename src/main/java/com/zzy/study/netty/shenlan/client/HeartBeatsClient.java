package com.zzy.study.netty.shenlan.client;

import com.zzy.study.netty.shenlan.codec.*;
import com.zzy.study.netty.shenlan.idle.*;
import com.zzy.study.netty.shenlan.message.NettyMessage;
import com.zzy.study.netty.shenlan.message.SecretKeyMessage;
import com.zzy.study.netty.shenlan.message.StartTaskMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.HashedWheelTimer;

import java.time.LocalTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class HeartBeatsClient implements ChannelAware, ServerListener {
    
    protected final HashedWheelTimer timer = new HashedWheelTimer();
    
    private Bootstrap boot;

    private volatile Channel channel;
    
    private final ConnectorIdleStateTrigger idleStateTrigger = new ConnectorIdleStateTrigger();

    public void connect(int port, String host) throws Exception {
        
        EventLoopGroup group = new NioEventLoopGroup();  
        
        boot = new Bootstrap();
        boot.group(group).channel(NioSocketChannel.class).handler(new LoggingHandler(LogLevel.INFO));
            
        final ConnectionWatchdog watchdog = new ConnectionWatchdog(boot, timer, port,host, true, this) {

                public ChannelHandler[] handlers() {
                    return new ChannelHandler[] {
                            this,
                            new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS),
                            idleStateTrigger,
                            new ClientDecoder(),
                            new StringDecoder(CharsetUtil.UTF_8),
                            new ClientEncoder(),
                            new MyClientNettyMessageHandler(HeartBeatsClient.this)
                    };
                }
            };

//        try{
//            Bootstrap bootstrap = new Bootstrap();
//            bootstrap.group(group).channel(NioSocketChannel.class)
//                    .handler(new ChannelInitializer<Channel>() {
//                        @Override
//                        protected void initChannel(Channel ch) throws Exception {
//                            ChannelPipeline pipeline = ch.pipeline();
//                            pipeline.addLast(watchdog.handlers());
////                            pipeline.addLast(new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS));
////                            pipeline.addLast(idleStateTrigger);
////                            pipeline.addLast(new TcpMessageDecoder());
////                            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
////                            pipeline.addLast(new NettyMessageEncoder());
////                            pipeline.addLast(new MyClientHandler());
////                            pipeline.addLast(new MyClientNettyMessageHandler());
//                        }
//                    });
//
//            ChannelFuture channelFuture = bootstrap.connect("192.168.20.168",8899).sync();
//            Channel channel=channelFuture.channel();
//            NettyMessage msg= LoginMessage.Builder("admin","123456");
//            channel.writeAndFlush(msg);
//            channelFuture.channel().closeFuture().sync();
//        }finally {
//            group.shutdownGracefully();
//        }
        doConnect(port, host, watchdog);
    }
    private void doConnect(int port, String host, ConnectionWatchdog watchdog){
        ChannelFuture future;
        //进行连接
        try {
            synchronized (boot) {
                boot.handler(new ChannelInitializer<Channel>() {

                    //初始化channel
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(watchdog.handlers());
                    }
                });

                future = boot.connect(host,port);
                future.addListener(new ChannelFutureListener(){

                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if(future.isSuccess()){
                            System.out.println("成功");
                            System.out.println("send LoginMessage");
                            setChannel(future.channel());
//                            getChannel().writeAndFlush(LoginMessage.Builder("admin","admin123"));
//                            startSendTask();
                        }else{
                            System.out.println("失败");
                            Thread.sleep(2000);
                            System.out.println("尝试重新连接");
                            doConnect(port, host, watchdog);
                        }
                    }
                });

            }

            // 以下代码在synchronized同步块外面是安全的
            future.channel().closeFuture().sync();
        } catch (Throwable t) {
            System.out.println("连接失败");
//                throw new Exception("connects to  fails", t);
        }
    }

    private void startSendTask(){
        ClientMessageServer messageServer=new ClientMessageServer(this);
        messageServer.startSend();
        new Thread(() -> {
            Random random=new Random();
            for(int i=0;i<10;i++){
                NettyMessage message=StartTaskMessage.Builder(1, random.nextInt(500), LocalTime.now().toString());
                messageServer.addSendMessage(message);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void setChannel(Channel channel) {
        this.channel=channel;
        //密钥协商
        this.channel.writeAndFlush(SecretKeyMessage.Builder(ClientDH.getPublicKey()));
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        int port = 8899;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // 采用默认值
            }
        }
        HeartBeatsClient client=new HeartBeatsClient();
        client.connect(port, "localhost");//"192.168.20.168"

    }


    @Override
    public void loginSuccess() {
        startSendTask();
    }
}
