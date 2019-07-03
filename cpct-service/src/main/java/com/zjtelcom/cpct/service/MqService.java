package com.zjtelcom.cpct.service;


public interface MqService {

     void initProducer();

     void initConsumer();

     String msg2Producer(Object msgBody, String key, String tag);

}
