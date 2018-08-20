package com.zjtelcom.cpct.elastic.config;

public class ESConfig {
    private String esIndex;          // ES库
    private String esType;           // ES表
    private int batchSize;           // 批量导入大小
    private String filePath;         // 导入文件的路径
    private int esThreadNum;         // 导入到ES的并发数量
    private String localClientIP;    // 本机IP地址


    public String getEsIndex() {
        return esIndex;
    }

    public ESConfig setEsIndex(String esIndex) {
        this.esIndex = esIndex;
        return this;
    }

    public String getEsType() {
        return esType;
    }

    public ESConfig setEsType(String esType) {
        this.esType = esType;
        return this;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public ESConfig setBatchSize(int batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    public String getFilePath() {
        return filePath;
    }

    public ESConfig setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public int getEsThreadNum() {
        return esThreadNum;
    }

    public ESConfig setEsThreadNum(int esThreadNum) {
        this.esThreadNum = esThreadNum;
        return this;
    }

    public String getLocalClientIP() {
        return localClientIP;
    }

    public ESConfig setLocalClientIP(String localClientIP) {
        this.localClientIP = localClientIP;
        return this;
    }
}
