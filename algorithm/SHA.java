package algorithm;

import util.Convert;

public class SHA {
    // 初始哈希值 H
    public static int[] H;

    // 哈希常量 K
    public static final int[] K = {
            0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5,
            0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
            0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3,
            0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
            0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc,
            0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
            0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7,
            0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
            0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13,
            0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
            0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3,
            0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
            0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5,
            0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
            0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208,
            0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
    };

    private static int rotateRight(int num, int n) {
        return (num >>> n) | (num << (32 - (n)));
    }

    private static int sigma_0(int x) {
        return rotateRight(x, 7) ^ rotateRight(x, 18) ^ (x >>> 3);
    }

    private static int sigma_1(int x) {
        return rotateRight(x, 17) ^ rotateRight(x, 19) ^ (x >>> 10);
    }

    private static int Sigma_0(int x) {
        return rotateRight(x, 2) ^ rotateRight(x, 13) ^ rotateRight(x, 22);
    }

    private static int Sigma_1(int x) {
        return rotateRight(x, 6) ^ rotateRight(x, 11) ^ rotateRight(x, 25);
    }

    private static int Ch(int x, int y, int z) {
        return (x & y) ^ (~x & z);
    }

    private static int Maj(int x, int y, int z) {
        return (x & y) ^ (x & z) ^ (y & z);
    }

    // 初始化 SHA 算法
    private static void initSHA() {
        H = new int[]{
                0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a,
                0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19
        };
    }

    // 主要变换函数
    private static void transform(byte[] buffer) {
        int[] expand = new int[64];
        for (int i = 0; i < 16; i++) {
            expand[i] = Convert.bytesToInt(buffer, i, true);
        }
        for (int i = 16; i < 64; i++) {
            expand[i] = sigma_1(expand[i - 2]) + expand[i - 7] + sigma_0(expand[i - 15]) + expand[i - 16];
        }
        int a = H[0], b = H[1], c = H[2], d = H[3], e = H[4], f = H[5], g = H[6], h = H[7];
        for (int i = 0; i < 64; i++) {
            int T1 = h + Sigma_1(e) + Ch(e, f, g) + K[i] + expand[i];
            int T2 = Sigma_0(a) + Maj(a, b, c);
            h = g;
            g = f;
            f = e;
            e = d + T1;
            d = c;
            c = b;
            b = a;
            a = T1 + T2;
        }
        H[0] += a;
        H[1] += b;
        H[2] += c;
        H[3] += d;
        H[4] += e;
        H[5] += f;
        H[6] += g;
        H[7] += h;
    }

    // 计算 SHA256 摘要
    public static byte[] encrypt(byte[] bytes) {
        initSHA();
        int byteLength = bytes.length;
        long bitsLengthTemp = ((byteLength & 0xFFFFFFFFL) << 3); // 消息的比特长度（乘以8）
        byte[] bitsLength = new byte[8];
        for (int i = 0; i < 8; i++) {
            bitsLength[7 - i] = (byte) (bitsLengthTemp & 0xFF);
            bitsLengthTemp >>>= 8;    // 无符号右移
        }
        int N = byteLength >> 6;                   // 以512位（64字节）为一组，消息包含的完整组数（右移6位，2^6=64）
        int rest = byteLength & 0x3f;              // 剩余不足一组的消息的字节数（即模64操作，0x3f=0b00111111，取消息长度的后6位）
        byte[][] data = new byte[2][64];           // 用于存储剩余部分填充后的数据，最多两组
        boolean moreData = false;                  // 表示剩余部分长度是否大于56字节，大于则还需新增一组，即data数组的第二组也要使用
        System.arraycopy(bytes, N * 64, data[0], 0, rest);
        // 用于填充，首位为1，后续均为0
        byte[] padding = {-128}; // 0b10000000000000000...
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
        for (int i = 0; i < N; i++) {
            System.arraycopy(bytes, i * 64, buffer, 0, 64);
            transform(buffer);
        }
        transform(data[0]);   // 对data的第一组进行变换
        // 当data中第二组也在使用时，对其也进行变换
        if (moreData)
            transform(data[1]);
        byte[] digest = new byte[32];
        // 将 H 进行拆分得到 digest 消息摘要
        for (int i = 0; i < H.length; i++) {
            Convert.intToBytes(H[i], digest, i, true);
        }
        return digest;
    }
}