package com.zzy.study.netty.shenlan.client;

import io.netty.channel.Channel;

public interface ChannelAware {
    void setChannel(Channel channel);
    Channel getChannel();
}
