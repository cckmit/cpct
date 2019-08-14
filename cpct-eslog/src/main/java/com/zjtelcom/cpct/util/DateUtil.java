package com.zjtelcom.cpct.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/7/9.
 */
public class DateUtil {
    private static DateFormat ldf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String formatDate(Date date) {
        return ldf.format(date);
    }
}
