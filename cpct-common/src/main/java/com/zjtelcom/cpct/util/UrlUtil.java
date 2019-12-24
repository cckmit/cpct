package com.zjtelcom.cpct.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class UrlUtil {



    /**
     * 获取完整的异常栈信息
     * @param t
     * @return
     */
    public static   String getTrace(Throwable t) {
        StringWriter stringWriter= new StringWriter();
        PrintWriter writer= new PrintWriter(stringWriter);
        t.printStackTrace(writer);
        StringBuffer buffer= stringWriter.getBuffer();
        return buffer.toString();
    }


}
