package com.zjtelcom.cpct.dao.channel;

import com.zjtelcom.cpct.domain.channel.Channel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ChannelMapper {
    int deleteByPrimaryKey(Long orgId);

    int insert(Channel record);

    Channel selectByPrimaryKey(Long orgId);

    List<Channel> selectAll();

    int updateByPrimaryKey(Channel record);

    Channel selectByPrimaryKeyFromShiTi(Long aLong);

    //regionId与regionName模糊匹配
    List<String> getChannelInfoByRegionId(Long regionId,String channelName);
}