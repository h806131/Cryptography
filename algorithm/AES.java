package algorithm;

import util.Convert;

public class AES {
    // S 盒替代
    private static final int[][] S_BOX = {
            {0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76},
            {0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0},
            {0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15},
            {0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75},
            {0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84},
            {0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf},
            {0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8},
            {0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2},
            {0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73},
            {0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb},
            {0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79},
            {0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08},
            {0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a},
            {0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e},
            {0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf},
            {0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16}
    };

    // 逆 S 盒替代
    private static final int[][] S_BOX_INV = {
            {0x52, 0x09, 0x6a, 0xd5, 0x30, 0x36, 0xa5, 0x38, 0xbf, 0x40, 0xa3, 0x9e, 0x81, 0xf3, 0xd7, 0xfb},
            {0x7c, 0xe3, 0x39, 0x82, 0x9b, 0x2f, 0xff, 0x87, 0x34, 0x8e, 0x43, 0x44, 0xc4, 0xde, 0xe9, 0xcb},
            {0x54, 0x7b, 0x94, 0x32, 0xa6, 0xc2, 0x23, 0x3d, 0xee, 0x4c, 0x95, 0x0b, 0x42, 0xfa, 0xc3, 0x4e},
            {0x08, 0x2e, 0xa1, 0x66, 0x28, 0xd9, 0x24, 0xb2, 0x76, 0x5b, 0xa2, 0x49, 0x6d, 0x8b, 0xd1, 0x25},
            {0x72, 0xf8, 0xf6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xd4, 0xa4, 0x5c, 0xcc, 0x5d, 0x65, 0xb6, 0x92},
            {0x6c, 0x70, 0x48, 0x50, 0xfd, 0xed, 0xb9, 0xda, 0x5e, 0x15, 0x46, 0x57, 0xa7, 0x8d, 0x9d, 0x84},
            {0x90, 0xd8, 0xab, 0x00, 0x8c, 0xbc, 0xd3, 0x0a, 0xf7, 0xe4, 0x58, 0x05, 0xb8, 0xb3, 0x45, 0x06},
            {0xd0, 0x2c, 0x1e, 0x8f, 0xca, 0x3f, 0x0f, 0x02, 0xc1, 0xaf, 0xbd, 0x03, 0x01, 0x13, 0x8a, 0x6b},
            {0x3a, 0x91, 0x11, 0x41, 0x4f, 0x67, 0xdc, 0xea, 0x97, 0xf2, 0xcf, 0xce, 0xf0, 0xb4, 0xe6, 0x73},
            {0x96, 0xac, 0x74, 0x22, 0xe7, 0xad, 0x35, 0x85, 0xe2, 0xf9, 0x37, 0xe8, 0x1c, 0x75, 0xdf, 0x6e},
            {0x47, 0xf1, 0x1a, 0x71, 0x1d, 0x29, 0xc5, 0x89, 0x6f, 0xb7, 0x62, 0x0e, 0xaa, 0x18, 0xbe, 0x1b},
            {0xfc, 0x56, 0x3e, 0x4b, 0xc6, 0xd2, 0x79, 0x20, 0x9a, 0xdb, 0xc0, 0xfe, 0x78, 0xcd, 0x5a, 0xf4},
            {0x1f, 0xdd, 0xa8, 0x33, 0x88, 0x07, 0xc7, 0x31, 0xb1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xec, 0x5f},
            {0x60, 0x51, 0x7f, 0xa9, 0x19, 0xb5, 0x4a, 0x0d, 0x2d, 0xe5, 0x7a, 0x9f, 0x93, 0xc9, 0x9c, 0xef},
            {0xa0, 0xe0, 0x3b, 0x4d, 0xae, 0x2a, 0xf5, 0xb0, 0xc8, 0xeb, 0xbb, 0x3c, 0x83, 0x53, 0x99, 0x61},
            {0x17, 0x2b, 0x04, 0x7e, 0xba, 0x77, 0xd6, 0x26, 0xe1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0c, 0x7d}
    };

    // 行移位
    private static final int[][] SHIFT = {
            {0, 1, 2, 3}, {1, 2, 3, 0}, {2, 3, 0, 1}, {3, 0, 1, 2}
    };

    // 逆行移位
    private static final int[][] SHIFT_INV = {
            {0, 1, 2, 3}, {3, 0, 1, 2}, {2, 3, 0, 1}, {1, 2, 3, 0}
    };

    // 列混淆矩阵
    private static final int[][] MIX_COLUMNS_MATRIX = {
            {0x02, 0x03, 0x01, 0x01},
            {0x01, 0x02, 0x03, 0x01},
            {0x01, 0x01, 0x02, 0x03},
            {0x03, 0x01, 0x01, 0x02}
    };

    // 逆列混淆矩阵
    private static final int[][] MIX_COLUMNS_MATRIX_INV = {
            {0x0e, 0x0b, 0x0d, 0x09},
            {0x09, 0x0e, 0x0b, 0x0d},
            {0x0d, 0x09, 0x0e, 0x0b},
            {0x0b, 0x0d, 0x09, 0x0e}
    };

    // 轮常量
    private static final long[] RCON = {
            0x01000000L, 0x02000000L, 0x04000000L, 0x08000000L, 0x10000000L,
            0x20000000L, 0x40000000L, 0x80000000L, 0x1b000000L, 0x36000000L
    };

    // 轮密钥
    private static final int[] roundKey = new int[44];

    // 轮密钥生成
    private static void genRoundKey(byte[] keyBytes) {
        byte[] w = new byte[4];         // 用于暂存 4 个字节（int）
        // 将初始密钥（128位）转换为 4 个 int 放入 roundKey[0~3]
        for (int i = 0; i < 16; i += 4) {
            System.arraycopy(keyBytes, i, w, 0, 4);
            roundKey[i / 4] = Convert.bytesToInt(w, 0, true);
        }
        for (int i = 4; i < 44; i++) {
            // 生成 roundKey[i]，i 是 4 的倍数时使用复杂的生成函数 g
            if (i % 4 == 0) {
                Convert.intToBytes(roundKey[i - 1], w, 0, true);    // int 转换存入 w
                byte[] w_new = new byte[4];       // 存储处理结果，包括移位和替代
                int[] idx = {1, 2, 3, 0};         // 移位规则（循环左移一位）
                for (int j = 0; j < 4; j++) {     // 执行移位
                    w_new[j] = w[idx[j]];
                }
                // S 盒替代
                for (int j = 0; j < 4; j++) {
                    int row = (w_new[j] >>> 4) & 0x0F;
                    int col = w_new[j] & 0x0F;
                    w_new[j] = (byte) (S_BOX[row][col] & 0xFF);
                }
                // 将处理结果转为 int，以便后续的异或操作
                int temp = Convert.bytesToInt(w_new, 0, true);
                temp ^= (int) RCON[i / 4 - 1];
                roundKey[i] = temp ^ roundKey[i - 4];
            } else {  // 生成 roundKey[i]，i 不是 4 的倍数
                roundKey[i] = roundKey[i - 1] ^ roundKey[i - 4];
            }
        }
    }

    // GF(2^8) 乘法
    private static byte galoisMul(byte a, byte b) {
        // byte 转二进制数组（aa、bb的每个元素都是 0 或 1）
        int[] aa = Convert.ByteToBinaryArray(a);
        int[] bb = Convert.ByteToBinaryArray(b);
        int[] rr = new int[aa.length + bb.length - 1];     // 多项式乘法系数（模 2 乘法结果）
        int r = 0;        // 用于存储 GF(2^8) 乘法结果

        for (int i = 0; i < aa.length; i++) {
            for (int j = 0; j < bb.length; j++) {
                rr[i + j] ^= (aa[i] * bb[j]);      // 模 2 乘法，下标表示多项式某项的次数
            }
        }

        // 既约多项式：1 + x + x^3 + x^4 + x^8
        int[] mod = {1, 1, 0, 1, 1, 0, 0, 0, 1}; // 低位在前，高位在后

        // rr 对既约多项式 mod 取模
        for (int i = rr.length - 1; i >= 8; i--) {
            if (rr[i] == 1) {
                for (int j = 0; j < mod.length; j++) {
                    rr[i - 8 + j] ^= mod[j];
                }
            }
        }

        // 将模既约多项式后的结果转为数值
        for (int i = 0; i < 8; i++) {
            r |= (rr[i] << i);
        }
        return (byte) (r & 0xFF);
    }

    // 对整个状态矩阵进行列混淆或逆列混淆
    public static void mixColumns(byte[][] state, int[][] matrix) {
        for (int c = 0; c < 4; c++) {
            byte[] newCol = new byte[4];    // 存储计算得到的新的一列
            for (int i = 0; i < 4; i++) {
                byte val = 0;
                for (int j = 0; j < 4; j++) {
                    val ^= galoisMul((byte) matrix[i][j], state[j][c]);
                }
                newCol[i] = val;
            }
            // 用新的一列更新状态矩阵
            for (int r = 0; r < 4; r++) {
                state[r][c] = newCol[r];
            }
        }
    }

    // 轮密钥加，n 为组号
    private static void addRoundKey(byte[][] state, int n) {
        byte[] roundKeyByte = new byte[16];
        // 将 w[0]~w[3] 4 个 32 位的轮密钥转换为长度为 16 的字节数组
        for (int j = 0; j < 4; j++) {
            Convert.intToBytes(roundKey[n * 4 + j], roundKeyByte, j, true);
        }
        int k = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                state[j][i] ^= roundKeyByte[k++];      // 异或
            }
        }
    }

    // 字节替代
    private static void subBytes(byte[][] state, int[][] S) {
        for (int m = 0; m < 4; m++) {
            for (int n = 0; n < 4; n++) {
                int row = (state[m][n] >>> 4) & 0x0F;       // 行号（左 4 位）
                int col = state[m][n] & 0x0F;               // 列号（右 4 位）
                state[m][n] = (byte) (S[row][col] & 0xFF);
            }
        }
    }

    // 行移位
    private static void rowShift(byte[][] state, int[][] SHIFT) {
        byte[][] state_shift = new byte[4][4];       // 用于暂存移位后的状态矩阵
        for (int m = 0; m < 4; m++) {
            for (int n = 0; n < 4; n++) {
                state_shift[m][n] = state[m][SHIFT[m][n]];
            }
        }
        // 更新 state
        for (int m = 0; m < 4; m++) {
            for (int n = 0; n < 4; n++) {
                state[n][m] = state_shift[n][m];
            }
        }
    }

    // 16 字节数组转为 4x4 状态矩阵
    private static void dataToState(byte[] data, byte[][] state) {
        int k = 0;
        for (int m = 0; m < 4; m++) {
            for (int n = 0; n < 4; n++) {
                state[n][m] = data[k++];
            }
        }
    }

    // 4x4 状态矩阵转 16 字节数组（放入data 的下标 16*n ~ 16*n+15）
    private static void stateToData(byte[] data, byte[][] state, int n) {
        int k = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                data[n * 16 + k] = state[j][i];
                k++;
            }
        }
    }

    // 单个分组加密和解密，i 为组号，data 为分组的源数据，result 为加密或者解密的结果，isEncrypt 指明是加密还是解密
    private static void singleGroup_En_De(int i, byte[][] data, byte[] result, boolean isEncrypt) {
        byte[] msg_bytes = data[i];        // 第 i 组的数据（16字节）
        byte[][] state = new byte[4][4];   // 用于存储当前状态矩阵
        dataToState(msg_bytes, state);     // 当前分组转为状态矩阵
        // 第 1 轮之前的轮密钥加，加密与解密使用的轮密钥不同
        if (isEncrypt) addRoundKey(state, 0);
        else addRoundKey(state, 10);

        // 10 轮运算
        for (int j = 0; j < 10; j++) {
            // 加密
            if (isEncrypt) {
                subBytes(state, S_BOX);        // 字节替代
                rowShift(state, SHIFT);        // 行移位
                if (j != 9) {
                    mixColumns(state, MIX_COLUMNS_MATRIX);        // 列混淆
                }
                addRoundKey(state, j + 1);  // 轮密钥加
            } else {  // 解密
                rowShift(state, SHIFT_INV);    // 逆行移位
                subBytes(state, S_BOX_INV);    // 逆字节替代
                addRoundKey(state, 9 - j);  // 轮密钥加
                if (j != 9) {
                    mixColumns(state, MIX_COLUMNS_MATRIX_INV);    // 逆列混淆
                }
            }
        }
        stateToData(result, state, i);    // 状态矩阵转字节数组，按分组顺序依次存入 result
    }

    // 加密和解密操作，ECB 模式（每组单独加解密）
    private static void ECB(int N, byte[][] data, byte[] result, byte[] key, boolean isEncrypt) {
        genRoundKey(key);                  // 生成轮密钥
        // N 组分组加密
        for (int i = 0; i < N; i++) {
            singleGroup_En_De(i, data, result, isEncrypt);
        }
    }

    // 加密操作，CBC 工作模式（每组加密前进行异或）
    private static void CBC_En(int N, byte[][] data, byte[] result, byte[] key, byte[] IV) {
        genRoundKey(key);                  // 生成轮密钥
        // N 组分组加密
        for (int i = 0; i < N; i++) {
            if (i > 0) {
                for (int j = 0; j < 16; j++) {
                    data[i][j] ^= result[(i - 1) * 16 + j];     // 明文与上一组密文异或
                }
            } else {
                for (int j = 0; j < 16; j++) {
                    data[0][j] ^= IV[j];    // 明文与初始向量异或
                }
            }
            singleGroup_En_De(i, data, result, true);
        }
    }

    // 解密操作，CBC 工作模式（每组解密后进行异或）
    private static void CBC_De(int N, byte[][] data, byte[] result, byte[] key, byte[] IV) {
        genRoundKey(key);                  // 生成轮密钥
        // N 组分组加密
        for (int i = 0; i < N; i++) {
            singleGroup_En_De(i, data, result, false);
            if (i > 0) {
                for (int j = 0; j < 16; j++) {
                    result[i * 16 + j] ^= data[i - 1][j];     // 明文与上一组密文异或
                }
            } else {
                for (int j = 0; j < 16; j++) {
                    result[j] ^= IV[j];         // 明文与初始向量异或
                }
            }
        }
    }

    // AES 加密算法，IV 为 null 调用ECB
    public static byte[] encrypt(byte[] bytes, byte[] key, byte[] IV) {
        int N = bytes.length / 16 + 1;      // 需要加密的分组数，分组大小 128bit
        int rest = bytes.length % 16;       // 最后一组剩余字节数
        // padding 后的消息字节数组，共 N 组 16 字节
        byte[][] bytes_pad = new byte[N][16];
        // 拷贝原始消息字节
        for (int i = 0; i < N - 1; i++) {
            System.arraycopy(bytes, i * 16, bytes_pad[i], 0, 16);
        }
        System.arraycopy(bytes, (N - 1) * 16, bytes_pad[N - 1], 0, rest);
        // 执行 padding 填充（PKCS#5/7）
        for (int i = rest; i < 16; i++) {
            bytes_pad[N - 1][i] = (byte) (16 - rest);
        }
        // 用于存储密文字节
        byte[] bytes_cipher = new byte[N * 16];
        // 加密
        if (IV == null) {
            ECB(N, bytes_pad, bytes_cipher, key, true);
        } else {
            CBC_En(N, bytes_pad, bytes_cipher, key, IV);
        }
        return bytes_cipher;
    }

    // AES 解密算法，IV 为 null 调用ECB
    public static byte[] decrypt(byte[] bytes, byte[] key, byte[] IV) {
        int N = bytes.length / 16;                 // 需要解密的分组数，分组大小 128bit
        byte[][] bytes_cipher = new byte[N][16];   // 分组存储密文
        for (int i = 0; i < N; i++) {
            System.arraycopy(bytes, i * 16, bytes_cipher[i], 0, 16);
        }
        // 用于存储明文字节
        byte[] bytes_message = new byte[N * 16];
        // 解密
        if (IV == null) {
            ECB(N, bytes_cipher, bytes_message, key, false);
        } else {
            CBC_De(N, bytes_cipher, bytes_message, key, IV);
        }
        // 获取真实长度
        int real_len = bytes_message.length - bytes_message[bytes_message.length - 1];
        // 去除填充部分
        byte[] bytes_msg_noPad = new byte[real_len];
        System.arraycopy(bytes_message, 0, bytes_msg_noPad, 0, real_len);
        return bytes_msg_noPad;
    }
}