package com.zjtelcom.cpct.dao.report;

import com.zjtelcom.cpct.domain.report.TopicDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktCamTopicMapper {

    int insertTopic(TopicDO topicDO);

    int updateTopic(TopicDO topicDO);

    int updateTopicState(TopicDO topicDO);

    int deleteTopicById(int id);

    TopicDO selectTopicInfoById(int id);

    List<TopicDO> selectAllTopicInfo();

    List<TopicDO> selectByKey(@Param("year") String year, @Param("season") String season, @Param("topicName") String topicName, @Param("topicCode") String topicCode);
}
