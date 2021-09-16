package com;

/**
 * @auther Stiles-JKY
 * @date 2021/4/14-11:19
 */
public class ThreadTest {

    public static volatile boolean flag;

    public static void main(String[] args) {
        Thread num_T = new Thread(() -> {
            int i = 1;
            while (true) {
                if (!flag) {
                    System.out.println(i++);
                    System.out.println(i++);
                    flag = true;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        Thread vab_T = new Thread(() -> {
            char c = 'A';
            while (true) {
                if(flag) {
                    System.out.println((char)c++);
                    flag = false;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        num_T.start();
        vab_T.start();
    }
}
