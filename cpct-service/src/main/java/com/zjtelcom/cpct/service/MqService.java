package com.zjtelcom.cpct.service;


public interface MqService {

     void initProducer() throws Exception;

     String msg2Producer(Object msgBody, String key, String tag) throws Exception;
}
