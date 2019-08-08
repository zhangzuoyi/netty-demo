package com.zzy.study.netty.shenlan.codec;

import com.zzy.study.netty.shenlan.message.*;
import com.zzy.study.netty.shenlan.util.ByteUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public abstract class TcpMessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        out.add(decode(in, getSecretKey(ctx)));
    }

    protected abstract byte[] getSecretKey(ChannelHandlerContext ctx);

    public NettyMessage decode(ByteBuf byteBuf, byte[] secretKey) throws Exception {
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

                ByteBuf inByteBuf = Unpooled.buffer(aesData.length);
                if(header.command == MessageType.SECRET_KEY.value()){
                    inByteBuf.writeBytes(aesData);
                }else{
                    byte[] plainData = AES.decode(aesData, secretKey);
                    if (plainData == null) {
                        //解密失败
                        throw new Exception("解密失败，需要重新连接服务端");
                    }
                    inByteBuf.writeBytes(plainData);
                }

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
                }else if(header.command == MessageType.TASK_START.value()){
                    body = StartTaskMessage.TaskBody.fromByteBuf(inByteBuf);
                }else if(header.command == MessageType.SECRET_KEY.value()){
                    body = SecretKeyMessage.SecretKeyBody.fromByteBuf(inByteBuf,header.dataLength);
                }

            }
            NettyMessage message = new NettyMessage(header, body);

            return message;
    }
}
