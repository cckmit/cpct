package com.zjtelcom.cpct.service.channel;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.dto.channel.ChannelAddVO;
import com.zjtelcom.cpct.dto.channel.ChannelEditVO;
import com.zjtelcom.cpct.dto.channel.ChannelVO;

import java.util.List;
import java.util.Map;


/**
 * @Description ChannelService
 * @Author hyf
 * @Date 2018/06/21
 */
public interface ChannelService {

    Map<String,Object> addChannel(Long userId, ChannelAddVO addVO);

    Map<String,Object> editChannel(Long userId, ChannelEditVO editVO);

    Map<String,Object> deleteChannel(Long userId,Long channelId);

    Map<String,Object> getChannelList(Long userId,String channelName,Integer page,Integer pageSize);

    Map<String,Object> getChannelDetail(Long userId,Long channelId);






}
