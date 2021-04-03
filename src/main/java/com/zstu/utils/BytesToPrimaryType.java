package com.zstu.utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * 该类用来接收一个byte-array，并将其转为基本数据类型
 *
 * @auther Stiles-JKY
 * @date 2020/5/4-5:25
 */
public abstract class BytesToPrimaryType {

    public static int toInt(byte[] bytes) {
        return toInt(bytes, "big");
    }

    /**
     * 将byte-array转为int类型，如果byte[]数组的长度超过4，则抛出一个异常，
     * 如果bytes.length <= 4,则按照4个byte来计算
     *
     * @return
     */
    public static int toInt(byte[] bytes, String endian) {
        //如果一个byte-array 的长度 大于4 则抛出异常
        if (bytes.length > 4) {
            throw new UnsupportedOperationException("byte-array is to long");
        }
        byte[] adjustBytes = {0, 0, 0, 0};
        if ("little".equals(endian)) {
            for (int i = 0; i < bytes.length; i++) {
                adjustBytes[i] = bytes[i];
            }
        } else {
            int index = 3;
            for (int i = bytes.length - 1; i >= 0; i--) {
                adjustBytes[index--] = bytes[i];
            }
        }


        int value = 0;
        //按照大小端模式进行解析
        if ("big".equals(endian)) {
            value = (int) ((adjustBytes[0] & 0xff) << 24
                    | (adjustBytes[1] & 0xff) << 16
                    | (adjustBytes[2] & 0xff) << 8
                    | (adjustBytes[3] & 0xff));
        } else if ("little".equals(endian)) {
            value = (int) ((adjustBytes[3] & 0xff) << 24
                    | (adjustBytes[2] & 0xff) << 16
                    | (adjustBytes[1] & 0xff) << 8
                    | (adjustBytes[0] & 0xff));
        }

        return value;
    }

    public static long toLong(byte[] bytes) {
        return toLong(bytes, "big");
    }

    public static long toLong(byte[] bytes, String endian) {
        if (bytes.length > 8) {
            throw new UnsupportedOperationException("byte-array is to long");
        }
        byte[] adjustBytes = {0, 0, 0, 0, 0, 0, 0, 0};
        if ("little".equals(endian)) {
            for (int i = 0; i < bytes.length; i++) {
                adjustBytes[i] = bytes[i];
            }
        } else {
            int index = 7;
            for (int i = bytes.length - 1; i >= 0; i--) {
                adjustBytes[index--] = bytes[i];
            }
        }


        long value = 0;
        if ("little".equals(endian)) {
            value = adjustBytes[0];
            value &= 0xff;
            value |= ((long) adjustBytes[1] << 8);
            value &= 0xffff;
            value |= ((long) adjustBytes[2] << 16);
            value &= 0xffffff;
            value |= ((long) adjustBytes[3] << 24);
            value &= 0xffffffffl;
            value |= ((long) adjustBytes[4] << 32);
            value &= 0xffffffffffl;
            value |= ((long) adjustBytes[5] << 40);
            value &= 0xffffffffffffl;
            value |= ((long) adjustBytes[6] << 48);
            value &= 0xffffffffffffffl;
            value |= ((long) adjustBytes[7] << 56);

        } else if ("big".equals(endian)) {
            value = adjustBytes[7];
            value &= 0xff;
            value |= ((long) adjustBytes[6] << 8);
            value &= 0xffff;
            value |= ((long) adjustBytes[5] << 16);
            value &= 0xffffff;
            value |= ((long) adjustBytes[4] << 24);
            value &= 0xffffffffl;
            value |= ((long) adjustBytes[3] << 32);
            value &= 0xffffffffffl;
            value |= ((long) adjustBytes[2] << 40);
            value &= 0xffffffffffffl;
            value |= ((long) adjustBytes[1] << 48);
            value &= 0xffffffffffffffl;
            value |= ((long) adjustBytes[0] << 56);
        }
        return value;
    }

    public static double toDouble(byte[] bytes) {

        return toDouble(bytes, "big");
    }

    /**
     * 将byte-array转为double类型
     * 不足8个按8个计算
     *
     * @return
     */
    public static double toDouble(byte[] bytes, String endian) {

        long value = 0;
        value = toLong(bytes, endian);
        return Double.longBitsToDouble(value);
    }

    public static String toStr(byte[] bytes) {
        return toStr(bytes, "UTF-8");
    }

    /**
     * 将byte-array转为String,默认采用UTF-8编码
     *
     * @return
     */
    public static String toStr(byte[] bytes, String encode) {

        String strVar = null;
        try {
            strVar = new String(bytes, encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return strVar;
    }

    /**
     * 将byte-array转为boolean
     *
     * @return
     */
    public static boolean toBoolean(byte b) {
        return b != 0;
    }

    public static short toShort(byte[] bytes, String endian) {
        short s = 0;
        if ("big".equals(endian)) {
            for (int i = 0; i < 2; i++) {
                s <<= 8;
                s |= (bytes[i] & 0xff);
            }
        } else if ("little".equals(endian)) {
            for (int i = 1; i >= 0; i--) {
                s <<= 8;
                s |= (bytes[i] & 0xff);
            }
        }
        return s;
    }

    public static char toChar(byte b, String charSet) {
        Charset cs = Charset.forName(charSet);
        ByteBuffer bb = ByteBuffer.allocate(1);
        bb.put(b);
        bb.flip();
        CharBuffer cb = cs.decode(bb);
        return cb.get();
    }

    /**
     * 将byte-array转为boolean
     *
     * @param bytes
     * @return
     */
    public static char toChar(byte[] bytes) {
        if (bytes.length < 2) {
            bytes = new byte[]{bytes[0], 0};
        }
        return (char) (((bytes[1] & 0xff) << 0) + ((bytes[0]) << 8));
    }

    private static byte[] getByteArrayByTypeSize(byte[] bytes, int typeSize, int offset) {
        byte[] choose = new byte[typeSize];
        for (int i = 0; i < typeSize; i++) {
            choose[i] = bytes[offset + i];
        }

        return choose;
    }


    /**
     * 传入一个byte-array得到一个int-array
     *
     * @param bytes    原始数组
     * @param endian   大小端模式
     * @param size     数组长度
     * @param typeSize 元素所占byte个数
     * @param offset   原始数组偏移量
     * @return
     */
    public static int[] toIntArray(byte[] bytes, String endian, int size, int typeSize, int offset) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            byte[] b = getByteArrayByTypeSize(bytes, typeSize, offset);
            offset += typeSize;
            array[i] = toInt(b, endian);
        }
        return array;
    }

    public static int[] toIntArray(byte[] bytes, int size, int offset) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            byte[] b = getByteArrayByTypeSize(bytes, 4, offset);
            offset += 4;
            array[i] = toInt(b);
        }

        return array;
    }


    /**
     * 传入一个byte-array得到一个double-array
     *
     * @param bytes    原始数组
     * @param endian   大小端模式
     * @param size     数组长度
     * @param typeSize 元素所占byte个数
     * @param offset   原始数组偏移量
     * @return
     */
    public static double[] toDoubleArray(byte[] bytes, String endian, int size, int typeSize, int offset) {
        double[] array = new double[size];
        for (int i = 0; i < size; i++) {
            byte[] b = getByteArrayByTypeSize(bytes, typeSize, offset);
            offset += typeSize;
            array[i] = toDouble(b, endian);
        }
        return array;
    }

    public static double[] toDoubleArray(byte[] bytes, int size, int offset) {
        double[] array = new double[size];
        for (int i = 0; i < size; i++) {
            byte[] b = getByteArrayByTypeSize(bytes, 8, offset);
            offset += 8;
            array[i] = toDouble(b);
        }
        return array;
    }

    /**
     * 传入一个byte-array得到一个char-array
     *
     * @param bytes    原始数组
     * @param size     数组长度
     * @param typeSize 元素所占byte个数
     * @param offset   原始数组偏移量
     * @return
     */
    public static char[] toCharArray(byte[] bytes, int size, int typeSize, int offset) {
        char[] array = new char[size];
        for (int i = 0; i < size; i++) {
            byte[] b = getByteArrayByTypeSize(bytes, typeSize, offset);
            offset += typeSize;
            array[i] = toChar(b);
        }
        return array;
    }

    public static byte getTypeDefaultSize(String type) {
        byte size = -1;
        switch (type) {
            case "int":
                size = 4;
                break;
            case "double":
                size = 8;
                break;
            case "short":
                size = 2;
                break;
            case "boolean":
                size = 1;
                break;
            case "byte":
                size = 1;
                break;
            case "long":
                size = 8;
                break;
            case "float":
                size = 4;
                break;
            case "char":
                size = 2;
                break;
            default:
                break;
        }
        return size;
    }
}
