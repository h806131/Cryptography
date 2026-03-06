package util;

public class Convert {
    // 将int类型（32位）拆成byte数组
    public static void intToBytes(int data, byte[] bytes, int n, boolean isBigEndian) {
        for (int i = 0; i < 4; i++) {
            if (isBigEndian)
                bytes[n * 4 + 3 - i] = (byte) (data & 0xFF);
            else bytes[n * 4 + i] = (byte) (data & 0xFF);
            data >>>= 8;
        }
    }

    // 4 字节转 int 类型
    public static int bytesToInt(byte[] bytes, int n, boolean isBigEndian) {
        int result = 0;  // 暂存转换结果
        for (int i = 0; i < 4; i++) {
            if (isBigEndian)
                result = (result << 8) | (((int) (bytes[n * 4 + i])) & 0xFF);
            else
                result = (result << 8) | (((int) (bytes[n * 4 + 3 - i])) & 0xFF);
        }
        return result;
    }

    // 8 字节转为 long（64位）类型，n 表示字节数组中的第几组，n=1 即下标 8~15 这 8 个字节
    public static long bytesToLong(byte[] bytes, int n) {
        long result = 0;  // 暂存转换结果
        for (int i = 0; i < 8; i++) {
            result = (result << 8) | (((long) (bytes[n * 8 + i])) & 0x0000_0000_0000_00FFL);
        }
        return result;
    }

    // long（64位）类型转字节数组，存入字节数组的第 n 组，n=1 即下标 8~15 这 8 个字节
    public static void longToBytes(long data, byte[] bytes, int n) {
        for (int i = 0; i < 8; i++) {
            bytes[n * 8 + 7 - i] = (byte) (data & 0xFF);
            data >>>= 8;
        }
    }

    // byte 转二进制数组
    public static int[] ByteToBinaryArray(byte b) {
        int[] result = new int[8];
        for (int i = 0; i < 8; i++) {
            result[i] = (b >> i) & 1;
        }
        return result;
    }

    // 将字节数组转换为十六进制字符串
    public static String bytesToHexStr(byte[] bytes) {
        StringBuilder str = new StringBuilder();
        for (int t : bytes) {
            str.append(String.format("%02X", t & 0xFF));
        }
        return str.toString();
    }

    // 将十六进制字符串转换为字节数组
    public static byte[] hexStrToBytes(String hex) throws IllegalArgumentException {
        if (hex == null) return null;

        // 转换为大写
        hex = hex.toUpperCase();

        // 检查长度是否为偶数
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException("十六进制字符串长度必须为偶数");
        }

        // 字节数组长度
        int len = hex.length() / 2;
        byte[] result = new byte[len];

        for (int i = 0; i < len; i++) {
            int high = Character.digit(hex.charAt(i * 2), 16);         // 字节高4位对应的十进制数
            int low = Character.digit(hex.charAt(i * 2 + 1), 16);      // 字节低4位对应的十进制数
            // 判断高四位和低四位是否都能正常转换为十进制（比如 0xAB 中的 A 和 B），是则合法
            if (high == -1 || low == -1) {
                throw new IllegalArgumentException("包含非法十六进制字符");
            }
            result[i] = (byte) ((high << 4) + low);
        }
        return result;
    }
}
