package com.zjtelcom.cpct.dubbo.service;

public interface MqProducerService {

    String msg2ESLogProducer(Object msgBody,String topic, String key, String tag) throws Exception;
}
