package algorithm;

import util.Convert;

public class MD5 {
    private static final int[][] s = {{7, 12, 17, 22}, {5, 9, 14, 20}, {4, 11, 16, 23}, {6, 10, 15, 21}};

    private static int F(int x, int y, int z) {
        return (x & y) | (~x & z);
    }

    private static int G(int x, int y, int z) {
        return (x & z) | (y & ~z);
    }

    private static int H(int x, int y, int z) {
        return x ^ y ^ z;
    }

    private static int I(int x, int y, int z) {
        return y ^ (x | ~z);
    }

    // 循环左移函数，将num循环左移n位
    private static int rotateLeft(int num, int n) {
        return (num << n) | (num >>> (32 - (n)));
    }

    // 四轮运算所需操作
    private static int FF(int a, int b, int c, int d, int x, int s, int ac) {
        a += F(b, c, d) + x + ac;
        a = rotateLeft(a, s);
        a += b;
        return a;
    }

    private static int GG(int a, int b, int c, int d, int x, int s, int ac) {
        a += G(b, c, d) + x + ac;
        a = rotateLeft(a, s);
        a += b;
        return a;
    }

    private static int HH(int a, int b, int c, int d, int x, int s, int ac) {
        a += H(b, c, d) + x + ac;
        a = rotateLeft(a, s);
        a += b;
        return a;
    }

    private static int II(int a, int b, int c, int d, int x, int s, int ac) {
        a += I(b, c, d) + x + ac;
        a = rotateLeft(a, s);
        a += b;
        return a;
    }

    // 存储ABCD四个常数用于计算
    private static int[] state = new int[4];

    // 算法的初始化操作
    private static void initMD5() {
        state = new int[]{
                0x67452301, 0xefcdab89, 0x98badcfe, 0x10325476
        };
    }

    // MD5基本变换函数
    private static void transform(byte[] block) {
        // 取出上次计算得到的值，并将512位再分为16组，每组32位（4字节）
        int a = state[0], b = state[1], c = state[2], d = state[3];
        int[] x = new int[16];

        // 将64字节数据每4字节转换为一个int，得到16个int存入x
        for (int i = 0; i < 16; i++) {
            x[i] = Convert.bytesToInt(block, i, false);
        }

        // 以下按照MD5算法对16组进行4轮运算
        // 第一轮
        a = FF(a, b, c, d, x[0], s[0][0], 0xd76aa478);
        d = FF(d, a, b, c, x[1], s[0][1], 0xe8c7b756);
        c = FF(c, d, a, b, x[2], s[0][2], 0x242070db);
        b = FF(b, c, d, a, x[3], s[0][3], 0xc1bdceee);
        a = FF(a, b, c, d, x[4], s[0][0], 0xf57c0faf);
        d = FF(d, a, b, c, x[5], s[0][1], 0x4787c62a);
        c = FF(c, d, a, b, x[6], s[0][2], 0xa8304613);
        b = FF(b, c, d, a, x[7], s[0][3], 0xfd469501);
        a = FF(a, b, c, d, x[8], s[0][0], 0x698098d8);
        d = FF(d, a, b, c, x[9], s[0][1], 0x8b44f7af);
        c = FF(c, d, a, b, x[10], s[0][2], 0xffff5bb1);
        b = FF(b, c, d, a, x[11], s[0][3], 0x895cd7be);
        a = FF(a, b, c, d, x[12], s[0][0], 0x6b901122);
        d = FF(d, a, b, c, x[13], s[0][1], 0xfd987193);
        c = FF(c, d, a, b, x[14], s[0][2], 0xa679438e);
        b = FF(b, c, d, a, x[15], s[0][3], 0x49b40821);

        // 第二轮
        a = GG(a, b, c, d, x[1], s[1][0], 0xf61e2562);
        d = GG(d, a, b, c, x[6], s[1][1], 0xc040b340);
        c = GG(c, d, a, b, x[11], s[1][2], 0x265e5a51);
        b = GG(b, c, d, a, x[0], s[1][3], 0xe9b6c7aa);
        a = GG(a, b, c, d, x[5], s[1][0], 0xd62f105d);
        d = GG(d, a, b, c, x[10], s[1][1], 0x2441453);
        c = GG(c, d, a, b, x[15], s[1][2], 0xd8a1e681);
        b = GG(b, c, d, a, x[4], s[1][3], 0xe7d3fbc8);
        a = GG(a, b, c, d, x[9], s[1][0], 0x21e1cde6);
        d = GG(d, a, b, c, x[14], s[1][1], 0xc33707d6);
        c = GG(c, d, a, b, x[3], s[1][2], 0xf4d50d87);
        b = GG(b, c, d, a, x[8], s[1][3], 0x455a14ed);
        a = GG(a, b, c, d, x[13], s[1][0], 0xa9e3e905);
        d = GG(d, a, b, c, x[2], s[1][1], 0xfcefa3f8);
        c = GG(c, d, a, b, x[7], s[1][2], 0x676f02d9);
        b = GG(b, c, d, a, x[12], s[1][3], 0x8d2a4c8a);

        // 第三轮
        a = HH(a, b, c, d, x[5], s[2][0], 0xfffa3942);
        d = HH(d, a, b, c, x[8], s[2][1], 0x8771f681);
        c = HH(c, d, a, b, x[11], s[2][2], 0x6d9d6122);
        b = HH(b, c, d, a, x[14], s[2][3], 0xfde5380c);
        a = HH(a, b, c, d, x[1], s[2][0], 0xa4beea44);
        d = HH(d, a, b, c, x[4], s[2][1], 0x4bdecfa9);
        c = HH(c, d, a, b, x[7], s[2][2], 0xf6bb4b60);
        b = HH(b, c, d, a, x[10], s[2][3], 0xbebfbc70);
        a = HH(a, b, c, d, x[13], s[2][0], 0x289b7ec6);
        d = HH(d, a, b, c, x[0], s[2][1], 0xeaa127fa);
        c = HH(c, d, a, b, x[3], s[2][2], 0xd4ef3085);
        b = HH(b, c, d, a, x[6], s[2][3], 0x4881d05);
        a = HH(a, b, c, d, x[9], s[2][0], 0xd9d4d039);
        d = HH(d, a, b, c, x[12], s[2][1], 0xe6db99e5);
        c = HH(c, d, a, b, x[15], s[2][2], 0x1fa27cf8);
        b = HH(b, c, d, a, x[2], s[2][3], 0xc4ac5665);

        // 第四轮
        a = II(a, b, c, d, x[0], s[3][0], 0xf4292244);
        d = II(d, a, b, c, x[7], s[3][1], 0x432aff97);
        c = II(c, d, a, b, x[14], s[3][2], 0xab9423a7);
        b = II(b, c, d, a, x[5], s[3][3], 0xfc93a039);
        a = II(a, b, c, d, x[12], s[3][0], 0x655b59c3);
        d = II(d, a, b, c, x[3], s[3][1], 0x8f0ccc92);
        c = II(c, d, a, b, x[10], s[3][2], 0xffeff47d);
        b = II(b, c, d, a, x[1], s[3][3], 0x85845dd1);
        a = II(a, b, c, d, x[8], s[3][0], 0x6fa87e4f);
        d = II(d, a, b, c, x[15], s[3][1], 0xfe2ce6e0);
        c = II(c, d, a, b, x[6], s[3][2], 0xa3014314);
        b = II(b, c, d, a, x[13], s[3][3], 0x4e0811a1);
        a = II(a, b, c, d, x[4], s[3][0], 0xf7537e82);
        d = II(d, a, b, c, x[11], s[3][1], 0xbd3af235);
        c = II(c, d, a, b, x[2], s[3][2], 0x2ad7d2bb);
        b = II(b, c, d, a, x[9], s[3][3], 0xeb86d391);

        // 更新计算结果
        state[0] = (int) ((state[0] & 0xFFFFFFFFL) + (a & 0xFFFFFFFFL));
        state[1] = (int) ((state[1] & 0xFFFFFFFFL) + (b & 0xFFFFFFFFL));
        state[2] = (int) ((state[2] & 0xFFFFFFFFL) + (c & 0xFFFFFFFFL));
        state[3] = (int) ((state[3] & 0xFFFFFFFFL) + (d & 0xFFFFFFFFL));
    }

    // 计算 MD5 摘要
    public static byte[] encrypt(byte[] message) {
        initMD5();                  // MD5 参数初始化
        int byteLength = message.length;
        long bitsLengthTemp = ((byteLength & 0xFFFFFFFFL) << 3); // 消息的比特长度（乘以8）
        byte[] bitsLength = new byte[8];
        for (int i = 0; i < 8; i++) {
            bitsLength[i] = (byte) (bitsLengthTemp & 0xFF);
            bitsLengthTemp >>>= 8;    // 无符号右移
        }
        int N = byteLength >> 6;                   // 以512位（64字节）为一组，消息包含的完整组数（右移6位，2^6=64）
        int rest = byteLength & 0x3f;              // 剩余不足一组的消息的字节数（即模64操作，0x3f=0b00111111，取消息长度的后6位）
        byte[][] data = new byte[2][64];           // 用于存储剩余部分填充后的数据，最多两组
        boolean moreData = false;                  // 表示剩余部分长度是否大于56字节，大于则还需新增一组，即data数组的第二组也要使用
        System.arraycopy(message, N * 64, data[0], 0, rest);
        byte[] padding = {-128};
        // 如果剩余部分长度小于56字节，则使用Padding填充至64字节，并将消息比特长度填充到最后8字节（64位），组成新的一组，占据data[0]~data[63]
        if (rest < 0x38) {
            System.arraycopy(padding, 0, data[0], rest, 1);
            System.arraycopy(bitsLength, 0, data[0], 56, 8);
        }
	    /* 如果剩余部分长度大于56字节，则使用Padding填充至120(64+56)字节，也就是data第二组的56字节；
	    将消息比特长度填充到最后8字节（64位），组成两组，占据data[0]~data[127] */
        else {
            System.arraycopy(padding, 0, data[0], rest, 1);
            System.arraycopy(bitsLength, 0, data[1], 56, 8);
            moreData = true;
        }
        byte[] buffer = new byte[64];
        // 将 N 组分别进行 algorithm.MD5 变换操作
        for (int i = 0; i < N; i++) {
            System.arraycopy(message, i * 64, buffer, 0, 64);
            transform(buffer);
        }
        transform(data[0]);   // 对data的第一组进行变换
        // 当 data 中第二组也在使用时，对其也进行变换
        if (moreData)
            transform(data[1]);
        byte[] digest = new byte[16];
        // 将 state 进行拆分得到 digest 消息摘要
        for (int i = 0; i < 4; i++) {
            Convert.intToBytes(state[i], digest, i, false);
        }
        return digest;
    }
}