package com.zstu.structure.pojo;

import java.util.LinkedList;
import java.util.List;

import com.zstu.exception.ParsedException;
import org.jdom.Element;

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

    private byte[] buffer;//存放数据帧中的一个数据块，单位为byte
    private List<ParseableNode> parseableNodes;//当前block中待解析节点

    public Block(Element blockElement, Frame frame) {
        this.name = blockElement.getAttributeValue("name");
        this.pass = Byte.parseByte(blockElement.getAttributeValue("pass"), 16);
        this.length = Integer.parseInt(blockElement.getAttributeValue("length"));
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
            if (pElement.getName().equals("seg")) {
                pn = new SegNode(pElement);
            } else if (pElement.getName().equals("time")) {
                pn = new TimeNode(pElement, this);
            } else if (pElement.getName().equals("random")) {
                pn = new RandomNode(pElement, this);
            } else if (pElement.getName().equals("loop")) {
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
            var.parsed = true;
            var.buffer = buffer;
            parsedVars.add(var);
            return;
        }
        for (ParseableNode p : parseableNodes) {
            offset = p.parse(buffer, offset, parsedVars);
        }
        offset = 0;
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
