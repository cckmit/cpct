package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.TopicLabel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface TopicLabelMapper {
    int deleteByPrimaryKey(Long labelId);

    int insert(TopicLabel record);

    TopicLabel selectByPrimaryKey(Long labelId);

    List<TopicLabel> selectAll();

    int updateByPrimaryKey(TopicLabel record);

    TopicLabel selectByLabelCode(@Param("labelCode") String labelCode);

    List<TopicLabel> selectByCampaignType();
}