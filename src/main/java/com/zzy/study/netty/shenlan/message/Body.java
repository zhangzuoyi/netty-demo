package com.zzy.study.netty.shenlan.message;

import io.netty.buffer.ByteBuf;

public abstract class Body {

    /**
     * body的报文长度
     */
	public abstract int length();

	public abstract byte[] toByteArray();

	public abstract void encode(ByteBuf out);
}
