package algorithm;

import java.math.BigInteger;
import java.util.Random;

public class RSA {
    private static final int keyBitLen = 1024;

    public static BigInteger[] genKeyPair() {
        BigInteger p, q, phi, n, d, e;
        // 用于生成 p、q 的随机数
        Random random = new Random();
        do {
            // 密钥长度（n = p * q的二进制长度）
            // 生成大素数 p（二进制位数取密钥长度的一半）
            p = BigInteger.probablePrime(keyBitLen / 2, random);
            do {
                // 生成大素数 q（二进制位数取密钥长度的一半）
                q = BigInteger.probablePrime(keyBitLen / 2, random);
            } while (p.equals(q));   // 确保 p 与 q 不同

            // 计算 n = p * q
            n = p.multiply(q);

            // 计算 φ(n) = (p-1)*(q-1)
            phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

            // 设定 e
            do {
                e = BigInteger.probablePrime(32, random);
            } while (e.compareTo(phi) >= 0);
        } while (!phi.gcd(e).equals(BigInteger.ONE));  // 确保 gcd(e, φ(n)) = 1

        // 计算 d = (e^-1) mod φ(n)，即 d 是 e 模 φ(n) 的逆元
        d = e.modInverse(phi);
        return new BigInteger[]{n, d, e};
    }

    private static byte[] encrypt_decrypt(byte[] msgBytes, BigInteger n, BigInteger d_e) {
        // 字节数组转大整数
        BigInteger msgInteger = new BigInteger(1, msgBytes);
        // 计算明文或密文 res = (m ^ d_e) mod n
        BigInteger res = msgInteger.modPow(d_e, n);
        return res.toByteArray();
    }

    // 加密（结果为128字节）
    public static byte[] encrypt(byte[] msgBytes, BigInteger n, BigInteger d_e) {
        int keyByteLen = keyBitLen / 8;
        byte[] cipher = encrypt_decrypt(msgBytes, n, d_e);
        byte[] res = new byte[keyByteLen];
        if (cipher.length > keyByteLen) {
            System.arraycopy(cipher, cipher.length - keyByteLen, res, 0, keyByteLen);
            return res;
        }
        return cipher;
    }

    // 解密
    public static byte[] decrypt(byte[] msgBytes, BigInteger n, BigInteger d_e) {
        return encrypt_decrypt(msgBytes, n, d_e);
    }

    // 签名（结果为128字节）
    public static byte[] sign(byte[] msgBytes, BigInteger n, BigInteger d) {
        int keyByteLen = keyBitLen / 8;
        byte[] sign = encrypt_decrypt(msgBytes, n, d);
        byte[] res = new byte[keyByteLen];
        if (sign.length > keyByteLen) {
            System.arraycopy(sign, sign.length - keyByteLen, res, 0, keyByteLen);
            return res;
        }
        return sign;
    }

    // 验签
    public static byte[] verify(byte[] msgBytes, BigInteger n, BigInteger e) {
        return encrypt_decrypt(msgBytes, n, e);
    }
}