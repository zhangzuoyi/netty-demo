package com.zzy.study.netty.shenlan.util;


import com.zzy.study.netty.shenlan.message.NettyMessage;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class ByteUtils {
	
	public static String byteArray2String(byte[] data, char cover) {
        int index = 0;
        for (int i = 0;i < data.length;i ++) {
            if (data[i] == cover) {
                index = i;
                break;
            }
        }
        if (index == 0) {
            return new String(data);
        }
        byte[] array = new byte[index];
        System.arraycopy(data, 0, array, 0, index);
        return new String(array);
    }

    /**
     * 字符串转换成byte数组，长度不够用cover填充
     * @param str
     * @param size
     * @param cover
     * @return
     */
    public static byte[] string2ByteArray(String str, int size, byte cover){
        byte[] result=new byte[size];
        Arrays.fill(result, cover);
        byte[] bytes= new byte[0];
        try {
            bytes = str.getBytes("utf8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(size>=bytes.length){
            System.arraycopy(bytes,0,result,0,bytes.length);
        }
        return result;
    }
    public static byte[] string2ByteArray(String str, int size){
        return string2ByteArray(str, size, (byte) NettyMessage.COVER);
    }
}
