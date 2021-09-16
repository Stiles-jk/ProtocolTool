package com.zstu.utils;

import java.util.zip.CRC32;

/**
 * 用于CRC检测
 *
 * @auther Stiles-JKY
 * @date 2021/4/19-10:40
 */
public abstract class CRCUtils {

    /**
     * 32位CRC校验
     * 后32位，即后4个byte数组为校验码位置
     *
     * @param receiveData
     * @return
     */
    public static int CRC32Check(byte[] receiveData) {
        int checkSum = 0;
        if (receiveData.length <= 4) return -1;
        byte[] uncheckedArray = ByteArrayUtils.copySubArray(receiveData, 0, receiveData.length - 4);
        System.out.println(ByteArrayUtils.printAsHex(uncheckedArray));
        CRC32 crc32 = new CRC32();
        crc32.update(uncheckedArray);
        int value = (int) crc32.getValue();
        byte[] bytes1 = PrimaryTypeToBytes.intToBytes(value, 4, "big");
        System.out.println(ByteArrayUtils.printAsHex(bytes1));
        System.out.println(value);
        return checkSum;
    }

    /**
     * CRC32 = X32 + X26 + X23 + X22 + X16 + X12 + X11 + X10
     * + X8 + X7 + X5 + X4 + X2 + X1 + X0
     *
     * @param receiveData
     * @return
     */
    public static int CRC_MPEG_2(byte[] receiveData) {
        int checkSum = 0x00000000;
        final int gx = 0x04c11db7;
        for (int i = 0; i < receiveData.length; i++) {
            checkSum ^= receiveData[i] << 24;
            for (int j = 0; j < 8; j++) {
                if ((checkSum & 0x80000000) != 0) {
                    checkSum = (checkSum << 1) ^ gx;
                } else {
                    checkSum <<= 1;
                }
            }
        }
        return checkSum;
    }

    public static void main(String[] args) {
        byte[] array = {0x1d, (byte) 0xc2, 0x34, 0x12, 0x56, (byte) 0x93, 0x62, 0x61, (byte) 0xb0};
        int i = CRC_MPEG_2(array);
        byte[] bytes = PrimaryTypeToBytes.intToBytes(i, 4, "big");
        System.out.println(ByteArrayUtils.printAsHex(bytes));
        System.out.println(i);
    }
}
