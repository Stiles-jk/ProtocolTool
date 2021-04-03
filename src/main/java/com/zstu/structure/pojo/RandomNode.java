package com.zstu.structure.pojo;

import java.util.ArrayList;
import java.util.List;

import com.zstu.utils.BitUtils;
import com.zstu.utils.ByteArrayUtils;
import com.zstu.utils.BytesToPrimaryType;
import com.zstu.utils.PrimaryTypeToBytes;
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
    private boolean end;
    private byte preBits;
    private byte[] reduceBytes;
    private byte pass;
    private String name;

    public RandomNode(Element randomElement, Block block) {
        super.nodeType = "random";
        this.pass = (byte) (randomElement.getAttributeValue("pass") == null ? 1 : Byte.parseByte(randomElement.getAttributeValue("pass")));
        this.name = randomElement.getAttributeValue("name");
        List<Element> children = randomElement.getChildren();
        this.segs = new ArrayList<>();
        super.frameName = block.frameName;
        super.frameAddr = block.frameAddr;
        super.blockName = block.name;
        for (Element c : children) {
            String cName = c.getName();
            if (cName.equals("length")) {
                getLengthInfo(c);
            } else if (cName.equals("seg")) {
                this.segs.add(new SegNode(c, this));
            }
        }

    }

    private void getLengthInfo(Element length) {
        lengthRange = Byte.parseByte(length.getAttributeValue("typesize"));
        lengthRangeUnit = length.getAttributeValue("unit");
        lengthRangeEndian = length.getAttributeValue("endian") == null ? "big" : (Byte.parseByte(length.getAttributeValue("endian"), 16) == 1 ? "big" : "little");
        if ("bit".equals(lengthRangeUnit)) {
            this.preBits = Byte.parseByte(length.getAttributeValue("preBits"));
            if (length.getAttribute("end") != null) {
                this.end = Boolean.parseBoolean(length.getAttributeValue("end"));
            }
        }
    }

    public byte[] getReduceBytes() {
        return this.reduceBytes;
    }

    private int getLengthInfo(byte[] buffer, int offset, List<ParsedVar> parsedVars) {
        ParsedVar var = new ParsedVar();
        var.parsed = true;
        var.valueName = "length";
        var.frameName = super.frameName;
        var.frameAddr = super.frameAddr;
        var.valueType = "int";
        var.blockName = blockName;
        if ("bit".equals(this.lengthRangeUnit)) {
            long length = BitUtils.getBitsFromByteArray(buffer, offset, preBits, lengthRange, lengthRangeEndian);
            var.buffer = PrimaryTypeToBytes.longToBytes(length, lengthRange > 8 ? lengthRange / 8 + 1 : 1, lengthRangeEndian);
            var.value = length;
            parsedVars.add(var);
            return (int) length;
        } else {
            byte[] bytes = ByteArrayUtils.copySubArray(buffer, offset, lengthRange);
            int length = BytesToPrimaryType.toInt(bytes, lengthRangeEndian);
            var.buffer = bytes;
            var.value = length;
            parsedVars.add(var);
            return length;
        }
    }

    @Override
    public int parse(byte[] buffer, int offset, List<ParsedVar> parsedVars) {

        //获取长度信息
        int lengthInfo = getLengthInfo(buffer, offset, parsedVars);
        if ("bit".equals(lengthRangeUnit)) {
            if (end) {
                offset += (preBits + lengthRange) / 8;
            }
        } else {
            offset += lengthRange;
        }
        //获取lengthInfo长度的数据
        byte[] dataRange = ByteArrayUtils.copySubArray(buffer, offset, lengthInfo);
        if (this.pass == 1) {
            ParsedVar var = new ParsedVar();
            var.buffer = dataRange;
            var.value = dataRange;
            var.valueName = this.name;
            var.valueType = "byte-array";
            var.parsed = false;
            parsedVars.add(var);
            offset += dataRange.length;
            return offset;
        }
        int index = 0;
        while (lengthInfo > index) {
            for (SegNode seg : segs) {
                index = seg.parse(dataRange, index, parsedVars);
                ParsedVar var = parsedVars.get(parsedVars.size() - 1);
                var.random = true;
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

    public byte getPass() {
        return pass;
    }

    public String getName() {
        return name;
    }
}
