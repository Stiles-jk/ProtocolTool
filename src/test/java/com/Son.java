package com;

/**
 * @auther Stiles-JKY
 * @date 2021/2/25-22:03
 */
public class Son extends Father {
    public final static String a = "son";
    public byte[] arr = null;

    public Son(byte[] arr) {
        this.arr = arr;
    }

    @Override
    public String getA() {
        return a;
    }
}
