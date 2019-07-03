package com.zjtelcom.cpct.service.impl;

import com.alibaba.fastjson.JSON;
import com.ctg.mq.api.CTGMQFactory;
import com.ctg.mq.api.IMQProducer;
import com.ctg.mq.api.IMQPushConsumer;
import com.ctg.mq.api.PropertyKeyConst;
import com.ctg.mq.api.bean.MQMessage;
import com.ctg.mq.api.bean.MQResult;
import com.ctg.mq.api.bean.MQSendResult;
import com.ctg.mq.api.bean.MQSendStatus;
import com.ctg.mq.api.exception.MQException;
import com.ctg.mq.api.listener.ConsumerTopicListener;
import com.ctg.mq.api.listener.ConsumerTopicStatus;
import com.zjtelcom.cpct.dao.MqLogMapper;
import com.zjtelcom.cpct.dao.filter.CloseRuleMapper;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.MqService;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

@Service
public class MqServiceImpl implements MqService {

    protected org.slf4j.Logger logger = LoggerFactory.getLogger(MqServiceImpl.class);
    //地址
    @Value("${ctg.namesrvAddr}")
    private String namesrvAddr;
    //生产组组名称
    @Value("${ctg.cpctGroupName}")
    private String producerGroupName;
    //账号
    @Value("${ctg.namesrvAuthID}")
    private String namesrvAuthID;
    //密码
    @Value("${ctg.namesrvAuthPwd}")
    private String namesrvAuthPwd;
    //是否顺序消费
    @Value("${ctg.consumeOrderly}")
    private String consumeOrderly;
    //实例名
    @Value("${ctg.instanceName}")
    private String instanceName;
    //消息最大字节数，默认1024 * 128
    @Value("${ctg.maxMessageSize}")
    private String maxMessageSize;
    //发送失败默认重试次数
    @Value("${ctg.sendMaxRetryTimes}")
    private String sendMaxRetryTimes;
    //发送超时时间
    @Value("${ctg.sendMsgTimeout}")
    private String sendMsgTimeout;
    //消息体多大时开始压缩 设置2k
    @Value("${ctg.compressMsgBodyOverHowmuch}")
    private String compressMsgBodyOverHowmuch;
    //客户端订阅broker的集群名，根据实际情况设置
    @Value("${ctg.clusterName}")
    private String clusterName;
    //租户ID，根据实际情况设置
    @Value("${ctg.tenantID}")
    private String tenantID;
    @Value("${ctg.cpctTopic}")
    private String topic;
    @Value("${ctg.clientWorkerThreads}")
    private String clientWorkerThreads;

    @Autowired
    private MqLogMapper mqLogMapper;
    @Autowired
    private CloseRuleMapper closeRuleMapper;

    private IMQProducer producer;
    private IMQPushConsumer pushConsumer;
    private int producerConnect;
    private int consumerConnect;

    private Properties getProperties(){
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.ProducerGroupName, producerGroupName);
        properties.setProperty(PropertyKeyConst.NamesrvAddr, namesrvAddr);
        properties.setProperty(PropertyKeyConst.NamesrvAuthID,namesrvAuthID);
        properties.setProperty(PropertyKeyConst.NamesrvAuthPwd, namesrvAuthPwd);
        properties.setProperty(PropertyKeyConst.MaxMessageSize,maxMessageSize);
        properties.setProperty(PropertyKeyConst.SendMaxRetryTimes, sendMaxRetryTimes);
        properties.setProperty(PropertyKeyConst.SendMsgTimeout, sendMsgTimeout);//设置发送超时时间
        properties.setProperty(PropertyKeyConst.CompressMsgBodyOverHowmuch, compressMsgBodyOverHowmuch);//消息体到达2k，自动压缩
        properties.setProperty(PropertyKeyConst.ClientWorkerThreads, clientWorkerThreads);
        properties.setProperty(PropertyKeyConst.ClusterName, clusterName);
        properties.setProperty(PropertyKeyConst.TenantID, tenantID);
        return properties;
    }

    @Override
    public void initProducer() {
        try {
            Properties properties = getProperties();
            producer = CTGMQFactory.createProducer(properties);
            producerConnect = producer.connect();
            logger.info("MqServiceImpl->initProducer:MQ生产者初始化成功！");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void initConsumer() {
        try {
            Properties properties = getProperties();
            pushConsumer = CTGMQFactory.createPushConsumer(properties);
            consumerConnect = pushConsumer.connect();
            logger.info("MqServiceImpl->initConsumer:MQ消费者初始化成功！");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public String msg2Producer(Object msgBody, String key, String tag) {
        try {
            if (producerConnect == 0 && msgBody != null) {
                MQMessage message = new MQMessage(topic, key, tag, null);
                message.setBody(JSON.toJSONString(msgBody).getBytes());
                MQSendResult send = producer.send(message);
                MQSendStatus sendStatus = send.getSendStatus();
                if (sendStatus.toString().equals("SEND_OK")){
                    insertSendLog(send.getMessageID(), tag, key);
                    return "SEND_OK";
                }
            }else {
                logger.info("MqServiceImpl->msg2Producer:producerConnect连接异常！");
            }
            return "SEND_FAIL";
        } catch (Exception e) {
            e.printStackTrace();
            return "SEND_ERROR";
        }
    }

    private void insertSendLog(String msgId, String ruleId, String batchNum){
        if(mqLogMapper.insertSendLog2(msgId, ruleId, batchNum) != 1)
            logger.info("MqServiceImpl->insertSendLog:数据库记录插入失败！");
    }

}
