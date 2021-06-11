package com.example.h26xprojection;

/**
 * Created by Zach on 2021/6/11 16:22
 */
public class DataUtil {

    public static String byte2hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String tmp = null;

        for (byte b : bytes) {
            tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1) {
                tmp = "0" + tmp.toUpperCase();
            }
            sb.append(tmp.toUpperCase()).append(" ");
        }
        return sb.toString();
    }
}
