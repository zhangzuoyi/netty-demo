package com.zzy.study.netty.shenlan.message;


import com.zzy.study.netty.shenlan.codec.AES;
import io.netty.buffer.ByteBuf;

public class NettyMessage {
	
	public static final int ByteLength = 1;
	public static final int ShortLength = 2;
	public static final int IntLength = 4;
	public static final int Uint8Length = ByteLength;
	public static final int Uint16Length = ShortLength;
	public static final int Uint32Length = IntLength;

    public static final char COVER = '\0';

	public Header header;
	public Body body;
	
	public NettyMessage(Header header) {
		this(header, null);
	}
	
	public NettyMessage(Header header, Body body) {
		this.header = header;
		this.body = body;
	}

	public int length() {
		return Header.LENGTH + (body == null?0:body.length());
	}

	public void encode(ByteBuf out, byte[] secretKey){
		header.encode(out);
		if(body != null){
			byte[] plainData=body.toByteArray();
			if(header.command == MessageType.SECRET_KEY.value()){
				out.writeBytes(plainData);
			}else{
				byte[] encodeData= AES.encode(plainData, secretKey);
				out.writeBytes(encodeData);
			}
		}
	}
}
