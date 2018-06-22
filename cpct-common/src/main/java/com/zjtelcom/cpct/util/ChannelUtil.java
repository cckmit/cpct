package com.zjtelcom.cpct.util;

import com.sun.org.apache.bcel.internal.generic.RET;
import com.zjtelcom.cpct.domain.Channel;
import com.zjtelcom.cpct.dto.ChannelVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelUtil  {

    public static ChannelVO map2VO(Channel channel){
        ChannelVO vo = BeanUtil.create(channel,new ChannelVO());
        return vo;
    }

}
