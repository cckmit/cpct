package com.zjtelcom.cpct.service.synchronize.channel;

import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/9/14
 * @Description:渠道同步
 */
public interface SynChannelService {

    Map<String,Object> synchronizeSingleChannel(Long channelId, String roleName);

    Map<String,Object> synchronizeBatchChannel(String roleName);

    Map<String,Object> deleteSingleChannel(Long channelId, String roleName);

}
