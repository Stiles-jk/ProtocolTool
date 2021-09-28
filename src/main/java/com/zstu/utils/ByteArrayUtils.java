package com.zstu.utils;

import com.sun.org.apache.bcel.internal.generic.LNEG;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @auther Stiles-JKY
 * @date 2021/2/15-22:26
 */
public abstract class ByteArrayUtils {


    public static byte[] copySubArray(byte[] src, int start, int length) {
        byte[] subBytes = null;
        if (length == -1) {
            subBytes = new byte[src.length - start];
        } else {
            subBytes = new byte[length];
        }
        System.arraycopy(src, start, subBytes, 0, subBytes.length);
        return subBytes;
    }

    /**
     * 合并多个byte-array
     *
     * @param bytes
     * @return
     */
    public static byte[] mergeBytes(byte[]... bytes) {
        byte[] mergedBytes = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            for (byte[] byteArray : bytes) {
                baos.write(byteArray);
            }
            mergedBytes = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mergedBytes;
    }

    public static byte[] getBytesFromList(List<byte[]> bytes) {
        byte[] mergedBytes = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            for (byte[] byteArray : bytes) {
                baos.write(byteArray);
            }
            mergedBytes = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mergedBytes;
    }

    public static String printAsHex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            stringBuilder.append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    public static byte[] hexToByteArray(String[] hexs) {
        byte[] bytes = new byte[hexs.length];
        for (int i = 0; i < bytes.length; i++) {
            String hex = hexs[i];
            if (hex.startsWith("0x") || hex.startsWith("0X")) hex = hex.substring(2);
            try {
                bytes[i] = Byte.parseByte(hex, 16);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return bytes;
    }

    public static byte[][] divideTwo(byte[] src, int start, int length) {
        byte[][] divided = new byte[2][];
        divided[0] = new byte[length];
        System.arraycopy(src, start, divided[0], 0, length);
        byte[] dSrc = new byte[src.length - length];
        System.arraycopy(src, length, dSrc, 0, dSrc.length);
        divided[1] = dSrc;
        return divided;
    }

    public static List<byte[]> divideAsLength(byte[] src, int size, byte stuff) {
        List<byte[]> divides = new ArrayList<>();
        if (src.length % size != 0) {
            byte[] stuffs = new byte[size - (src.length % size)];
            src = mergeBytes(src,stuffs);
        }
        int offset = 0;
        while (offset < src.length) {
            byte[] divide = copySubArray(src,offset,size);
            divides.add(divide);
            offset += size;
        }
        return divides;
    }

    /**
     * 将两个数组合并
     *
     * @param src
     * @param dest
     * @param <T>
     * @return
     */
    public static <T> T[] concatTwo(T[] src, T[] dest) {
        int totalLength = src.length + dest.length;

        T[] result = Arrays.copyOf(dest, totalLength);
        System.arraycopy(src, 0, result, dest.length, src.length);
        return result;
    }

    /**
     * 专门为dest的长度为最终长度设计
     *
     * @param src
     * @param dest
     * @return
     */
    public static byte[] concatTwo(byte[] src, byte[] dest) {
        int totalLength = src.length + dest.length;

        byte[] result = Arrays.copyOf(dest, totalLength);
        System.arraycopy(src, 0, result, dest.length, src.length);
        return result;
    }

    public static byte[] concatAll(byte[]... bs) {
        byte[] result = new byte[0];
        for (byte[] b : bs) {
            result = concatTwo(b, result);
        }
        return result;
    }

    /**
     * 将多个数组合并
     *
     * @param first
     * @param rest
     * @param <T>
     * @return
     */
    public static <T> T[] concatAll(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }

        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            // src position dest position length
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }

        return result;
    }
}
