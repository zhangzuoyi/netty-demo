package com.zzy.study.netty.shenlan.codec;

import com.zzy.study.netty.shenlan.message.*;
import com.zzy.study.netty.shenlan.util.ByteUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class TcpMessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        out.add(decode(in));
    }

    public NettyMessage decode(ByteBuf byteBuf) throws Exception {
            int iCode = byteBuf.readInt();
            Header header = new Header();
            if (iCode != header.iCode) {
                throw new Exception("从服务端读到的数据不符合协议");
            }

            boolean needReconnect = false;
            Body body = null;
            header.iCode = iCode;        //固定识别码
            header.command = byteBuf.readShort();
            header.protocolVersion = byteBuf.readShort();
            header.terminalID = byteBuf.readInt();
            header.terminalType = byteBuf.readByte();
            header.receiverID = byteBuf.readInt();
            header.receiverType = byteBuf.readByte();
            header.dataLength = byteBuf.readShort();

            if (header.dataLength > 0) {
                    byte[] aesData = new byte[header.dataLength];
                    byteBuf.readBytes(aesData);
                    byte[] plainData = aesData;
                    if (plainData == null) {
                        //解密失败
                        throw new Exception("解密失败，需要重新连接服务端");
                    }

                    ByteBuf inByteBuf = Unpooled.buffer(plainData.length);
                    inByteBuf.writeBytes(plainData);

                    if (header.command == MessageType.LOGIN.value()) {
                        //登录
                        byte[] bodyByte1 = new byte[LoginMessage.LoginBody.UserNameSize];
                        byte[] bodyByte2 = new byte[LoginMessage.LoginBody.AuthenticationSize];

                        inByteBuf.readBytes(bodyByte1);
                        inByteBuf.readBytes(bodyByte2);

                        byte status = inByteBuf.readByte();
                        body = new LoginMessage.LoginBody(
                                ByteUtils.byteArray2String(bodyByte1, NettyMessage.COVER),
                                ByteUtils.byteArray2String(bodyByte2, NettyMessage.COVER));
                        ((LoginMessage.LoginBody) body).status = status;

                        LoginMessage.LoginBody lb = (LoginMessage.LoginBody) body;
                    }

            }
            NettyMessage message = new NettyMessage(header, body);

            return message;
    }
}
