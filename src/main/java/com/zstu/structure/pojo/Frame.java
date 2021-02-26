package com.zstu.structure.pojo;

import java.util.ArrayList;
import java.util.List;

import com.zstu.exception.NodeAttributeNotFoundException;
import com.zstu.exception.ParsedException;
import com.zstu.exception.ProtoNodeNotFoundException;
import com.zstu.utils.BitUtils;
import com.zstu.utils.ByteArrayUtils;
import org.jdom.Element;


/**
 * 对一项协议的javaBean封装
 * 解析时会将数据帧分为n段block对应的byte-array；再由每个block去单独解析对应的数据段；
 *
 * @author jky
 */
public class Frame {

    private List<Block> blocks;
    int offset = 0;
    private FrameFlag flag;
    public String frameName;
    public String frameAddr;

    public Frame(Element frame) throws ProtoNodeNotFoundException, NodeAttributeNotFoundException {
        this.frameName = frame.getAttributeValue("name");
        this.frameAddr = frame.getAttributeValue("id");
        Element starter = frame.getChild("starter");
        if (starter == null) throw new ProtoNodeNotFoundException("can't find starter in " + frameName);
        flag = new FrameFlag(starter);
        getParseChain(frame);
    }

    public void parse(byte[] recvBytes, List<ParsedVar> parsedVars) throws ParsedException {
        byte[] data = ByteArrayUtils.copySubArray(recvBytes, flag.length, -1);
        for (Block block : blocks) {
            int length = block.getLength();
            try{
                byte[] blockBytes = ByteArrayUtils.copySubArray(data, offset, length);
                block.setBuffer(blockBytes);
                block.parseData(parsedVars);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println(block.getName());
                System.out.println(block.getLength());
            }
            offset += length;
        }
        offset = 0;
    }

    public List<Block> getBlocks() {
        return this.blocks;
    }

    //比对数据流中的数据
    public boolean checkFrame(byte[] bytes) {

        return false;
    }

    public FrameFlag getFlag() {
        return this.flag;
    }


    private void getParseChain(Element frame) {
        blocks = new ArrayList<>();
        List<Element> blockElements = frame.getChildren("block");
        for (Element element : blockElements) {
            blocks.add(new Block(element, this));
        }
    }
}
