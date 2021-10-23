package com.zstu.structure.pojo;

import com.zstu.utils.ByteArrayUtils;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * 时间数据专用,
 *
 * @author jky
 */
public class TimeNode extends ParseableNode {

    private int length;
    private List<SegNode> timeSegs;

    public TimeNode(Element timeElement, Block block) {
        if (timeElement == null) return;
        super.nodeType = "time";
        initBaseInfo(timeElement);
        List<Element> segs = timeElement.getChildren("seg");
        timeSegs = new ArrayList<>();
        super.frameName = block.frameName;
        super.frameAddr = block.frameAddr;
        super.blockName = block.name;
        parseTimeSegs(segs);
    }

    /**
     * @param buffer     block中的buffer
     * @param offset     block中offset的相对位置
     * @param parsedVars 解析得到的数据链
     * @return
     */
    @Override
    public int parse(byte[] buffer, int offset, List<ParsedVar> parsedVars) {
        StringBuilder time = new StringBuilder();
        byte[] timeBytes = ByteArrayUtils.copySubArray(buffer, offset, length);
        ParsedVar var = new ParsedVar();
        int timeOffset = 0;
        for (SegNode timeSeg : timeSegs) {
            ParsedVar tVar = new ParsedVar();
            timeOffset = timeSeg.parse(timeBytes, timeOffset, tVar);
            time.append(tVar.value);
            time.append("/");
        }
        var.valueName = "time";
        var.valueType = "time";
        var.value = time.toString();
        var.buffer = timeBytes;
        addBaseInfo(var);
        parsedVars.add(var);
        offset += length;
        return offset;
    }

    public int getLength() {
        return length;
    }

    public List<SegNode> parseTimeSegs(List<Element> segElements) {
        for (int i = 0; i < segElements.size(); i++) {
            Element seg = segElements.get(i);
            this.timeSegs.add(new SegNode(seg, this));
            this.length += timeSegs.get(i).getLength();
        }
        return timeSegs;
    }

    public List<SegNode> getTimeSegs() {
        return timeSegs;
    }
}
