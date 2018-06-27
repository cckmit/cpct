package com.zjtelcom.cpct.util;

import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.dto.channel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class ChannelUtil  {

    public static ChannelVO map2ChannelVO(Channel channel){
        ChannelVO vo = BeanUtil.create(channel,new ChannelVO());
        return vo;
    }

    public static ScriptVO map2ScriptVO(Script script){
        ScriptVO vo = BeanUtil.create(script,new ScriptVO());
        return vo;
    }

    public static CamScriptVO map2CamScriptVO(CamScript script){
        CamScriptVO vo = BeanUtil.create(script,new CamScriptVO());
        return vo;
    }
    public static LabelVO map2LabelVO(Label label){
        LabelVO vo = BeanUtil.create(label,new LabelVO());
        return vo;
    }

    public static LabelValueVO map2LabelValueVO(LabelValue labelValue){
        LabelValueVO vo = BeanUtil.create(labelValue,new LabelValueVO());
        return vo;
    }

    public static VerbalVO map2VerbalVO(MktVerbal verbal){
        VerbalVO vo = BeanUtil.create(verbal,new VerbalVO());
        return vo;
    }

    public static String getRandomStr(int length) {
        char[] chars = "23456789ABCDEFGHJKMNPQRSTUVWXYZ".toCharArray();
        Random r = new Random(System.currentTimeMillis());
        String string = "";

        for(int i = 0; i < length; ++i) {
            string = string + chars[r.nextInt(31)];
        }

        return string;
    }

    public static String getDataType(String dataType){
        //1000	日期型;1100	日期时间型;1200	字符型;1300	浮点型;1400	整数型;1500	布尔型;1600	计算型
        String data = "";
        switch (dataType){
            case "text":
                data = "1200";
                break;
            case "number":
                data = "1400";
                break;
            case "date":
                data = "1100";
                break;
            case "integer":
                data = "1400";
                break;
            case "character":
                data = "1200";
                break;
            case "smallint":
                data = "1600";
                break;
        }
        return data;
    }



}
