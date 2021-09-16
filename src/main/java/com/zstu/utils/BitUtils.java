package com.zstu.utils;


/**
 * @author jky
 */
public abstract class BitUtils {

    public long mergeBytes(byte[] bytes) {
        long l = 0;
        for (int i = 0; i < bytes.length; i++) {
            l &= (l & bytes[i]);
            l <<= 8;
        }
        return l;
    }

    public static byte[] takeBytesByBit(byte[] bytes, int bitLength) {
        int len = bitLength % 8 == 0 ? bitLength / 4 : bitLength / 4 + 1;
        byte[] bs = new byte[len];
        for (int i = 0; i < bs.length; i++) {
            bs[i] = takeByteByBit(bytes[i], bitLength, true);
            // 改变原数组
            bytes[i] ^= bs[i];
            bitLength -= 8;
        }
        return bs;
    }

    public static byte takeByteByBit(byte b, int bitLength, boolean front) {
        byte B;
        byte t = b;
        if (front) {
            t = (byte) (t >> (8 - bitLength));
            t = (byte) (t << (8 - bitLength));
        } else {
            t = (byte) (t << (8 - bitLength));
            t = (byte) (t >> (8 - bitLength));
        }
        B = (byte) ((0xff) & t);
        return B;
    }

    public static byte[] divideArray(byte[] src, int offset, int length) {
        if (length == -1) {
            byte[] sub = new byte[src.length - offset];
            System.arraycopy(src, offset, sub, 0, sub.length);
            return sub;
        }
        if (src.length - offset < length)
            return null;
        byte[] sub = new byte[length];
        System.arraycopy(src, offset, sub, 0, length);
        return sub;
    }

    public static byte[] copyByteArray(byte[] src) {
        byte[] dest = new byte[src.length];
        System.arraycopy(src, 0, dest, 0, src.length);
        return dest;
    }

    public static short mergeToShort(byte[] src) {
        short result = 0;
        result &= (result | ((byte) 0xFF & src[0]));
        result <<= 8;
        result &= (result | ((byte) 0xFF & src[1]));
        return result;
    }


    /**
     * 从byte-array中获取指定长度的二进制位，并将其转为long输出
     *
     * @param src      原byte数组
     * @param start    数组开始位置
     * @param typesize start开始的后length个数组，这里要注意，如果一个byte被分为了多个，则length为前第n个bit的下标 + 1
     *                 例如：byte为 00101111;按位分为两部分，001和01111；
     *                 则length取值为4和8
     * @return
     */
    public static long getBitsFromByteArray(byte[] src, Integer start, int preBits, int typesize, String endian) {
        if ("big".equals(endian)) {
            return getBitsFromByteArrayBig(src, start, preBits, typesize);
        } else {
            return getBitsFromByteArrayLittle(src, start, preBits, typesize);
        }
    }


    /**
     * @param src      原数组
     * @param offset   数组索引
     * @param typesize 数组索引到
     * @return
     */
    public static long getBitsFromByteArrayBig(byte[] src, int offset, int preBits, int typesize) {
        byte[] sub;
        if (src.length - offset > 8) {
            sub = ByteArrayUtils.copySubArray(src, offset, 8);//去除前位的干扰
        } else {
            sub = ByteArrayUtils.copySubArray(src, offset, -1);//去除前位的干扰
        }

        long var = 0;
        long big = BytesToPrimaryType.toLong(sub, "big");
        int zeroCount = 0;
        byte[] bytes = PrimaryTypeToBytes.longToBytesBig(big, 8);
        for(int i = 0; i < 8; i++) {
            if (bytes[i] != 0) {
                break;
            }
            zeroCount++;
        }
//        System.out.println(Long.toBinaryString(var));
        var = big;
        var <<= (preBits + 8 * zeroCount);
//        System.out.println(Long.toBinaryString(var));
        var >>>= (64 - typesize);
//        System.out.println(Long.toBinaryString(var));
        return var;
    }


    public static long getBitsFromByteArrayLittle(byte[] src, int offset, int preBits, int typesize) {
        byte[] sub;
        if (src.length - offset > 8) {
            sub = ByteArrayUtils.copySubArray(src, offset, 8);//去除前位的干扰
        } else {
            sub = ByteArrayUtils.copySubArray(src, offset, -1);//去除前位的干扰
        }
        long var = 0;
        long little = BytesToPrimaryType.toLong(sub, "little");
//        System.out.println(Long.toBinaryString(little));
        var = little;
//        System.out.println(Long.toBinaryString(var));
        var >>>= preBits;
        var <<= (64 - typesize);
//        System.out.println(Long.toBinaryString(var));
        var >>>= (64 - typesize);
//        System.out.println(Long.toBinaryString(var));
        return var;
    }

    public static byte[] littleToBig(byte[] src) {
        long little = BytesToPrimaryType.toLong(src, "little");
        byte[] bigBytes = PrimaryTypeToBytes.longToBytesBig(little, src.length);
        return bigBytes;
    }

    /**
     * 取 long 的前size个位或后size个位
     *
     * @param front true为前size个位，false为后size个位
     * @param src   数据源
     * @param size  位数
     * @return
     */
    public static long tackBitsFromLong(boolean front, long src, int size) {
        long var = 0;
        int range = 64 - size;
        if (front) {
            while (range > 0) {
                src >>>= 1;
                range--;
            }
            var = src;
        } else {
            while (range > 0) {
                src <<= 1;
                range--;
            }
            var = src;
            int range2 = 64 - size;
            while (range2 > 0) {
                var >>>= 1;
                range2--;
            }
        }
        return var;
    }
}
