package com.zzy.study.netty.shenlan.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.StringUtils;

public class LoginMessage {

	public static NettyMessage Builder(String userName, String authentication) {
		LoginBody body = new LoginBody(userName, authentication);
		Header header = new Header();
        header.command = MessageType.LOGIN.value();
        header.dataLength = (short) body.length();
        return new NettyMessage(header, body);
    }

    public static class LoginBody extends Body {

        public static final int UserNameSize = 33;
        public static final int AuthenticationSize = 33;

        public byte status = 0; 
        public String userName = "";
        public String authentication = "";
        
        public void setUserName(String userName) {
            this.userName = StringUtils.rightPad(userName, UserNameSize, NettyMessage.COVER);
        }
        
        public void setAuthentication(String authentication) {
        	this.authentication = StringUtils.rightPad(authentication, AuthenticationSize, NettyMessage.COVER);
        }
        
        public LoginBody(String userName, String authentication) {
        	setUserName(userName);
        	setAuthentication(authentication);
        	status = 0;
        }

        @Override
        public int length() {
            return UserNameSize + AuthenticationSize + NettyMessage.Uint8Length;
        }

        @Override
        public byte[] toByteArray()  {
            ByteBuf out = Unpooled.buffer(length());
            out.writeBytes(userName.getBytes());
            out.writeBytes(authentication.getBytes());
            out.writeByte(status);
            return out.array();
        }

        @Override
        public void encode(ByteBuf out) {
            out.writeBytes(userName.getBytes());
            out.writeBytes(authentication.getBytes());
            out.writeByte(status);
        }
    }
}
