package com.zjtelcom.cpct.service.channel;

import com.zjtelcom.cpct.dto.channel.ContactChannelDetail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Description ChannelService
 * @Author hyf
 * @Date 2018/06/21
 */
public interface ChannelService {

    Map<String,Object> listAllChildChannelList();

    Map<String,Object> listChannelByIdList(List<Long> idList);

    Map<String,Object> listChannelTree(Long userId,String channelName);

    Map<String,Object> getChannelTreeList(HashMap<String,String> param);

    Map<String,Object> getParentList(Long userId);

    Map<String,Object> getChannelListByParentId(Long userId,Long parentId);

    Map<String,Object> createParentChannel(Long userId,ContactChannelDetail parentAddVO);

    Map<String,Object> getChannelTreeForActivity(Long userId);

    Map<String,Object> createContactChannel (Long userId, ContactChannelDetail addVO);

    Map<String,Object> modContactChannel (Long userId, ContactChannelDetail editVO);

    Map<String,Object> delContactChannel (Long userId,ContactChannelDetail channelDetail);

    Map<String,Object> getChannelList(Long userId,String channelName,Integer page,Integer pageSize);

    Map<String,Object> getChannelDetail(Long userId,Long channelId);

    Map<String,Object> getChannelListByType(Long userId,String channelType);

    Object addAcount();





}
