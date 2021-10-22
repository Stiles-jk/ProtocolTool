package com.zstu.structure.pojo;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.zstu.exception.ParsedException;
import com.zstu.utils.ByteArrayUtils;
import com.zstu.utils.BytesToPrimaryType;
import org.jdom.Element;
import sun.net.www.protocol.http.HttpURLConnection;

/**
 * 对网络协议中一个描述字段的抽象；例如同步头字段，数据字段等。
 * Block的长度的单位为byte；如果某一字段表述时使用bit，则需要对其进行bit分割
 *
 * @author jky
 */
public class Block {

    public String frameName;// 帧名称
    public String frameAddr;// 帧地址
    public String name;// block 名称
    private byte pass;// 是否跳过解析
    private int length;// length==-1表示为可变长字段
    private int offset = 0;
    private String type;
    private byte typesize = -1;
    private String endian = "big";
    //是否进行crc校验
    private boolean shouldCheck;

    private byte[] buffer;//存放数据帧中的一个数据块，单位为byte
    private List<ParseableNode> parseableNodes;//当前block中待解析节点

    public Block(Element blockElement, Frame frame) {
        this.name = blockElement.getAttributeValue("name");
        this.pass = Byte.parseByte(blockElement.getAttributeValue("pass"), 16);
        this.length = Integer.parseInt(blockElement.getAttributeValue("length"));
        this.type = blockElement.getAttributeValue("type");
        this.typesize = blockElement.getAttribute("typesize") == null ? -1 : Byte.parseByte(blockElement.getAttributeValue("typesize"));
        if (blockElement.getAttribute("endian") != null) {
            this.endian = Byte.parseByte(blockElement.getAttributeValue("endian"), 16) == 1 ? "big" : "little";
        }
        this.shouldCheck = Boolean.parseBoolean(blockElement.getAttributeValue("check"));
        this.buffer = new byte[this.length];
        this.frameName = frame.frameName;
        this.frameAddr = frame.frameAddr;

        this.parseableNodes = new LinkedList<>();
        creatParseableNodes(blockElement);
    }

    private void creatParseableNodes(Element block) {
        List<Element> pElements = block.getChildren();
        for (Element pElement : pElements) {
            ParseableNode pn = null;
            if ("seg".equals(pElement.getName())) {
                pn = new SegNode(pElement);
            } else if ("time".equals(pElement.getName())) {
                pn = new TimeNode(pElement, this);
            } else if ("random".equals(pElement.getName())) {
                pn = new RandomNode(pElement, this);
            } else if ("loop".equals(pElement.getName())) {
                pn = new LoopNode(pElement, this);
            }
            if (pn == null) continue;
            pn.frameName = this.frameName;
            pn.frameAddr = this.frameAddr;
            pn.blockName = this.name;
            this.parseableNodes.add(pn);
        }
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    public void parseData(List<ParsedVar> parsedVars) throws ParsedException {
        //pass current block
        if (this.pass == 1) {
            ParsedVar var = new ParsedVar();
            var.blockName = name;
            var.frameName = this.frameName;
            var.frameAddr = this.frameAddr;
            var.parsed = false;
            var.buffer = buffer;
            parsedVars.add(var);
            Arrays.fill(buffer, (byte) 0);
            offset = 0;
            return;
        }
        //block作为基本类型的数组
        if (type != null && type.contains("-array") && typesize != -1) {
            Object array = parseToPrimaryArray(buffer, type, typesize, endian);
            ParsedVar var = new ParsedVar();
            var.blockName = name;
            var.frameName = this.frameName;
            var.frameAddr = this.frameAddr;
            var.parsed = true;
            var.buffer = buffer;
            var.value = array;
            var.valueType = type;
            var.valueName = name;
            parsedVars.add(var);
        } else {
            //按照block中的seg进行解析
            for (ParseableNode p : parseableNodes) {
                offset = p.parse(buffer, offset, parsedVars);
            }
        }
        //清空buffer中的数据
        Arrays.fill(buffer, (byte) 0);
        offset = 0;
    }

    private Object parseToPrimaryArray(byte[] bytes, String type, int typesize, String endian) {
        Object array = null;
        int size = bytes.length / typesize;
        int offset = 0;
        switch (type) {
            case "byte-array":
                array = bytes;
                break;
            case "int-array":
                int[] ia = new int[size];
                for (int i = 0; i < size; i++) {
                    byte[] temp = ByteArrayUtils.copySubArray(bytes, offset, typesize);
                    offset += typesize;
                    ia[i] = BytesToPrimaryType.toInt(temp, endian);
                }
                array = ia;
                break;
            case "long-array":
                long[] la = new long[size];
                for (int i = 0; i < size; i++) {
                    byte[] temp = ByteArrayUtils.copySubArray(bytes, offset, typesize);
                    offset += typesize;
                    la[i] = BytesToPrimaryType.toLong(temp, endian);
                }
                array = la;
                break;
            case "double-array":
                double[] da = new double[size];
                for (int i = 0; i < size; i++) {
                    byte[] temp = ByteArrayUtils.copySubArray(bytes, offset, typesize);
                    offset += typesize;
                    da[i] = BytesToPrimaryType.toDouble(temp, endian);
                }
                array = da;
                break;
            case "char-array":
                char[] ca = new char[size];
                for (int i = 0; i < size; i++) {
                    byte[] temp = ByteArrayUtils.copySubArray(bytes, offset, typesize);
                    offset += typesize;
                    ca[i] = BytesToPrimaryType.toChar(temp);
                }
                array = ca;
                break;
            case "boolean-array":
                boolean[] ba = new boolean[size];
                for (int i = 0; i < size; i++) {
                    ba[i] = BytesToPrimaryType.toBoolean(bytes[i]);
                }
                array = ba;
                break;
            case "short-array":
                short[] sa = new short[size];
                for (int i = 0; i < size; i++) {
                    byte[] temp = ByteArrayUtils.copySubArray(bytes, offset, typesize);
                    offset += typesize;
                    sa[i] = BytesToPrimaryType.toShort(temp, endian);
                }
                array = sa;
                break;
            default:
                break;
        }
        return array;
    }


    public boolean getShouldCheck() {
        return this.shouldCheck;
    }

    public int getLength() {
        return this.length;
    }

    public String getName() {
        return this.name;
    }

    public byte getPass() {
        return pass;
    }

    public int getOffset() {
        return offset;
    }

    public List<ParseableNode> getParseableNodes() {
        return parseableNodes;
    }
}
