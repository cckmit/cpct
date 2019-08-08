package com.zjtelcom.cpct.service;


public interface MqService {

     void initProducer();

     void initConsumer();

     String msg2Producer(Object msgBody, String topic,String key, String tag);

     //es添加日志队列中转
     String pushEsLogConsumer();

}
