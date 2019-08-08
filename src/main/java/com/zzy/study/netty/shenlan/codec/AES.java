package com.zzy.study.netty.shenlan.codec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

	private static final String Algorithm = "AES";
	private static final String AlgorithmPadding = "AES/CFB/NoPadding";//算法/模式/补码方式
	public static final byte[] IV="1234567890123456".getBytes();

	public static byte[] encode(byte[] value, byte[] key) {
		try {
			IvParameterSpec sr = new IvParameterSpec(IV);
			SecretKeySpec sk = new SecretKeySpec(key, Algorithm);
			Cipher cipher = Cipher.getInstance(AlgorithmPadding);
			cipher.init(Cipher.ENCRYPT_MODE, sk, sr);
			return cipher.doFinal(value);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static byte[] decode(byte[] value, byte[] key) {
		try {
			IvParameterSpec sr = new IvParameterSpec(IV);
			SecretKeySpec sk = new SecretKeySpec(key, Algorithm);
			Cipher cipher = Cipher.getInstance(AlgorithmPadding);
			cipher.init(Cipher.DECRYPT_MODE, sk, sr);
			return cipher.doFinal(value);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
