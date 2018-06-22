package com.zjtelcom.cpct.util;

import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.dto.ChannelVO;

public class ChannelUtil  {

    public static ChannelVO map2VO(Channel channel){
        ChannelVO vo = BeanUtil.create(channel,new ChannelVO());
        return vo;
    }

}
