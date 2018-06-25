package com.zjtelcom.cpct.util;

import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.dto.*;
import com.zjtelcom.cpct.dto.CamScriptVO;
import com.zjtelcom.cpct.dto.ChannelVO;
import com.zjtelcom.cpct.dto.LabelVO;
import com.zjtelcom.cpct.dto.LabelValueVO;

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
}
