package com.zzy.study.netty.shenlan.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;

public class SecretKeyMessage {

	public static NettyMessage Builder(byte[] publicKey) {

		SecretKeyBody body = new SecretKeyBody(publicKey);
		Header header = new Header();
		header.command = MessageType.SECRET_KEY.value();
		header.dataLength = (short)body.length();
		return new NettyMessage(header, body);
	}

	@Data
	public static class SecretKeyBody extends Body {

		public static final int PubkeySize = 296;//128
		
		private byte[] pubkey;
		
		public SecretKeyBody(byte[] pubkey) {
			this.pubkey = pubkey;
		}

		@Override
		public int length() {
//			return PubkeySize ;
			return pubkey.length;
		}

		@Override
		public byte[] toByteArray() {
			ByteBuf out = Unpooled.buffer(length());
			out.writeBytes(pubkey);

			return out.array();
		}

		@Override
		public void encode(ByteBuf out) {
			out.writeBytes(pubkey);
		}

		public static SecretKeyBody fromByteBuf(ByteBuf byteBuf, int length){
			try {
				byte[] pubkey=new byte[length];

				byteBuf.readBytes(pubkey);

				return new SecretKeyBody(pubkey);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
