package com.zzy.study.netty.shenlan.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Header {
	
	public static final int LENGTH = 20;
	
    public int iCode = 0x7fb96ae9;   //固定识别码
    public short command = 0;   //命令字
    public short protocolVersion = 0x0010;  //协议版本
    public int terminalID = 0;     //终端ID

    public byte terminalType = 2;   ///终端类型(1为设备，2为JavaServer，3为客户终端)
    public int receiverID = 0;     //转发目标ID
    public byte receiverType = 1;   ///转发消息的接收终端类型(1为设备，2为JavaServer，3为客户终端)
    public short dataLength = 0;


    public byte[] toByteArray() {
        return toByteBuf().array();
    }

    private ByteBuf toByteBuf()  {
        ByteBuf out = Unpooled.buffer(LENGTH);
        out.writeInt(iCode);
        out.writeShort(command);
        out.writeShort(protocolVersion);
        out.writeInt(terminalID);
        out.writeByte(terminalType);
        out.writeInt(receiverID);
        out.writeByte(receiverType);
        out.writeShort(dataLength);
        return out;
    }

    public void encode(ByteBuf out)  {
        out.writeInt(iCode);
        out.writeShort(command);
        out.writeShort(protocolVersion);
        out.writeInt(terminalID);
        out.writeByte(terminalType);
        out.writeInt(receiverID);
        out.writeByte(receiverType);
        out.writeShort(dataLength);
    }
}
