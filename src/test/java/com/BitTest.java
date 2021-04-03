package com;

import com.zstu.structure.pojo.ParseableNode;
import com.zstu.utils.BitUtils;
import com.zstu.utils.ByteArrayUtils;
import com.zstu.utils.BytesToPrimaryType;
import com.zstu.utils.PrimaryTypeToBytes;

/**
 * 测试BitUtils工具
 *
 * @auther Stiles-JKY
 * @date 2021/2/25-0:08
 */
public class BitTest {

    public static void main(String[] args) {
        long a = 40367;
        System.out.println(Long.toBinaryString(a));
        long f_l = BitUtils.tackBitsFromLong(true, a, 57);
        System.out.println(Long.toBinaryString(f_l));
        long b_l = BitUtils.tackBitsFromLong(false, a, 7);
        System.out.println(Long.toBinaryString(b_l));
//        byte[] bytes = PrimaryTypeToBytes.longToBytesBig(a, 8);
//        System.out.println(ByteArrayUtils.printAsHex(bytes));
//        long bitsFromByteArray = ByteArrayUtils.getBitsFromByteArray(bytes, 6, 9);
//        byte[] longToBytesBig = PrimaryTypeToBytes.longToBytesBig(bitsFromByteArray, 8);
//        long bitsFromByteArray1 = ByteArrayUtils.getBitsFromByteArray(bytes, 7, 4);
//        long bitsFromByteArray2 = ByteArrayUtils.getBitsFromByteArray(bytes, 7, 8);
//        System.out.println(Long.toBinaryString(bitsFromByteArray));
//        System.out.println(Long.toBinaryString(bitsFromByteArray1));
//        System.out.println(Long.toBinaryString(bitsFromByteArray2));
//        System.out.println(bitsFromByteArray);
//        System.out.println(bitsFromByteArray1);
//        System.out.println(bitsFromByteArray2);
//        System.out.println(ParseableNode.parsePrimaryType(longToBytesBig, "int", "big"));
//        int i = 1;
//        i <<= 31;
//        i >>>= 31;
//        i >>>= 1;
//        System.out.println(Integer.toBinaryString(i).length());
//        System.out.println(Integer.toBinaryString(i));
//        System.out.println(i);
    }
}
