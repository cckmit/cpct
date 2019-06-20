package com.zjtelcom.cpct.service;

import com.alibaba.fastjson.JSON;
import com.ctg.mq.api.CTGMQFactory;
import com.ctg.mq.api.IMQProducer;
import com.ctg.mq.api.PropertyKeyConst;
import com.ctg.mq.api.bean.MQMessage;
import com.ctg.mq.api.bean.MQSendResult;
import com.ctg.mq.api.bean.MQSendStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class MqService {

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

    private int connect;

    private IMQProducer producer;

    private void initProducer() throws Exception {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.ProducerGroupName, producerGroupName);
        properties.setProperty(PropertyKeyConst.NamesrvAddr, namesrvAddr);
        properties.setProperty(PropertyKeyConst.NamesrvAuthID,namesrvAuthID);
        properties.setProperty(PropertyKeyConst.NamesrvAuthPwd, namesrvAuthPwd);
        properties.setProperty(PropertyKeyConst.MaxMessageSize,maxMessageSize);
        properties.setProperty(PropertyKeyConst.SendMaxRetryTimes, sendMaxRetryTimes);
        properties.setProperty(PropertyKeyConst.SendMsgTimeout, sendMsgTimeout);//设置发送超时时间
        properties.setProperty(PropertyKeyConst.CompressMsgBodyOverHowmuch, compressMsgBodyOverHowmuch);//消息体到达2k，自动压缩
        properties.setProperty(PropertyKeyConst.ClusterName, clusterName);
        properties.setProperty(PropertyKeyConst.TenantID, tenantID);
        producer = CTGMQFactory.createProducer(properties);
        connect = producer.connect();
    }


    public  String msg2Producer(Object msgBody, String key, String tag) throws Exception {
        try {
            if (connect == 0 && msgBody != null) {
                MQMessage message = new MQMessage(topic, key, tag, null);
                message.setBody(JSON.toJSONString(msgBody).getBytes());
                try {
                    MQSendResult send = producer.send(message);
                    String content = "topic:"+topic+" , messageId:" + send.getMessageID();
                    System.out.println("send内容:"+send);
                    MQSendStatus sendStatus = send.getSendStatus();
                    System.out.println(send.getMessageID().getBytes());
                    if (sendStatus.toString().equals("SEND_OK")){
                        System.out.println(send.getMessageID());
                    }
                    return sendStatus.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }
}
