package com.zjtelcom.cpct.statistic.service.impl;

import com.ctg.mq.api.CTGMQFactory;
import com.ctg.mq.api.IMQPushConsumer;
import com.ctg.mq.api.PropertyKeyConst;
import com.ctg.mq.api.bean.MQResult;
import com.ctg.mq.api.exception.MQException;
import com.ctg.mq.api.listener.ConsumerTopicListener;
import com.ctg.mq.api.listener.ConsumerTopicStatus;
import com.zjtelcom.cpct.statistic.service.MqLabelService;
import com.zjtelcom.cpct.statistic.service.TrialLabelService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

@Service
public class MqLabelServiceImpl implements MqLabelService, InitializingBean, DisposableBean {

    protected org.slf4j.Logger logger = LoggerFactory.getLogger(MqLabelServiceImpl.class);

    @Autowired
    private TrialLabelService trialLabelService;

    //地址
    @Value("${ctg.namesrvAddr}")
    private String namesrvAddr;
    //账号
    @Value("${ctg.namesrvAuthID}")
    private String namesrvAuthID;
    //密码
    @Value("${ctg.namesrvAuthPwd}")
    private String namesrvAuthPwd;
    //topic
    @Value("${ctg.topic}")
    private String topic;
    //消费组
    @Value("${ctg.consumerGroupName}")
    private String consumerGroupName;
    //客户端订阅broker的集群名，根据实际情况设置
    @Value("${ctg.clusterName}")
    private String clusterName;
    //租户ID，根据实际情况设置
    @Value("${ctg.tenantID}")
    private String tenantID;
    //发送超时时间
    @Value("${ctg.sendMsgTimeout}")
    private String sendMsgTimeout;
    //消费线程数
    @Value("${ctg.clientWorkerThreads}")
    private String clientWorkerThreads;

    private int labelStatisiticConnect;
    private IMQPushConsumer labelStatisiticConsumer;

    @Override
    public void afterPropertiesSet() throws Exception {
        initLabelStatisticConsumer();
        logger.info("试算清单数据消费者初始化成功！");
    }

    /**
     * 试算用户标签数据
     */
    @Override
    public String trialUserLabelConsumer() {
        if (labelStatisiticConnect != 0) {
            return "error";
        }
        try {
            labelStatisiticConsumer.listenTopic(topic, null, new ConsumerTopicListener(){
                @Override
                public ConsumerTopicStatus onMessage(List<MQResult> mqResultList) {
                    logger.info("labelStatistic pushConsumer start ~~~ ");
                    try {
                        for (MQResult result : mqResultList) {
                            String s = new String(result.getMessage().getBody());
                            trialLabelService.trialUerLabelLog(s, result.getMessage().getMessageID(), result.getMessage().getKey());
                        }
                        return ConsumerTopicStatus.CONSUME_SUCCESS;
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.warn("消息签收失败"+e.getMessage());
                        return ConsumerTopicStatus.CONSUME_SUCCESS;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("push consumer 出现异常:" + e);
        }
        return "ok";
    }

    @Override
    public void destroy() throws Exception {
        if (labelStatisiticConsumer != null) labelStatisiticConsumer.close();
    }

    //MQ链接
    public void initLabelStatisticConsumer() throws Exception {
        logger.info("initLabelStatisticConsumer...init...." + namesrvAddr);
        Properties properties = new Properties();
        labelStatisiticConsumer = CTGMQFactory.createPushConsumer(properties);
        properties.setProperty(PropertyKeyConst.ConsumerGroupName, consumerGroupName);
        properties.setProperty(PropertyKeyConst.NamesrvAddr, namesrvAddr);
        properties.setProperty(PropertyKeyConst.NamesrvAuthID, namesrvAuthID);
        properties.setProperty(PropertyKeyConst.NamesrvAuthPwd, namesrvAuthPwd);
        properties.setProperty(PropertyKeyConst.SendMsgTimeout, sendMsgTimeout);
        properties.setProperty(PropertyKeyConst.ClusterName, clusterName);
        properties.setProperty(PropertyKeyConst.TenantID, tenantID);
        properties.setProperty(PropertyKeyConst.ClientWorkerThreads, clientWorkerThreads);
        labelStatisiticConnect = labelStatisiticConsumer.connect();
        logger.info("esLogConnect" + labelStatisiticConnect);
    }
}
