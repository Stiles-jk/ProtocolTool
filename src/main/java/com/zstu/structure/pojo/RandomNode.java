package com.zstu.structure.pojo;

import java.util.ArrayList;
import java.util.List;

import com.zstu.utils.BitUtils;
import com.zstu.utils.ByteArrayUtils;
import com.zstu.utils.BytesToPrimaryType;
import org.jdom.Element;
import sun.rmi.runtime.Log;


/**
 * 可变长字段，该字段包含了可变长的数据字段，在数据字段前包含了长度信息,；可变长数据字段的解析工作,部分需要有专用的协议进行解析
 *
 * @author jky
 */
public class RandomNode extends ParseableNode {

    private List<SegNode> segs;
    private byte lengthRange;
    private String lengthRangeUnit;
    private String lengthRangeEndian;
    private byte[] reduceBytes;

    public RandomNode(Element randomElement,Block block) {
        super.nodeType = "random";
        List<Element> children = randomElement.getChildren();
        this.segs = new ArrayList<>();
        super.frameName = block.frameName;
        super.frameAddr = block.frameAddr;
        super.blockName = block.name;
        for (Element c : children) {
            String cName = c.getName();
            if (cName.equals("length")) {
                lengthRange = Byte.parseByte(c.getAttributeValue("len"));
                lengthRangeUnit = c.getAttributeValue("unit");
                lengthRangeEndian = c.getAttributeValue("endian");
            } else if (cName.equals("seg")) {
                this.segs.add(new SegNode(c,this));
            }
        }

    }

    public byte[] getReduceBytes() {
        return this.reduceBytes;
    }

    private int getLengthInfo(byte[] buffer, int offset) {
        if ("bit".equals(this.lengthRangeUnit)) {
            long length = ByteArrayUtils.getBitsFromByteArray(buffer, offset, lengthRange);
            return (int) length;
        } else {
            byte[] bytes = ByteArrayUtils.copySubArray(buffer, offset, lengthRange);
            int length = BytesToPrimaryType.toInt(bytes, lengthRangeEndian);
            return length;
        }
    }

    @Override
    public int parse(byte[] buffer, int offset, List<ParsedVar> parsedVars) {
        //获取长度信息
        int lengthInfo = getLengthInfo(buffer, offset);
        if ("bit".equals(lengthRangeUnit)) {
            offset += lengthRange / 8;
        } else {
            offset += lengthRange;
        }
        //获取长度
        byte[] dataRange = ByteArrayUtils.copySubArray(buffer, offset, lengthInfo);
        int index = 0;
        while (lengthInfo > 0) {
            for (SegNode seg : segs) {
                int segLen = seg.parse(dataRange, index, parsedVars);
                index += segLen;
                lengthInfo -= segLen;
                if (lengthInfo <= 0) break;
            }
        }
        offset += dataRange.length;
        return offset;
    }

    public List<SegNode> getSegs() {
        return segs;
    }

    public byte getLengthRange() {
        return lengthRange;
    }
}
