package com.zzy.study.netty.shenlan.message;

public class HeartBeatMessage {

	public static NettyMessage Builder()  {
		Header header = new Header();
        header.command = MessageType.HEARTBEAT.value();
        header.dataLength = 0;
        return new NettyMessage(header);
    }
}
