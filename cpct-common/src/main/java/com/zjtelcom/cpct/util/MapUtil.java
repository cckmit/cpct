package com.zjtelcom.cpct.util;

public class MapUtil {

    public static Integer getIntNum(Object obj){
        try {
            return  Integer.parseInt(String.valueOf(obj));
        }catch (Throwable e){

        }
        return  0;
    }


    public static String getString(Object obj){
        try {
            return  String.valueOf(obj);
        }catch (Throwable e){

        }
        return  "";
    }

}
