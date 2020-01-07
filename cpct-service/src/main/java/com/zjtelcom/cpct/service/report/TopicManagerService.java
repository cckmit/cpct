package com.zjtelcom.cpct.service.report;

import java.util.Map;

public interface TopicManagerService {
    Map<String,Object> addTopic(Map<String, Object> topicContent);

    Map<String,Object>  updateTopic(Map<String, Object> topicContent);

    Map<String,Object>  updateTopicState(Map<String, Object> topicState);

    Map<String,Object>  deleteTopic(int id);

    Map<String,Object> getTopicInfoById(int id);

    Map<String, Object> getAllTopic();

    Map<String,Object> getTopicPageLists(Map<String, Object> pageParams);
}

