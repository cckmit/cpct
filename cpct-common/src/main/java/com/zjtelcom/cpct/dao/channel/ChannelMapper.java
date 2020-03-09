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
}