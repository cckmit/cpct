package com.zjtelcom.cpct.service.channel;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.dto.ChannelAddVO;
import com.zjtelcom.cpct.dto.ChannelEditVO;
import com.zjtelcom.cpct.dto.ChannelVO;

import java.util.List;


/**
 * @Description ChannelService
 * @Author hyf
 * @Date 2018/06/21
 */
public interface ChannelService {

    RespInfo  addChannel(Long userId, ChannelAddVO addVO);

    RespInfo editChannel(Long userId, ChannelEditVO editVO);

    RespInfo deleteChannel(Long userId,Long channelId);

    List<ChannelVO> getChannelList(Long userId,String channelName,Integer page,Integer pageSize);

    ChannelVO getChannelDetail(Long userId,Long channelId);






}
