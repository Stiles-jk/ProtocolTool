package com.zstu.utils;

import com.zstu.structure.pojo.ParsedVar;

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
    
}
