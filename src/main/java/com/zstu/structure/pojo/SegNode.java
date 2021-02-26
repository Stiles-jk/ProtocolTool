package com.zstu.structure.pojo;

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

    private String type;// 数据类型
    private byte typeSize;// 数据所占内存大小
    private String unit;// 数据所占内存大小的单位
    private String endian;// 大小端
    private byte pass;//是否跳过
    private int length;//byte长度

    public SegNode(Element seg) {
        super.nodeType = "seg";
        super.initBaseInfo(seg);
        this.type = seg.getAttributeValue("type");
        this.typeSize = seg.getAttributeValue("typesize") == null ? BytesToPrimaryType.getTypeDefaultSize(type) : Byte.parseByte(seg.getAttributeValue("typesize"));
        this.unit = seg.getAttributeValue("unit") == null ? "byte" : seg.getAttributeValue("unit");
        this.endian = seg.getAttributeValue("endian") == null ? "big" : (Byte.parseByte(seg.getAttributeValue("endian"), 16) == 1 ? "big" : "little");
        this.pass = (byte) (seg.getAttributeValue("pass") == null ? 1 : Byte.parseByte(seg.getAttributeValue("pass")));
        this.length = super.calcLength(unit, typeSize);
    }

    public SegNode(Element seg,ParseableNode pn) {
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
        //get bytes from buffer
        if ("bit".equals(this.unit)) {
            long segLong = ByteArrayUtils.getBitsFromByteArray(buffer, offset, typeSize);
            parseAsBits(segLong, var);
        } else {
            byte[] segBytes = null;
            try {
                System.out.println(Arrays.toString(buffer));
                System.out.println("copyData : " + offset);
                System.out.println("length : " + length);
                segBytes = ByteArrayUtils.copySubArray(buffer, offset, length);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("error offset: " + offset);
                System.out.println("error segName: " + super.nodeName);
                System.out.println("error length: " + length);
            }
            parseAsBytes(segBytes, var);
        }

        var.valueName = this.nodeName;
        var.valueType = this.type;
        if (super.dev != null) var.deviceName = super.dev;
        if (super.devId != null) var.deviceId = Byte.parseByte(super.devId);
        addBaseInfo(var);
        parsedVars.add(var);
        offset += length;
        return offset;
    }

    public int parse(byte[] buffer, int offset, ParsedVar var) {
        List<ParsedVar> vars = new ArrayList<>();
        parse(buffer, offset, vars);
        var = vars.get(0);
        return offset += length;
    }

    private void parseAsBits(long bits, ParsedVar var) {
        var.buffer = PrimaryTypeToBytes.longToBytes(bits, 8, endian);
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
