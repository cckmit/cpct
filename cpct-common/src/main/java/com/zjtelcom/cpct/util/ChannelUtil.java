package com.zjtelcom.cpct.util;

import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.dto.channel.*;
import com.zjtelcom.cpct.enums.Operator;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
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
        if (label.getOperator()!=null){
            List<String> opratorList = StringToList(label.getOperator());
            List<OperatorDetail> opStList  = new ArrayList<>();
            for (String operator : opratorList){
                Operator op = Operator.getOperator(Integer.valueOf(operator));
                OperatorDetail detail = new OperatorDetail();
                if (op!=null){
                    detail.setOperName(op.getDescription());
                    detail.setOperValue(op.getValue());
                }
                opStList.add(detail);
            }
            vo.setOperatorList(opStList);
        }
        List<String> valueList = new ArrayList<>();
        if (label.getRightOperand()!=null && !label.getRightOperand().equals("")){
            valueList = StringToList(label.getRightOperand());
        }
        vo.setValueList(valueList);
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

    public static List<String> StringToList(String var1) {
        String[] array = var1.split(",");
        List<String> list = new ArrayList<String>();
        for (String str : array)
        {
            list.add(str);
        }
        return list;
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
