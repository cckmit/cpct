package com.zjtelcom.cpct.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;

@Configuration
public class ElasticsearchConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchConfig.class);

    /**
     * elk集群地址
     */
    @Value("${elasticsearch.ip}")
    private String hostName;
    /**
     * 端口
     */
    @Value("${elasticsearch.port}")
    private String port;

    /**
     * 集群名称
     */
    @Value("${elasticsearch.cluster.name}")
    private String clusterName;

    /**
     * 连接池
     */
    @Value("${elasticsearch.pool}")
    private String poolSize;

    /**
     * es节点
     */
    @Value("${esnode.data1}")
    private String data1;
    @Value("${esnode.data2}")
    private String data2;
    @Value("${esnode.data3}")
    private String data3;

    @Bean(name = "transportClient")
    public TransportClient transportClient() {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        LOGGER.info("Elasticsearch初始化开始。。。。。"+hostName+clusterName);
        TransportClient transportClient = null;
        try {
            Settings settings = Settings.builder()
                    //.put("client.transport.sniff", true)
                    .put("client.transport.ignore_cluster_name", true)
                    .put("client.transport.ping_timeout","30s")
                    .put("client.transport.nodes_sampler_interval","10s")
                    //.put("index.mapping.total_fields.limit",4000)
                    .build();
            transportClient = new PreBuiltTransportClient(settings);
            TransportAddress transportAddress = new TransportAddress(InetAddress.getByName(hostName), Integer.valueOf(port));
            transportClient.addTransportAddresses(transportAddress);
            transportClient
                    .addTransportAddress(new TransportAddress(InetAddress.getByName(data2), Integer.valueOf(port)))
                    .addTransportAddress(new TransportAddress(InetAddress.getByName(data3), Integer.valueOf(port)));
        } catch (Exception e) {
            LOGGER.error("elasticsearch TransportClient create error!!", e);
        }
        return transportClient;
    }
}
