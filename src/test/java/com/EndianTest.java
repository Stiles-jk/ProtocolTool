package com;
import com.zstu.utils.ByteArrayUtils;
import com.zstu.utils.BytesToPrimaryType;
import com.zstu.utils.PrimaryTypeToBytes;

/**
 * @auther Stiles-JKY
 * @date 2021/3/7-14:57
 */
public class EndianTest {

    public static void main(String[] args) {
        long l = 8;
        byte[] bytes = PrimaryTypeToBytes.longToBytesLittle(l, 2);
        System.out.println(ByteArrayUtils.printAsHex(bytes));


        byte[] bytes1 = PrimaryTypeToBytes.longToBytesBig(l, 2);
        System.out.println(ByteArrayUtils.printAsHex(bytes1));

        System.out.println("------- 解码 ------- ");
        long l_l = BytesToPrimaryType.toLong(bytes, "little");
        System.out.println("little: " + l_l);
        long b_l = BytesToPrimaryType.toLong(bytes1, "big");
        System.out.println("big: " + b_l);

        System.out.println("-------- double --------");
        double d = 7.1;
        byte[] bytes2 = PrimaryTypeToBytes.doubleToBytesLittle(d, 8);
        System.out.println(ByteArrayUtils.printAsHex(bytes2));

        byte[] bytes3 = PrimaryTypeToBytes.doubleToBytesBig(d, 8);
        System.out.println(ByteArrayUtils.printAsHex(bytes3));

        System.out.println("---------- 解码 ---------");
        System.out.println("little: " + BytesToPrimaryType.toDouble(bytes2, "little"));
        System.out.println("big: " + BytesToPrimaryType.toDouble(bytes3, "big"));

        System.out.println("------- int ----------");
        int i = 8;
        byte[] bytes4 = PrimaryTypeToBytes.intToBytes(i, 2, "little");
        System.out.println(ByteArrayUtils.printAsHex(bytes4));

        byte[] bytes5 = PrimaryTypeToBytes.intToBytes(i, 2, "big");
        System.out.println(ByteArrayUtils.printAsHex(bytes5));

        System.out.println("---------- 解码 ---------");
        System.out.println("little: " + BytesToPrimaryType.toInt(bytes4, "little"));
        System.out.println("big: " + BytesToPrimaryType.toInt(bytes5, "big"));

        System.out.println("------- short ---------");
        short s = 8;
        byte[] bytes6 = PrimaryTypeToBytes.shortToBytes(s, "little");
        System.out.println(ByteArrayUtils.printAsHex(bytes6));

        byte[] bytes7 = PrimaryTypeToBytes.shortToBytes(s, "big");
        System.out.println(ByteArrayUtils.printAsHex(bytes7));

        System.out.println("---------- 解码 ---------");
        System.out.println("little: " + BytesToPrimaryType.toShort(bytes6, "little"));
        System.out.println("big: " + BytesToPrimaryType.toShort(bytes7, "big"));

        System.out.println("---------- test --------");
        long test = 1;
        byte[] bytes8 = PrimaryTypeToBytes.longToBytesBig(test, 4);
        System.out.println(ByteArrayUtils.printAsHex(bytes8));
        System.out.println(BytesToPrimaryType.toLong(new byte[]{1,0,0,0},"big"));
    }
}
