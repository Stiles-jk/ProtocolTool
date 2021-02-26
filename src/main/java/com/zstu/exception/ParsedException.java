package com.zstu.exception;

import com.zstu.structure.pojo.ParseableNode;

import java.io.PrintStream;

/**
 * 协议数据解析异常
 *
 * @auther Stiles-JKY
 * @date 2021/2/24-17:12
 */
public class ParsedException extends Exception {

    public ParsedException(String msg) {
        super(msg);
    }

    public ParsedException(String msg, ParseableNode node) {
        String info = node.nodeName + " " + node.nodeType;
        System.out.println(msg + " " + info);
        super.printStackTrace();
    }
}
