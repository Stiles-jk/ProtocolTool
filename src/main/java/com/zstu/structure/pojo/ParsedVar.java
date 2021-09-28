package com.zstu.structure.pojo;

import com.zstu.utils.ByteArrayUtils;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 代表一个parseable解析出的数据
 *
 * @author jky
 */
public class ParsedVar implements Serializable {

    private static final long serialVersionUID = 20210217L;
    public String frameName;//协议名称
    public String frameAddr;//1553b专用，协议对应子地址
    public String blockName;//块名称
    public String valueName;//值对应的名称
    public boolean parsed = false;
    public String valueType;//数据类型
    public Object value;//值
    public String deviceName;//值对应的设备名称
    public byte deviceId;//值对应的设备编号
    public byte[] buffer;
    public boolean random;

    @Override
    public String toString() {
        return "ParsedVar{" +
                "frameName='" + frameName + '\'' +
                ", frameAddr='" + frameAddr + '\'' +
                ", blockName='" + blockName + '\'' +
                ", valueName='" + valueName + '\'' +
                ", parsed=" + parsed +
                ", valueType='" + valueType + '\'' +
                ", value=" + value +
                ", deviceName='" + deviceName + '\'' +
                ", deviceId=" + deviceId +
                ", buffer=" + Arrays.toString(buffer) +
                ", random=" + random +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
