package com.zstu.structure.pojo;

import com.zstu.exception.ParsedException;
import com.zstu.utils.ByteArrayUtils;
import com.zstu.utils.BytesToPrimaryType;
import org.jdom.Element;

import java.util.List;

/**
 * 可解析节点名称，当前节点的数据和设备无关时dev和devId为null；
 * 对外提供当前节点的统一属性访问，和唯一的parse方法。
 */
public abstract class ParseableNode {
    //帧相关
    public String frameName;
    public String frameAddr;
    //block相关
    public String blockName;
    //seg相关
    public String nodeType;//节点类型
    public String dev;//当前节点对应设备
    public String nodeName;//节点名称
    public String devId;//设备id

    public abstract int parse(byte[] buffer, int offset, List<ParsedVar> parsedVars) throws ParsedException;

    protected void addBaseInfo(ParsedVar var) {
        var.frameName = this.frameName;
        var.frameAddr = this.frameAddr;
        var.blockName = this.blockName;
    }

    protected Object parsePrimaryType(byte[] bytes, String type, String endian) {
        Object primaryType = null;
        switch (type) {
            case "byte":
                primaryType = bytes[0];
                break;
            case "int":
                if (bytes.length == 8) {
                    long l = BytesToPrimaryType.toLong(bytes, endian);
                    primaryType = 0x0000ffff & l;
                } else {
                    primaryType = BytesToPrimaryType.toInt(bytes, endian);
                }
                break;
            case "long":
                primaryType = BytesToPrimaryType.toLong(bytes, endian);
                break;
            case "double":
                primaryType = BytesToPrimaryType.toDouble(bytes, endian);
                break;
            case "char":
                primaryType = BytesToPrimaryType.toChar(bytes);
                break;
            case "boolean":
                primaryType = BytesToPrimaryType.toBoolean(bytes[0]);
                break;
            case "short":
                primaryType = BytesToPrimaryType.toShort(bytes, endian);
                break;
            default:
                break;
        }
        return primaryType;
    }

    protected void initBaseInfo(Element e) {
        this.dev = e.getAttributeValue("dev");
        this.nodeName = e.getAttributeValue("name");
        this.devId = e.getAttributeValue("devID");
    }

    protected int calcLength(String unit, byte typeSize) {
        return "bit".equals(unit) ? typeSize / 8 : typeSize;
    }
}
