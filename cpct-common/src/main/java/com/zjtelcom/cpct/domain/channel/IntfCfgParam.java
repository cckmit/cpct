package com.zjtelcom.cpct.domain.channel;

import com.zjtelcom.cpct.BaseEntity;

import java.util.Date;

public class IntfCfgParam extends BaseEntity {
    private Long intfCfgParamId;

    private Long interfaceCfgId;

    private String url;

    private String port;

    private String userName;

    private String password;

    private Long timeout;

    private String publicKey;

    private Long regionId;



    public Long getIntfCfgParamId() {
        return intfCfgParamId;
    }

    public void setIntfCfgParamId(Long intfCfgParamId) {
        this.intfCfgParamId = intfCfgParamId;
    }

    public Long getInterfaceCfgId() {
        return interfaceCfgId;
    }

    public void setInterfaceCfgId(Long interfaceCfgId) {
        this.interfaceCfgId = interfaceCfgId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }


}