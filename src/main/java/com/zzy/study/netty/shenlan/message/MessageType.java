package com.zzy.study.netty.shenlan.message;

import java.util.HashMap;

public enum MessageType {

    /**
     * 3登录
     */
    LOGIN((short)0x1101);

	private short value;

	MessageType(Short value) {
		this.value = value;
	}

	public short value() {
		return this.value;
	}

	private static HashMap<Short, String> hashMap = new HashMap<Short, String>();
	public static String getName(Short key) {
		if (hashMap.containsKey(key)) {
			return hashMap.get(key);
		}
		return "";
	}
}
