package com.zstu.exception;

/**
 * 协议描述文件解析异常
 *
 * @auther Stiles-JKY
 * @date 2021/2/24-20:42
 */
public class ProtoNodeNotFoundException extends Exception {
    private static final long serialVersionUID = 20212417L;

    public ProtoNodeNotFoundException(String message) {
        super(message);
    }
}
