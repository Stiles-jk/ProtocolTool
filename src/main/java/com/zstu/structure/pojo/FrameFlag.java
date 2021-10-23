package com.zstu.structure.pojo;

import com.zstu.exception.NodeAttributeNotFoundException;
import com.zstu.utils.ByteArrayUtils;
import org.jdom.Element;

import java.util.Arrays;

/**
 * 帧识别标识符，用于中当前协议栈中找到对应的协议；
 *
 * @auther Stiles-JKY
 * @date 2021/2/14-23:56
 */
public class FrameFlag {

    public byte[] starters;
    public int length;

    public FrameFlag(Element starter) throws NodeAttributeNotFoundException {
        String var = starter.getAttributeValue("var");
        if (var == null || "".equals(var))
            throw new NodeAttributeNotFoundException("can't find attribute var in " + starter.getParentElement().getAttributeValue("name"));
        String[] starterStrs = var.split(",");
        this.starters = ByteArrayUtils.hexToByteArray(starterStrs);
        System.out.println("starts: " + ByteArrayUtils.printAsHex(starters));
        if (starters == null) throw new NumberFormatException(starter.getParentElement().getAttributeValue("name"));
        this.length = starters.length;
    }

    /**
     * 查找帧头
     *
     * @param data
     * @return
     */
    public boolean checkStarter(byte[] data) {
        byte[] curStarter = ByteArrayUtils.copySubArray(data, 0, length);
        return Arrays.equals(this.starters, curStarter);
    }

}
