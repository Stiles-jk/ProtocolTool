package com.zstu.structure.pojo;

import org.jdom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * 对应协议中的相同的部分，是对某一个或是某几个Seg块的循环解析，循环的顺序是对原有seg的顺序；
 * 例如：seg1 seg2 循环2次就是 seg1Var,seg2Var,seg1Var,seg2Var
 *
 * @auther Stiles-JKY
 * @date 2021/2/24-14:50
 */
public class LoopNode extends ParseableNode {

    private List<SegNode> loopSegs;
    private int loopTime;

    public LoopNode(Element loop,Block block) {
        super.nodeType = "loop";
        loopTime = loop.getAttribute("time") == null ? -1 : Integer.parseInt(loop.getAttributeValue("time"));
        loopSegs = new ArrayList<>();
        super.frameName = block.frameName;
        super.frameAddr = block.frameAddr;
        super.blockName = block.name;
        List<Element> segs = loop.getChildren("seg");
        if (segs != null && segs.size() > 0) {
            segs.stream().forEach(seg -> loopSegs.add(new SegNode(seg,this)));
        }
    }


    /**
     * @param buffer     block中的buffer
     * @param offset     block中offset的相对位置
     * @param parsedVars 解析得到的数据链
     * @return
     */
    @Override
    public int parse(byte[] buffer, int offset, List<ParsedVar> parsedVars) {
        int suffix = 1;
        int temp = loopTime;
        if (temp == -1) {
            int index = 0;
            while (offset < buffer.length) {
                offset = loopSegs.get(index % loopSegs.size()).parse(buffer, offset, parsedVars);
                index++;
            }
        } else {
            while (temp > 0) {
                for (SegNode seg : loopSegs) {
                    offset = seg.parse(buffer, offset, parsedVars,suffix+"");
                    suffix++;
                }
                temp--;
            }
        }
        return offset;
    }

    public List<SegNode> getLoopSegs() {
        return loopSegs;
    }

    public int getLoopTime() {
        return loopTime;
    }
}
