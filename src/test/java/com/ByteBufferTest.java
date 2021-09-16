package com;

import com.zstu.utils.ByteArrayUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @auther Stiles-JKY
 * @date 2021/4/8-23:16
 */
public class ByteBufferTest {

    public static void main(String[] args) {
        ByteBuffer bb = ByteBuffer.allocate(1024);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        short s1 = 1;
        short s2 = 2;
        long l1 = 3;
        long l2 = 4;
        bb.putShort(s1);
        bb.putShort(s2);
        bb.putLong(l1);
        bb.putLong(l2);
        bb.flip();
        byte[] data = new byte[bb.limit()];
        bb.get(data);
        System.out.println(ByteArrayUtils.printAsHex(data));
    }
}
