package com.zjtelcom.cpct.elastic.service;

public interface MqProducerService {

    String msg2ESLogProducer(Object msgBody, String topic, String key, String tag) throws Exception;
}
