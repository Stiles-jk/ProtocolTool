package com.zstu.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 用于对网络数据为TLV格式时，对其进行打包解包工具
 *
 * @author jky
 */
public class TLVUtils {

    private final static int OUTTER = 0x01;
    private final static int INNER = 0x00;

    /**
     * 从没有嵌套结构的TLV数据帧中抽离出value区域的值，
     *
     * @param frame  原数据帧
     * @param offset 数据帧的指针，指向TLV的L块的首部
     * @return byte-array
     */
    public byte[] getBlock(byte[] frame, int offset, int length) {
        byte[] result = new byte[length];
        System.arraycopy(frame, offset, result, 0, length);
        return result;
    }

    public int getTagValue(byte[] frame, int offset, Map<String, Byte> result) {
        if (frame == null) {
            return -1;
        }

        byte type = getBlock(frame, 0, 1)[0];
        if (type == OUTTER) {
            offset += getByOutter(frame, result);
        } else if (type == INNER) {
            offset += getByInner(frame, result);
        }
        return offset;
    }

    private int getByOutter(byte[] frame, Map<String, Byte> result) {
        byte[] tagBlock = getBlock(frame, 0, 2);
        result.put("proto", tagBlock[0]);
        result.put("c_id", tagBlock[1]);
        return 2;
    }

    private int getByInner(byte[] frame, Map<String, Byte> result) {
        byte[] tagBlock = getBlock(frame, 0, 3);
        result.put("proto", tagBlock[0]);
        result.put("f_id", tagBlock[1]);
        result.put("c_id", tagBlock[2]);
        return 3;
    }

    // ----------------------- SIMPLE TlV ------------------------------

    /**
     * 将原始数据包装为简单TLV结构
     *
     * @param oData
     * @return
     */
    public byte[] simpleTLVPackage(byte[] oData) {
        byte[] tlv = new byte[0];

        return tlv;
    }


    public List<byte[]> getTLVValue(byte[] tlvBytes) {
        List<byte[]> tlv = new LinkedList<>();

        return tlv;
    }




}
