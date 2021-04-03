package com;

import com.sun.media.sound.SoftTuning;
import com.zstu.utils.ByteArrayUtils;
import com.zstu.utils.BytesToPrimaryType;
import com.zstu.utils.PrimaryTypeToBytes;

import java.util.Arrays;

/**
 * @auther Stiles-JKY
 * @date 2021/3/1-11:50
 */
public class CharTest {

    public static void main(String[] args) {
//        byte[] bytes = PrimaryTypeToBytes.charToBytes(new char[]{'0'}, "UTF-8");
//        System.out.println(Arrays.toString(bytes));
//        char c = BytesToPrimaryType.toChar((byte) 97, "UTF-8");
//        char c1 = BytesToPrimaryType.toChar(new byte[]{0, 97});
//        System.out.println(c1);
        short s = 600;
        byte[] bytes = PrimaryTypeToBytes.shortToBytes(s, "big");
        short big = BytesToPrimaryType.toShort(bytes, "big");
        System.out.println(big);
    }
}
