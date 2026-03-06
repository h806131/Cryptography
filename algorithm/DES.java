package algorithm;

import util.Convert;

public class DES {
    // 初始置换 IP 表
    public static final int[] IP = {
            58, 50, 42, 34, 26, 18, 10, 2,
            60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6,
            64, 56, 48, 40, 32, 24, 16, 8,
            57, 49, 41, 33, 25, 17, 9, 1,
            59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5,
            63, 55, 47, 39, 31, 23, 15, 7
    };
    // 逆初始置换 IP^-1 表
    public static final int[] IP_INV = {
            40, 8, 48, 16, 56, 24, 64, 32,
            39, 7, 47, 15, 55, 23, 63, 31,
            38, 6, 46, 14, 54, 22, 62, 30,
            37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28,
            35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26,
            33, 1, 41, 9, 49, 17, 57, 25
    };
    // 扩展置换表 E
    public static final int[] E = {
            32, 1, 2, 3, 4, 5,
            4, 5, 6, 7, 8, 9,
            8, 9, 10, 11, 12, 13,
            12, 13, 14, 15, 16, 17,
            16, 17, 18, 19, 20, 21,
            20, 21, 22, 23, 24, 25,
            24, 25, 26, 27, 28, 29,
            28, 29, 30, 31, 32, 1
    };
    // P 盒置换表
    public static final int[] P = {
            16, 7, 20, 21,
            29, 12, 28, 17,
            1, 15, 23, 26,
            5, 18, 31, 10,
            2, 8, 24, 14,
            32, 27, 3, 9,
            19, 13, 30, 6,
            22, 11, 4, 25
    };
    // 8 个 S 盒，每个 4x16
    public static final int[][][] S_BOX = {
            {   // S1
                    {14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7},
                    {0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8},
                    {4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0},
                    {15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}
            },
            {   // S2
                    {15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10},
                    {3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5},
                    {0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15},
                    {13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9}
            },
            {   // S3
                    {10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8},
                    {13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1},
                    {13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7},
                    {1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12}
            },
            {   // S4
                    {7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15},
                    {13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9},
                    {10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4},
                    {3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14}
            },
            {   // S5
                    {2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9},
                    {14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6},
                    {4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14},
                    {11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}
            },
            {   // S6
                    {12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11},
                    {10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8},
                    {9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6},
                    {4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13}
            },
            {   // S7
                    {4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1},
                    {13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6},
                    {1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2},
                    {6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12}
            },
            {   // S8
                    {13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7},
                    {1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2},
                    {7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8},
                    {2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}
            }
    };
    // 压缩置换表 PC1，64 -> 56
    static final int[] PC1 = {
            57, 49, 41, 33, 25, 17, 9,
            1, 58, 50, 42, 34, 26, 18,
            10, 2, 59, 51, 43, 35, 27,
            19, 11, 3, 60, 52, 44, 36,
            63, 55, 47, 39, 31, 23, 15,
            7, 62, 54, 46, 38, 30, 22,
            14, 6, 61, 53, 45, 37, 29,
            21, 13, 5, 28, 20, 12, 4
    };

    // 压缩置换表 PC2，56 -> 48
    static final int[] PC2 = {
            14, 17, 11, 24, 1, 5,
            3, 28, 15, 6, 21, 10,
            23, 19, 12, 4, 26, 8,
            16, 7, 27, 20, 13, 2,
            41, 52, 31, 37, 47, 55,
            30, 40, 51, 45, 33, 48,
            44, 49, 39, 56, 34, 53,
            46, 42, 50, 36, 29, 32
    };
    // 存储明文分组的 l 和 r 两部分
    private static int l, r;
    // 16 轮的子密钥
    private static final long[] subKeys = new long[16];

    // 通用的置换函数，P 为置换表，data 为需要置换的数据，oldLen 为置换前比特长度，newLen 为置换后比特长度
    private static long permutation(int[] P, long data, int oldLen, int newLen) {
        long result = 0L;   // 暂存置换的结果
        for (int j = 0; j < newLen; j++) {
            long bit = (data >>> (oldLen - P[j])) & 0x0000_0000_0000_0001L;
            result = (result << 1) | bit;
        }
        return result;    // 返回置换后的数据
    }

    // 子密钥生成
    private static void genSubKey(long key) {
        // 循环左移位数
        int[] shift = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};
        // 密钥压缩置换 PC1
        long key_pc1 = permutation(PC1, key, 64, 56);
        // 将 56 位的压缩置换 PC1 结果分为两部分，分别循环左移，然后再拼接，最后执行压缩置换 PC2 得到子密钥
        for (int i = 0; i < 16; i++) {
            int c = ((int) (key_pc1 >>> 28)) & 0x0FFF_FFFF;      // 左半部分用 int 存储
            int d = (int) (key_pc1 & 0x0000_0000_0FFF_FFFFL);    // 右半部分用 int 存储
            c = ((c >>> (28 - shift[i])) | (c << shift[i])) & 0x0FFFFFFF;   // 左半部分循环左移
            d = ((d >>> (28 - shift[i])) | (d << shift[i])) & 0x0FFFFFFF;   // 右半部分循环左移
            key_pc1 = ((long) c) << 28 | (long) d;               // 拼接循环左移后的两部分
            // PC2 压缩置换
            subKeys[i] = permutation(PC2, key_pc1, 56, 48);
        }
    }

    // 轮函数，round 为当前轮数
    public static void roundFunction(int round) {
        // 对 32 位的 r 进行扩展置换 E，32 -> 48
        long msg_r_ex = permutation(E, r, 32, 48);
        msg_r_ex ^= subKeys[round];  // 扩展置换结果与子密钥异或
        int msg_r_s = 0;             // 用于存储 S 盒替代的结果
        int[] s_arr = new int[8];    // 用于暂存查表得到的 S 盒替代的各值
        // 将 48 位的扩展置换结果分为 8 组（每组 6 位），在 8 个 S 盒中查找得到 8 个替代值存入 s_arr
        for (int i = 0; i < 8; i++) {
            int row = (int) (((msg_r_ex >>> 5) & 0x0000_0000_0000_0001L) * 2 + (msg_r_ex & 0x0000_0000_0000_0001L));
            int col = (int) ((msg_r_ex >>> 1) & 0x0000_0000_0000_000FL);
            msg_r_ex >>>= 6;
            s_arr[7 - i] = S_BOX[7 - i][row][col];
        }
        // 按照查找得到的结果执行 S 盒替代
        for (int i = 0; i < 8; i++) {
            msg_r_s = (msg_r_s << 4) | s_arr[i];
        }
        // P盒置换
        int msg_r_p = (int) permutation(P, msg_r_s, 32, 32);
        // l 设为本轮初始的 r，r 设为上述变换结果与本轮初始 l 的异或
        int r_temp = r;
        r = msg_r_p ^ l;
        l = r_temp;
    }

    // 单个分组的加密、解密，i 为组号，data 为加密或解密源数据，result 为加解密结果，isEncrypt 表示加密还是解密（区别在于轮密钥的使用）
    private static void singleGroup_En_De(int i, byte[] data, byte[] result, boolean isEncrypt) {
        long msg = Convert.bytesToLong(data, i);    // 当前分组的 8 字节转换为 long
        // 初始置换 IP
        long msg_ip = permutation(IP, msg, 64, 64);
        // 分为 l 和 r
        l = (int) ((msg_ip >>> 32) & 0x0000_0000_FFFF_FFFFL);
        r = (int) (msg_ip & 0x0000_0000_FFFF_FFFFL);
        // 16轮加密或解密
        if (isEncrypt) {
            for (int j = 0; j < 16; j++) {
                roundFunction(j);
            }
        } else {
            for (int j = 0; j < 16; j++) {
                roundFunction(15 - j);
            }
        }
        // l 与 r 互换
        int temp = r;
        r = l;
        l = temp;
        // 组合互换后的 l 和 r
        long msg_swap = ((long) l & 0xFFFFFFFFL) << 32 | (long) r & 0xFFFFFFFFL;
        // 逆初始置换 IP^-1
        long c = permutation(IP_INV, msg_swap, 64, 64);
        Convert.longToBytes(c, result, i);    // 64位密文转字节数组
    }


    // 分组加密、解密，模式为ECB，N 为组数，data 为源字节信息，result 为结果字节信息，key 为密钥， isEncrypt 表明是解密还是加密
    private static void ECB(int N, byte[] data, byte[] result, long key, boolean isEncrypt) {
        genSubKey(key);  // 生成子密钥
        // 执行分组加密或解密
        for (int i = 0; i < N; i++) {
            singleGroup_En_De(i, data, result, isEncrypt);  // 单组加密、解密
        }
    }


    // 分组加密，模式为CBC。IV 为初始向量
    private static void CBC_En(int N, byte[] data, byte[] result, byte[] IV, long key) {
        genSubKey(key);  // 生成子密钥
        // 执行分组加密或解密
        for (int i = 0; i < N; i++) {
            if (i > 0) {
                for (int j = 0; j < 8; j++) {
                    data[i * 8 + j] ^= result[(i - 1) * 8 + j];      // 明文与上一组密文异或
                }
            } else {
                for (int j = 0; j < 8; j++) {
                    data[j] ^= IV[j];           // 明文与初始向量异或
                }
            }
            singleGroup_En_De(i, data, result, true);  // 单组加密
        }
    }

    // 分组解密，模式为CBC。IV 为初始向量
    private static void CBC_De(int N, byte[] data, byte[] result, byte[] IV, long key) {
        genSubKey(key);  // 生成子密钥
        // 执行分组加密或解密
        for (int i = 0; i < N; i++) {
            singleGroup_En_De(i, data, result, false);  // 单组解密
            if (i > 0) {
                for (int j = 0; j < 8; j++) {
                    result[i * 8 + j] ^= data[(i - 1) * 8 + j];  // 明文与上一组密文异或
                }
            } else {
                for (int j = 0; j < 8; j++) {
                    result[j] ^= IV[j];      // 明文与初始向量异或
                }
            }
        }
    }

    // DES 加密算法，IV 为 null 则调用 ECB
    public static byte[] encrypt(byte[] bytes, byte[] byteKey, byte[] IV) {
        long key = Convert.bytesToLong(byteKey, 0);   // 密钥从字节数组转为 long，方便后续的移位操作
        int N = bytes.length / 8 + 1;      // 需要加密的分组数
        int rest = bytes.length % 8;       // 最后一组剩余字节数
        byte[] bytes_pad = new byte[bytes.length + 8 - rest]; // padding 后的消息字节数组
        byte[] bytes_cipher = new byte[bytes_pad.length];     // 密文的字节数组
        // 拷贝原始消息字节
        System.arraycopy(bytes, 0, bytes_pad, 0, bytes.length);
        // 执行 padding 填充（PKCS#5/7）
        for (int i = (N - 1) * 8 + rest; i < N * 8; i++) {
            bytes_pad[i] = (byte) (8 - rest);
        }
        // 分组加密
        if (IV == null) {
            ECB(N, bytes_pad, bytes_cipher, key, true);
        } else {
            CBC_En(N, bytes_pad, bytes_cipher, IV, key);
        }
        return bytes_cipher;
    }

    // DES 解密算法，IV 为 null 则调用 ECB
    public static byte[] decrypt(byte[] bytes_cipher, byte[] byteKey, byte[] IV) {
        long key = Convert.bytesToLong(byteKey, 0);   // 密钥从字节数组转为 long，方便后续的移位操作
        int N = bytes_cipher.length / 8;                 // 需要解密的分组数
        byte[] bytes_message = new byte[bytes_cipher.length];   // 用于存储解密信息的字节
        // 分组解密
        if (IV == null) {
            ECB(N, bytes_cipher, bytes_message, key, false);
        } else {
            CBC_De(N, bytes_cipher, bytes_message, IV, key);
        }
        // 获取真实长度
        int real_len = bytes_message.length - bytes_message[bytes_message.length - 1];
        // 去除填充部分
        byte[] bytes_msg_noPad = new byte[real_len];
        System.arraycopy(bytes_message, 0, bytes_msg_noPad, 0, real_len);
        return bytes_msg_noPad;
    }
}