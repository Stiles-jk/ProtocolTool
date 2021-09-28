package com.zstu;

import com.zstu.exception.CRCException;
import com.zstu.exception.NodeAttributeNotFoundException;
import com.zstu.exception.ParsedException;
import com.zstu.exception.ProtoNodeNotFoundException;
import com.zstu.structure.pojo.Frame;
import com.zstu.structure.pojo.FrameFlag;
import com.zstu.structure.pojo.ParsedVar;
import com.zstu.utils.ByteArrayUtils;
import com.zstu.utils.XMLUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 管理xml文件中定义的数据帧协议
 *
 * @auther Stiles-JKY
 * @date 2021/2/14-23:26
 */
public class FrameManager {

    private List<Frame> frameList;
    private boolean OESwap;

    private static volatile FrameManager FRAMEMANAGER = null;

    private FrameManager() {
        this.frameList = new LinkedList<>();
    }

    public static FrameManager getInstance() {
        if (FRAMEMANAGER == null) {
            synchronized (FrameManager.class) {
                if (FRAMEMANAGER == null) {
                    FRAMEMANAGER = new FrameManager();
                }
            }
        }
        return FRAMEMANAGER;
    }

    public List<Frame> getFrameList() {
        return this.frameList;
    }

    public void registerFrame(Frame frame) {
        this.frameList.add(frame);
    }

    /**
     * 获取指定的数据帧解析链
     *
     * @param flag
     * @return
     */
    public Frame getFrame(FrameFlag flag) {
        return frameList.stream().filter(frame -> frame.getFlag().equals(flag)).findFirst().get();
    }

    /**
     * 从xml文件中解析出所有的协议，并注册到FrameManager中
     *
     * @param path
     * @throws IOException
     * @throws JDOMException
     */
    public void createFramesList(String path) throws IOException, JDOMException, NodeAttributeNotFoundException, ProtoNodeNotFoundException {
        if (path == null) throw new RuntimeException("path is null");
        File xmlFile = new File(path);
        if (xmlFile.exists() == false || xmlFile.canRead() == false)
            throw new FileNotFoundException("can't find xml file" + path);
        Element root = XMLUtils.getRoot(xmlFile);
        OESwap = Boolean.parseBoolean(root.getAttributeValue("OESwap"));
        System.out.println("OESwap: " + OESwap);
        List<Element> frames = root.getChildren("frame");
        for (Element frame : frames) {
            this.frameList.add(new Frame(frame));
        }
    }

    public List<ParsedVar> doParse(byte[] data) throws ParsedException, CRCException {

        Frame frame = null;
        for (Frame f : frameList) {
            boolean flag = f.getFlag().checkStarter(data);
            if (flag) frame = f;
        }
        if (frame == null) throw new ParsedException("can't match any farme: " + ByteArrayUtils.printAsHex(data));

        //开始解析
        List<ParsedVar> parsedVars = new ArrayList<>();
        if (frame != null) {
            frame.parse(data, parsedVars, OESwap);
        }
        return parsedVars;
    }


}
