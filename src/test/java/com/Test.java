package com;

import com.zstu.utils.ByteArrayUtils;
import com.zstu.utils.BytesToPrimaryType;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Calendar;

/**
 * @auther Stiles-JKY
 * @date 2021/2/23-10:26
 */
public class Test {

    public static void main(String[] args) throws IOException {
//        System.out.println(InetAddress.getLocalHost().getHostAddress());
//        System.out.println(ByteArrayUtils.printAsHex(new byte[]{1, 2, 16, 125}));
//        byte a = 1;
//        OutputStream os = new FileOutputStream("ctrl.bin");
//        os.write(a);
//        os.flush();
//        os.close();
//        String hex = "0x01";
//        System.out.println(hex.substring(2));
//        byte[] a = {1,3,2};
//        byte[] b = {1,3,3};
//        System.out.println(Arrays.equals(a,b));

//        byte[] arr = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
//        byte[][] bytes = ByteArrayUtils.divideTwo(arr, 0, 4);
//        System.out.println(Arrays.toString(bytes[0]));
//        arr = bytes[1];
//        System.out.println(Arrays.toString(arr));

//        String a = "00,00,00,01,00,00,00,01,00,00,00,01,be,be,be,be,03,03,03,03,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00";
//        System.out.println(Arrays.toString(a.split(",")));
//        byte[] arr = {1,2,3,4};
//        Son s = new Son(arr);
//        arr = null;
//        System.out.println(Arrays.toString(s.arr));
//        System.out.println(s.getA());
        byte[] date = {0, 0, 7, -27, 0, 0, 0, 2, 0, 0, 0, 7, 0, 0, 0, 17, 0, 0, 0, 3, 0, 0, 0, 37};
        System.out.println(Calendar.getInstance().get(Calendar.MONTH));
        int offset = 0;
        while (offset < date.length) {
            byte[] bytes = ByteArrayUtils.copySubArray(date, offset, 4);
            System.out.println(BytesToPrimaryType.toInt(bytes));
            offset += 4;
        }
    }


}
