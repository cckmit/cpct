package com.zjtelcom.cpct.dao.channel;

import com.zjtelcom.cpct.domain.channel.Channel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface ContactChannelMapper {
    int deleteByPrimaryKey(Long contactChlId);

    int insert(Channel record);

    Channel selectByPrimaryKey(Long contactChlId);

    List<Channel> selectAll();

    int updateByPrimaryKey(Channel record);
}