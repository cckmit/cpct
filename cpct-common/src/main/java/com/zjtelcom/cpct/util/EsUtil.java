package com.zjtelcom.cpct.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class EsUtil {

    public static List<String> StringToList(String var1) {
        String[] array = var1.split(",");
        List<String> list = new ArrayList<String>();
        for (String str : array)
        {
            list.add(str);
        }
        return list;
    }

    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }


    public static String getRandomStr(int length) {
        char[] chars = "23456789".toCharArray();
        Random r = new Random(System.currentTimeMillis());
        String string = "";

        for(int i = 0; i < length; ++i) {
            string = string + chars[r.nextInt(8)];
        }

        return string;
    }
    public static String StringList2String(List<String> stringList){
        if (stringList == null || stringList.isEmpty()){
            return "";
        }
        String[] sts = new String[stringList.size()];
        for (int i = 0;i<sts.length;i++){
            sts[i] = stringList.get(i);
        }
        return StringUtils.join(sts,",");
    }
    /**
     * 将一个list均分成n个list,主要通过偏移量来实现的
     * @param source
     * @return
     */
    public static <T> List<List<T>> averageAssign(List<T> source,int n) {
        List<List<T>> result = new ArrayList<List<T>>();
        int remaider = source.size() % n;  //(先计算出余数)
        int number = source.size() / n;  //然后是商
        int offset = 0;//偏移量
        for (int i = 0; i < n; i++) {
            List<T> value = null;
            if (remaider > 0) {
                value = new ArrayList<T>(source.subList(i * number + offset, (i + 1) * number + offset + 1));
                remaider--;
                offset++;
            } else {
                value =  new ArrayList<T>(source.subList(i * number + offset, (i + 1) * number + offset));
            }
            result.add(value);
        }
        return result;
    }
    public static String[] getFieldList(String title,String suffix,List<String> sysArea){
        List<String> areas = new ArrayList<>();
        if (sysArea==null || sysArea.isEmpty() || sysArea.get(0).equals("") || sysArea.get(0).equals("1")){
            String[] fields =  getIndexs();
            return fields;
        }
        List<String> area = new ArrayList<>();
        for (String areaCode : sysArea){
            area.add(areaCode.length()>3 ? areaCode.substring(0,3) : areaCode);
        }
        if (area.contains("571") || area.contains("10")){
            areas.add("asset_hangzhou");
            areas.add("other_hangzhou");
        }
        if (area.contains("570")|| area.contains("20")){
            areas.add("asset_quzhou");
            areas.add("other_quzhou");
        }
        if (area.contains("572")|| area.contains("11")){
            areas.add("asset_huzhou");
            areas.add("other_huzhou");
        }
        if (area.contains("573")|| area.contains("12")){
            areas.add("asset_jiaxing");
            areas.add("other_jiaxing");
        }
        if (area.contains("574")|| area.contains("13")){
            areas.add("asset_ningbo");
            areas.add("other_ningbo");
        }
        if (area.contains("575")|| area.contains("14")){
            areas.add("asset_shaoxing");
            areas.add("other_shaoxing");
        }
        if (area.contains("576")|| area.contains("15")){
            areas.add("asset_taizhou");
            areas.add("other_taizhou");
        }
        if (area.contains("577")|| area.contains("16")){
            areas.add("asset_wenzhou");
            areas.add("other_wenzhou");
        }
        if (area.contains("578")|| area.contains("17")){
            areas.add("asset_lishui");
            areas.add("other_lishui");
        }
        if (area.contains("579")|| area.contains("18")){
            areas.add("asset_jinhua");
            areas.add("other_jinhua");
        }
        if (area.contains("580")|| area.contains("19")){
            areas.add("asset_zhoushan");
            areas.add("other_zhoushan");
        }
        String[] fields = areas.toArray(new String[areas.size()]);
        return fields;
    }

//    public static String[] getIndexs(String title,String suffix){
//        List<String> areas = new ArrayList<>();
//        areas.add("hangzhou"+suffix);
//        areas.add("quzhou"+suffix);
//        areas.add("huzhou"+suffix);
//        areas.add("jiaxing"+suffix);
//        areas.add("ningbo"+suffix);
//        areas.add("shaoxing"+suffix);
//        areas.add("taizhou"+suffix);
//        areas.add("wenzhou"+suffix);
//        areas.add("lishui"+suffix);
//        areas.add("jinhua"+suffix);
//        areas.add("zhoushan"+suffix);
//        for (int i = 0 ; i < fields.length ;i ++){
//            fields[i] = title+areas.get(i);
//        }
//        String[] indexs = areas.toArray(new String[areas.size()]);
//        return indexs;
//    }

    public static String getC3Name(String region){
        String indexName = "杭州市";
        if (region.equals("10") || region.equals("571")){
            indexName = "杭州市";
        }else if (region.equals("20") || region.equals("570")){
            indexName = "衢州市";
        }else if (region.equals("11") || region.equals("572")){
            indexName = "湖州市";
        }else if (region.equals("12") || region.equals("573")){
            indexName = "嘉兴市";
        }else if (region.equals("13") || region.equals("574")){
            indexName = "宁波市";
        }else if (region.equals("14") || region.equals("575")){
            indexName = "绍兴市";
        }else if (region.equals("15") || region.equals("576")){
            indexName = "台州市";
        }else if (region.equals("16") || region.equals("577")){
            indexName = "温州市";
        }else if (region.equals("17") || region.equals("578")){
            indexName = "丽水市";
        }else if (region.equals("18") || region.equals("579")){
            indexName = "金华市";
        }else if (region.equals("19") || region.equals("580")){
            indexName = "舟山市";
        }
        return indexName;
    }

    public static String[] getIndexName(String region,String type){
        List<String> indexList = new ArrayList<>();
        String indexName = "asset_hangzhou";
        String otherIndex = "other_hangzhou";
        if (region.equals("10") || region.equals("571")){
            indexName = type+"hangzhou";
            otherIndex =  "other_hangzhou";
            indexList.add(indexName);
            indexList.add(otherIndex);
        }else if (region.equals("20") || region.equals("570")){
            indexName = type+"quzhou";
            otherIndex =  "other_quzhou";
            indexList.add(indexName);
            indexList.add(otherIndex);
        }else if (region.equals("11") || region.equals("572")){
            indexName = type+"huzhou";
            otherIndex =  "other_huzhou";
            indexList.add(indexName);
            indexList.add(otherIndex);
        }else if (region.equals("12") || region.equals("573")){
            indexName = type+"jiaxing";
            otherIndex =  "other_jiaxing";
            indexList.add(indexName);
            indexList.add(otherIndex);
        }else if (region.equals("13") || region.equals("574")){
            indexName = type+"ningbo";
            otherIndex =  "other_ningbo";
            indexList.add(indexName);
            indexList.add(otherIndex);
        }else if (region.equals("14") || region.equals("575")){
            indexName = type+"shaoxing";
            otherIndex =  "other_shaoxing";
            indexList.add(indexName);
            indexList.add(otherIndex);
        }else if (region.equals("15") || region.equals("576")){
            indexName = type+"taizhou";
            otherIndex =  "other_taizhou";
            indexList.add(indexName);
            indexList.add(otherIndex);
        }else if (region.equals("16") || region.equals("577")){
            indexName = type+"wenzhou";
            otherIndex =  "other_wenzhou";
            indexList.add(indexName);
            indexList.add(otherIndex);
        }else if (region.equals("17") || region.equals("578")){
            indexName = type+"lishui";
            otherIndex =  "other_lishui";
            indexList.add(indexName);
            indexList.add(otherIndex);
        }else if (region.equals("18") || region.equals("579")){
            indexName = type+"jinhua";
            otherIndex =  "other_jinhua";
            indexList.add(indexName);
            indexList.add(otherIndex);
        }else if (region.equals("19") || region.equals("580")){
            indexName = type+"zhoushan";
            otherIndex =  "other_zhoushan";
            indexList.add(indexName);
            indexList.add(otherIndex);
        }
        String[] indexs = indexList.toArray(new String[indexList.size()]);
        return indexs;
    }

    public static String[] getIndexs(){
        List<String> areas = new ArrayList<>();
        List<String> areaList = new ArrayList<>();
        areas.add("hangzhou");
        areas.add("quzhou");
        areas.add("huzhou");
        areas.add("jiaxing");
        areas.add("ningbo");
        areas.add("shaoxing");
        areas.add("taizhou");
        areas.add("wenzhou");
        areas.add("lishui");
        areas.add("jinhua");
        areas.add("zhoushan");
        for (String area : areas){
            areaList.add("asset_"+area);
            areaList.add("other_"+area);
        }
        String[] indexs = areaList.toArray(new String[areaList.size()]);
        return indexs;
    }



    public static String getAreaName(String index){
        String name = "";
        switch (index){
            case "asset_quzhou":
                name = "衢州";
                break;
            case "asset_zhoushan":
                name = "舟山";
                break;
            case "asset_huzhou":
                name = "湖州";
                break;
            case "asset_shaoxing":
                name = "绍兴";
                break;
            case "asset_ningbo":
                name = "宁波";
                break;
            case "asset_wenzhou":
                name = "温州";
                break;
            case "asset_taizhou":
                name = "台州";
                break;
            case "asset_jinhua":
                name = "金华";
                break;
            case "asset_lishui":
                name = "丽水";
                break;
            case "asset_jiaxing":
                name = "嘉兴";
                break;
            case "asset_hangzhou":
                name = "杭州";
                break;
        }
        return name;
    }

}
