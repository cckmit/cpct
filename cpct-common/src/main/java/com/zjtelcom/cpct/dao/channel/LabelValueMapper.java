package com.zjtelcom.cpct.dao.channel;



import com.zjtelcom.cpct.domain.channel.TopicLabelValue;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabelValueMapper {
    int deleteByPrimaryKey(Long labelValueId);

    int insert(TopicLabelValue record);

    TopicLabelValue selectByPrimaryKey(Long labelValueId);

    List<TopicLabelValue> selectAll();

    int updateByPrimaryKey(TopicLabelValue record);

    List<TopicLabelValue> selectByLabelId(@Param("labelId")Long labelId);

    TopicLabelValue selectByValue(@Param("value")String value);
}