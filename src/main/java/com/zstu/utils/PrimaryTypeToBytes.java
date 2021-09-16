package com.zstu.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * 将基本类型转为byte-array
 *
 * @auther Stiles-JKY
 * @date 2020/5/4-7:05
 */
public abstract class PrimaryTypeToBytes {

    public static byte[] intToBytes(int var, int typesize, String endian) {
        byte[] bytes = null;
        if ("big".equals(endian)) {
            bytes = intToBytesBig(var, typesize);
        } else if ("little".equals(endian)) {
            bytes = intToBytesLittle(var, typesize);
        }
        return bytes;
    }

    // big-endian
    public static byte[] intToBytesBig(int var, int typesize) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (var >> 24);
        bytes[1] = (byte) (var >> 16);
        bytes[2] = (byte) (var >> 8);
        bytes[3] = (byte) var;

        byte[] result = new byte[typesize];
        int index = 0;
        for (int i = 4 - typesize; i < 4; i++) {
            result[index++] = bytes[i];
        }
        return result;
    }

    // little-endian
    public static byte[] intToBytesLittle(int var, int typesize) {
        byte[] bytes = new byte[4];
        bytes[3] = (byte) (var >> 24);
        bytes[2] = (byte) (var >> 16);
        bytes[1] = (byte) (var >> 8);
        bytes[0] = (byte) var;

        byte[] result = new byte[typesize];

        for (int i = 0; i < typesize; i++) {
            result[i] = bytes[i];
        }
        return result;
    }



    public static byte[] doubleToBytes(double var, int typesize, String endian) {
        byte[] bytes = null;
        if ("big".equals(endian)) {
            bytes = doubleToBytesBig(var, typesize);
        } else if ("little".equals(endian)) {
            bytes = doubleToBytesLittle(var, typesize);
        }
        return bytes;
    }

    public static byte[] doubleToBytesLittle(double var, int typesize) {
        long value = Double.doubleToRawLongBits(var);
        byte[] byteRet = new byte[8];
        for (int i = 0; i < 8; i++) {
            byteRet[i] = (byte) ((value >> 8 * i) & 0xff);
        }
        int index = 0;
        byte[] result = new byte[typesize];
        for (int i = 8 - typesize; i < 8; i++) {
            result[index++] = byteRet[i];
        }
        return result;
    }

    public static byte[] doubleToBytesBig(double var, int typesize) {
        long value = Double.doubleToRawLongBits(var);
        byte[] byteRet = new byte[8];
        int count = 7;
        for (int i = 0; i < 8; i++) {
            byteRet[count--] = (byte) ((value >> 8 * i) & 0xff);
        }

        byte[] result = new byte[typesize];
        for (int i = 0; i < typesize; i++) {
            result[i] = byteRet[i];
        }
        return result;
    }

    public static byte[] longToBytesLittle(long value, int typesize) {
        byte[] byteRet = new byte[8];
        //value右边为低位，byteRet[0]位于栈顶为低地址；低位放入低地址，小端方式存储
        for (int i = 0; i < 8; i++) {
            byteRet[i] = (byte) ((value >> 8 * i) & 0xff);
        }

        //小端存储，有效位从byteRet[0]开始
        byte[] result = new byte[typesize];
        for (int i = 0; i < typesize; i++) {
            result[i] = byteRet[i];
        }
        return result;
    }

    public static byte[] longToBytesBig(long value, int typesize) {
        byte[] byteRet = new byte[8];
        int count = 7;
        //value右边为低位，byteRet[7]为高地址；高地址存低位，大端序
        for (int i = 0; i < 8; i++) {
            byteRet[count--] = (byte) ((value >> 8 * i) & 0xff);
        }

        byte[] result = new byte[typesize];
        int index = typesize - 1;
        for(int i = 7; i >= 8 - typesize; i--) {
            result[index--] = byteRet[i];
        }
        return result;
    }

    public static byte[] longToBytes(long value, int typesize, String endian) {
        if ("big".equals(endian)) return longToBytesBig(value, typesize);
        if ("little".equals(endian)) return longToBytesLittle(value, typesize);
        return null;
    }

    public static byte[] charToBytes(char[] chars,String charSet) {
        Charset cs = Charset.forName(charSet);
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);
        return bb.array();
    }

    public static byte[] shortToBytes(short s,String endian) {
        byte[] data = new byte[2];
        if (endian.equals("little")) {
            data[0] = (byte) ((0xff) & s);
            s >>>= 8;
            data[1] = (byte) ((0xff) & s);
        } else if (endian.equals("big")) {
            data[1] = (byte) ((0xff) & s);
            s >>>= 8;
            data[0] = (byte) ((0xff) & s);
        }
        return data;
    }

}
