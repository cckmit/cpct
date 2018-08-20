package com.zjtelcom.cpct.elastic.util;

import java.util.Random;

public class EsSearchUtil {



    public static  String getRandomStr(int length) {
        char[] chars = "23456789".toCharArray();
        Random r = new Random(System.currentTimeMillis());
        String string = "";

        for(int i = 0; i < length; ++i) {
            string = string + chars[r.nextInt(8)];
        }

        return string;
    }
}
