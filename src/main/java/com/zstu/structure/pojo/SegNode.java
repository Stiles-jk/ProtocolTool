package com.zstu.structure.pojo;

import com.zstu.utils.BitUtils;
import com.zstu.utils.ByteArrayUtils;
import com.zstu.utils.BytesToPrimaryType;
import com.zstu.utils.PrimaryTypeToBytes;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SegNode 是块中某一信息量的值,编写xml时要保证值的单一性;每一个seg都要有明确的长度定义；
 *
 * @author jky
 */
public class SegNode extends ParseableNode {
    /**数据类型*/
    private final String type;
    /**数据所占内存大小*/
    private final byte typeSize;
    /**前面已经使用的bit数量*/
    private byte preBits;
    /**是否从此处开始停止位解析*/
    private boolean end;
    /**数据所占内存大小的单位*/
    private final String unit;
    /**大小端*/
    private final String endian;
    /**是否跳过*/
    private final byte pass;
    /**byte长度*/
    private final int length;

    public SegNode(Element seg) {
        super.nodeType = "seg";
        super.initBaseInfo(seg);
        this.type = seg.getAttributeValue("type");
        this.typeSize = seg.getAttributeValue("typesize") == null ? BytesToPrimaryType.getTypeDefaultSize(type) : Byte.parseByte(seg.getAttributeValue("typesize"));
        this.unit = seg.getAttributeValue("unit") == null ? "byte" : seg.getAttributeValue("unit");
        this.endian = seg.getAttributeValue("endian") == null ? "big" : (Byte.parseByte(seg.getAttributeValue("endian"), 16) == 1 ? "big" : "little");
        this.pass = (byte) (seg.getAttributeValue("pass") == null ? 1 : Byte.parseByte(seg.getAttributeValue("pass")));
        this.length = super.calcLength(unit, typeSize);
        if ("bit".equals(unit)) {
            preBits = Byte.parseByte(seg.getAttributeValue("preBits"));
            if (seg.getAttribute("end") != null) {
                end = Boolean.parseBoolean(seg.getAttributeValue("end"));
            }
        }
    }

    public SegNode(Element seg, ParseableNode pn) {
        this(seg);
        super.frameName = pn.frameName;
        super.frameAddr = pn.frameAddr;
        super.blockName = pn.blockName;
    }

    /**
     * 解析seg对应的数据
     *
     * @param buffer     seg父节点中的buffer
     * @param offset     seg父节点中的buffer相对的offset
     * @param parsedVars 解析出数据存放的位置，一个seg一定只对应一个parsedVar
     * @return
     */
    @Override
    public int parse(byte[] buffer, int offset, List<ParsedVar> parsedVars) {

        ParsedVar var = new ParsedVar();
//        System.out.println(ByteArrayUtils.printAsHex(buffer));
        //get bytes from buffer
        if ("bit".equals(this.unit)) {
            offset = parseAsBit(buffer, offset, var);

        } else {
            offset = parseAsByte(buffer, offset, var);
        }
        var.valueName = this.nodeName;
        var.valueType = this.type;
        if (super.dev != null) var.deviceName = super.dev;
        if (super.devId != null) var.deviceId = Byte.parseByte(super.devId);
        addBaseInfo(var);
        parsedVars.add(var);
        return offset;
    }

    private int parseAsByte(byte[] buffer, int offset, ParsedVar var) {
        byte[] segBytes = null;

        try {
//                System.out.println(Arrays.toString(buffer));
//                System.out.println("length : " + length);
            segBytes = ByteArrayUtils.copySubArray(buffer, offset, length);
//                System.out.println("copyData : " + ByteArrayUtils.printAsHex(segBytes));
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("error offset: " + offset);
            System.out.println("error segName: " + super.nodeName);
            System.out.println("error length: " + length);
        }

        if (pass == 1) {
            var.parsed = false;
            var.value = segBytes;
        } else {
            parseAsBytes(segBytes, var);
        }

        offset += length;
        return offset;
    }

    private int parseAsBit(byte[] buffer, int offset, ParsedVar var) {
        long segLong = BitUtils.getBitsFromByteArray(buffer, offset, preBits, typeSize, endian);
        System.out.println(Long.toBinaryString(segLong));
        if (pass == 1) {
            var.parsed = false;
            var.value = segLong;
        } else {
            parseAsBits(segLong, var);
        }
        if (end) {
            offset += ((preBits + typeSize) / 8);//位操作结束，加上offset
        }
        return offset;
    }

    public int parse(byte[] buffer, int offset, List<ParsedVar> parsedVars, String suffix) {
        offset = this.parse(buffer, offset, parsedVars);
        ParsedVar var = parsedVars.get(parsedVars.size() - 1);
        var.valueName = var.valueName.concat(suffix);
        return offset;
    }

    public int parse(byte[] buffer, int offset, ParsedVar var) {
        List<ParsedVar> vars = new ArrayList<>();
        offset = parse(buffer, offset, vars);
        var.valueType = vars.get(0).valueType;
        var.valueName = vars.get(0).valueName;
        var.value = vars.get(0).value;
        var.blockName = vars.get(0).blockName;
        var.buffer = vars.get(0).buffer;
        var.deviceId = vars.get(0).deviceId;
        var.deviceName = vars.get(0).deviceName;
        var.frameAddr = vars.get(0).frameAddr;
        var.frameName = vars.get(0).frameName;
        var.parsed = vars.get(0).parsed;
        return offset;
    }

    private void parseAsBits(long bits, ParsedVar var) {
        var.buffer = PrimaryTypeToBytes.longToBytes(bits, typeSize > 8 ? typeSize / 8 + 1 : 1, endian);
        if (pass == 1) {
            var.parsed = false;
            return;
        }
        var.value = parsePrimaryType(var.buffer, type, endian);
        var.parsed = true;
    }

    private void parseAsBytes(byte[] segBytes, ParsedVar var) {
        var.buffer = segBytes;
        if (pass == 1) {
            var.parsed = false;
            return;
        }
        var.value = parsePrimaryType(segBytes, type, endian);
        var.parsed = true;
    }

    public String getType() {
        return type;
    }

    public byte getTypeSize() {
        return typeSize;
    }

    public String getUnit() {
        return unit;
    }

    public String getEndian() {
        return endian;
    }

    public byte getPass() {
        return pass;
    }

    public int getLength() {
        return length;
    }
}
