package com.zstu.structure.pojo;

import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.List;

import com.zstu.exception.CRCException;
import com.zstu.exception.NodeAttributeNotFoundException;
import com.zstu.exception.ParsedException;
import com.zstu.exception.ProtoNodeNotFoundException;
import com.zstu.utils.BitUtils;
import com.zstu.utils.ByteArrayUtils;
import com.zstu.utils.CRCUtils;
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

    public void parse(byte[] recvBytes, List<ParsedVar> parsedVars, boolean OESwap) throws ParsedException, CRCException {
        byte[] data = ByteArrayUtils.copySubArray(recvBytes, flag.length, -1);

        System.out.println("before swap: " + ByteArrayUtils.printAsHex(data));
        data = Odd_EvenSwap(data, OESwap);
        System.out.println("after swap: " + ByteArrayUtils.printAsHex(data));

        //计算CRC校验
        byte[] allFrameData = new byte[0];
        byte[] crcCheckBytes = new byte[0];

        for (Block block : blocks) {
            int length = block.getLength();
            try {
                byte[] blockBytes = ByteArrayUtils.copySubArray(data, offset, length);

                if (block.getShouldCheck()) {
                    crcCheckBytes = ByteArrayUtils.mergeBytes(crcCheckBytes, blockBytes);
                }

                if (block.getPass() != 1)
                    allFrameData = ByteArrayUtils.mergeBytes(allFrameData, blockBytes);

                block.setBuffer(blockBytes);

                block.parseData(parsedVars);

            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println(block.getName() + "parse error");
                System.out.println(block.getLength());
            }
            offset += length;
        }

        //CRC校验
        int checkSum = CRCUtils.CRC_MPEG_2(crcCheckBytes);
        if (checkSum != 0) {
            throw new CRCException(checkSum + "");
        }
        //将整帧数据添加到最后一个ParsedVar中
        addAllReceive(parsedVars, data);
        System.out.println(ByteArrayUtils.printAsHex(allFrameData));
        offset = 0;
    }

    private void addAllReceive(List<ParsedVar> vars, byte[] bytes) {
        byte[] data = ByteArrayUtils.copySubArray(bytes, 0, bytes.length);
        ParsedVar var = new ParsedVar();
        var.frameName = this.frameName;
        var.valueType = "byte-array";
        var.valueName = "allBytes";
        var.value = data;
        var.buffer = data;
        vars.add(var);
    }

    public List<Block> getBlocks() {
        return this.blocks;
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

    private byte[] Odd_EvenSwap(byte[] data, boolean swap) {
        if (!swap) return data;
        if (data == null) return new byte[]{0};
        for (int i = 0; i < data.length; i += 2) {
            byte temp = data[i];
            if (i + 1 >= data.length) break;
            data[i] = data[i + 1];
            data[i + 1] = temp;
        }
        return data;
    }
}
