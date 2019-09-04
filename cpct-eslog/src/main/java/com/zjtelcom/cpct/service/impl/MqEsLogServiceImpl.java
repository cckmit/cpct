package com.zjtelcom.cpct.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ctg.mq.api.CTGMQFactory;
import com.ctg.mq.api.IMQPushConsumer;
import com.ctg.mq.api.PropertyKeyConst;
import com.ctg.mq.api.bean.MQResult;
import com.ctg.mq.api.exception.MQException;
import com.ctg.mq.api.listener.ConsumerTopicListener;
import com.ctg.mq.api.listener.ConsumerTopicStatus;
import com.zjtelcom.cpct.service.MqEsLogService;
import com.zjtelcom.cpct.util.ElasticsearchUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

@Service
public class MqEsLogServiceImpl implements MqEsLogService, InitializingBean, DisposableBean {

    protected org.slf4j.Logger logger = LoggerFactory.getLogger(MqEsLogServiceImpl.class);
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
    //日志队列 中转添加es日志
    @Value("${ctg.cpctEsLogTopic}")
    private String cpctEsLogTopic;
    @Value("${ctg.cpctEsLogGroup}")
    private String cpctEsLogGroup;

    private IMQPushConsumer pushConsumer;
    private int consumerConnect;
    private int esLogConnect;
    private IMQPushConsumer esLogConsumer;


    //日志中转队列消费者
    private void initESLogConsumer() throws Exception {
        logger.info("initESLogConsumer...init...." + namesrvAddr);
        Properties properties3 = new Properties();
        properties3.setProperty(PropertyKeyConst.ConsumerGroupName, cpctEsLogGroup);
        properties3.setProperty(PropertyKeyConst.NamesrvAddr, namesrvAddr);
        properties3.setProperty(PropertyKeyConst.NamesrvAuthID, namesrvAuthID);
        properties3.setProperty(PropertyKeyConst.NamesrvAuthPwd, namesrvAuthPwd);
        properties3.setProperty(PropertyKeyConst.SendMsgTimeout, sendMsgTimeout);//设置发送超时时间
        properties3.setProperty(PropertyKeyConst.ClusterName, clusterName);
        properties3.setProperty(PropertyKeyConst.TenantID, tenantID);
        properties3.setProperty(PropertyKeyConst.ClientWorkerThreads, clientWorkerThreads);
        esLogConsumer = CTGMQFactory.createPushConsumer(properties3);
        esLogConnect = esLogConsumer.connect();
        logger.info("esLogConnect" + esLogConnect);
    }

    //日志队列 中转日志插入es 消费者
    @Override
    public String pushEsLogConsumer() {
        if (esLogConnect != 0) {
            return "error";
        }
        try {
            esLogConsumer.listenTopic(cpctEsLogTopic, null, new ConsumerTopicListener() {
                @Override
                public ConsumerTopicStatus onMessage(List<MQResult> mqResultList) {
                    try {
                        for (MQResult result : mqResultList) {
                            String esLogResult = new String(result.getMessage().getBody());
                            String[] split = result.getMessage().getKey().split(",");
                            String index = null;
                            String esType = "doc";
                            String id = null;
                            if (split.length == 2){
                                index = split[0];
                                String s = ElasticsearchUtil.addData(JSONObject.parseObject(esLogResult), index, esType);
                            }else if (split.length == 3){
                                index = split[0];
                                id = split[2];
                                String s = ElasticsearchUtil.addData(JSONObject.parseObject(esLogResult), index, esType,id);
                            }

                            //logger.info("下拉成功转添加esLog日志 索引名称："+index+",id ；" + id);
                        }
                        return ConsumerTopicStatus.CONSUME_SUCCESS;
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.warn("消息签收失败"+e.getMessage());
                        return ConsumerTopicStatus.CONSUME_SUCCESS;
                    }
                }
            });
        } catch (MQException e) {
            e.printStackTrace();
            System.out.println("push consumer 出现异常" + e);
        }
        return "ok";
    }

    @Override
    public void destroy() throws Exception {
        if (esLogConsumer != null) esLogConsumer.close();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            initESLogConsumer();
            logger.info("MqServiceImpl->initESLogConsumer:MQ日志中转队列消费者初始化成功！");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
