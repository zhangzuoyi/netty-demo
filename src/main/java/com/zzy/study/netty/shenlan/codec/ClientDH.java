package com.zzy.study.netty.shenlan.codec;

import java.security.KeyPair;

public class ClientDH {
    public static KeyPair keyPair;//DH 密钥对
    public static byte[] serverPublicKey;//服务端公钥
    public static byte[] secrectKey;//AES 密钥

    public static void genKeyPair(){
        keyPair=JdkDHUtils.initKey();
    }
    public static byte[] getPublicKey(){
        if(keyPair == null){
            genKeyPair();
        }
        return JdkDHUtils.getPublicKey(keyPair);
    }
    public static void genSecretKey(byte[] serverPublicKey){
        ClientDH.serverPublicKey=serverPublicKey;
        secrectKey=JdkDHUtils.getSecretKey(serverPublicKey, JdkDHUtils.getPrivateKey(keyPair));
    }
}
