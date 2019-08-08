package com.zzy.study.netty.shenlan.codec;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author java小工匠
 */
public class JdkDHUtils {

    public static final String ALGORITHM = "DH";

    // 甲方初始化密钥对
    public static KeyPair initKey() {
        try {
            // 实例化密钥对生成器
            KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM);
            // 初始化密钥对生成器参数 默认1024 512-1024之间64的倍数
            generator.initialize(1024);
            // 产生密钥对
            KeyPair keyPair = generator.genKeyPair();
            return keyPair;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // 乙方初始化密钥生成对
    public static KeyPair initKey(byte[] key) {
        try {
            // 公钥从字节数组转换为PublicKey
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
            // 实例化密钥工厂
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            // 还原甲方的公钥
            DHPublicKey dhPublicKey = (DHPublicKey) keyFactory.generatePublic(keySpec);
            // 剖析甲方公钥，得到其参数
            DHParameterSpec dhParameterSpec = dhPublicKey.getParams();
            // 实例化密钥对生成器
            KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM);
            // 使用得到的参数，初始化密钥生成器
            generator.initialize(dhParameterSpec);
            return generator.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // 获取公钥
    public static byte[] getPublicKey(KeyPair keyPair) {
        byte[] bytes = keyPair.getPublic().getEncoded();
        return bytes;
    }

    // 获取公钥
    public static String getPublicKeyStr(KeyPair keyPair) {
        byte[] bytes = keyPair.getPublic().getEncoded();
        return encodeHex(bytes);
    }

    // 获取私钥
    public static byte[] getPrivateKey(KeyPair keyPair) {
        byte[] bytes = keyPair.getPrivate().getEncoded();
        return bytes;
    }

    // 获取公钥
    public static String getPrivateKeyStr(KeyPair keyPair) {
        byte[] bytes = keyPair.getPrivate().getEncoded();
        return encodeHex(bytes);
    }

    // 生成本地密钥
    public static byte[] getSecretKey(byte[] publicKey, byte[] privateKey) {
        try {
            // 实例化密钥工厂
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            // 将公钥从字节数组转换为PublicKey
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(keySpec);
            // 将私钥从字节数组转换为PrivateKey
            PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privateKey);
            PrivateKey priKey = keyFactory.generatePrivate(privateSpec);
            // 先实例化KeyAgreement
            KeyAgreement keyAgreement = KeyAgreement.getInstance(ALGORITHM);
            // 用自己的私钥初始化keyAgreement
            keyAgreement.init(priKey);
            // 结合对方的公钥进行运算
            keyAgreement.doPhase(pubKey, true);
            // 开始生成本地密钥SecretKey 密钥算法为对称密码算法
            SecretKey key = keyAgreement.generateSecret("AES");
            return key.getEncoded();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // 数据准16进制编码
    public static String encodeHex(final byte[] data) {
        return encodeHex(data, true);
    }

    // 数据转16进制编码
    public static String encodeHex(final byte[] data, final boolean toLowerCase) {
        final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        final char[] toDigits = toLowerCase ? DIGITS_LOWER : DIGITS_UPPER;
        final int l = data.length;
        final char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return new String(out);
    }

    public static void main(String[] args) {
        KeyPair keyPair1 = initKey();
        byte[] publicKey1 = getPublicKey(keyPair1);
        String publicKeyStr1 = encodeHex(publicKey1);
        byte[] privateKey1 = getPrivateKey(keyPair1);
        String privateKeyStr1 = encodeHex(privateKey1);
        System.out.println("甲方公钥:" + publicKeyStr1);
        System.out.println("甲方私钥:" + privateKeyStr1);
        KeyPair keyPair2 = initKey(publicKey1);
        byte[] publicKey2 = getPublicKey(keyPair2);
        String publicKeyStr2 = encodeHex(publicKey2);
        byte[] privateKey2 = getPrivateKey(keyPair2);
        String privateKeyStr2 = encodeHex(privateKey2);
        System.out.println("乙方公钥:" + publicKeyStr2);
        System.out.println("乙方私钥:" + privateKeyStr2);
        byte[] secrectKey1 = getSecretKey(publicKey2, privateKey1);
        byte[] secrectKey2 = getSecretKey(publicKey1, privateKey2);
        String secrectKeyStr1 = encodeHex(secrectKey1);
        String secrectKeyStr2 = encodeHex(secrectKey2);
        System.out.println("甲方协议密钥:" + secrectKeyStr1);
        System.out.println("乙方协议密钥:" + secrectKeyStr2);
        System.out.println("甲=乙:" + secrectKeyStr1.equals(secrectKeyStr2));

    }
}