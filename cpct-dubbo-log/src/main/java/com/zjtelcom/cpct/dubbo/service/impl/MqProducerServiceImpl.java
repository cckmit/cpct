package com.zjtelcom.cpct.dubbo.service.impl;

import com.alibaba.fastjson.JSON;
import com.ctg.mq.api.CTGMQFactory;
import com.ctg.mq.api.IMQProducer;
import com.ctg.mq.api.PropertyKeyConst;
import com.ctg.mq.api.bean.MQMessage;
import com.ctg.mq.api.bean.MQSendResult;
import com.ctg.mq.api.bean.MQSendStatus;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.dubbo.service.MqProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service
public class MqProducerServiceImpl implements MqProducerService, InitializingBean, DisposableBean {

    private Logger logger = LoggerFactory.getLogger(MqProducerServiceImpl.class);

    //地址
    @Value("${ctg.namesrvAddr}")
    private String namesrvAddr;
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
    @Value("${ctg.clientWorkerThreads}")
    private String clientWorkerThreads;
    //日志队列 中转添加es日志
    @Value("${ctg.cpctEsLogTopic}")
    private String cpctEsLogTopic;
    @Value("${ctg.cpctEsLogGroup}")
    private String cpctEsLogGroup;

    @Autowired
    private SysParamsMapper sysParamsMapper;


    private int esLogConnect;
    private IMQProducer esLogProducer;

    private void initESLogProducer() throws Exception {
        logger.info("initESLogProducer...init...." + namesrvAddr);
        Properties properties2 = new Properties();
        properties2.setProperty(PropertyKeyConst.ProducerGroupName, "groupName" + System.currentTimeMillis());
        properties2.setProperty(PropertyKeyConst.NamesrvAddr, namesrvAddr);
        properties2.setProperty(PropertyKeyConst.NamesrvAuthID, namesrvAuthID);
        properties2.setProperty(PropertyKeyConst.NamesrvAuthPwd, namesrvAuthPwd);
        properties2.setProperty(PropertyKeyConst.MaxMessageSize, maxMessageSize);
        properties2.setProperty(PropertyKeyConst.SendMaxRetryTimes, sendMaxRetryTimes);
        properties2.setProperty(PropertyKeyConst.SendMsgTimeout, sendMsgTimeout);//设置发送超时时间
        properties2.setProperty(PropertyKeyConst.CompressMsgBodyOverHowmuch, compressMsgBodyOverHowmuch);//消息体到达2k，自动压缩
        properties2.setProperty(PropertyKeyConst.ClusterName, clusterName);
        properties2.setProperty(PropertyKeyConst.TenantID, tenantID);
        properties2.setProperty(PropertyKeyConst.ClientWorkerThreads, clientWorkerThreads);
        esLogProducer = CTGMQFactory.createProducer(properties2);
        esLogConnect = esLogProducer.connect();
        logger.info("esLogConnect" + esLogConnect);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        long start = System.currentTimeMillis();
        initESLogProducer(); //标签日志
        long end = System.currentTimeMillis();
        logger.info("生产者实例化耗时:" + (end - start));
    }

    @Override
    public void destroy() throws Exception {
        if (esLogProducer != null) esLogProducer.close();
    }

    @Override
    public String msg2ESLogProducer(Object msgBody, String topic, String key, String tag) throws Exception {
        try {
            if (esLogConnect == 0 && msgBody != null) {
                String prodFilter = "0";
                List<Map<String, String>> sysFilList = sysParamsMapper.listParamsByKey("SYSYTEM_ESLOG_STATUS");
                if (sysFilList != null && !sysFilList.isEmpty()) {
                    prodFilter = sysFilList.get(0).get("value");
                }
                if (prodFilter.equals("1")) {
                    MQMessage message = new MQMessage(topic, key, tag, null);
                    message.setBody(JSON.toJSONString(msgBody).getBytes());
                    try {
                        MQSendResult send = esLogProducer.send(message);
                        String content = "topic:" + topic + " , messageId:" + send.getMessageID();
                        logger.info("content: " + content);
                        MQSendStatus sendStatus = send.getSendStatus();
                        System.out.println(send.getMessageID().getBytes());
                        if (sendStatus.toString().equals("SEND_OK")) {
                            System.out.println(send.getMessageID().toString());
                        }
                        return sendStatus.toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("pushProducer出现异常", e);
        }
        return "push error";
    }
}
