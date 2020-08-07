package com.zjtelcom.cpct.dto.event;

import java.io.Serializable;

public class EventEditVO implements Serializable {

    private String contactEvtName;//记录事件的名称
    private String contactEvtDesc;//记录事件的描述说明
    private String mktCampaignType;//事件分类
    private String recCampaignAmount;//推荐活动数量
    private String evtTrigType;//记录事件的触发类型,1000实时触发事件 2000定期触发事件 3000人工触发事件
    private Long contactEvtTypeId;//记录事件的所属事件类型标识
    private Long interfaceCfgId;//接口配置标识，主键标识
    private String evtMappedIp;//记录事件的映射匹配IP地址，事件识别可通过匹配IP地址进行匹配触点事件
    private String evtMappedAddr;//记录事件的映射地址，事件识别时可通过这个映身地址来适配触点事件，可以是URL地址，APP的类包名或其它识别编码
    private String evtProcotolType;//记录接口协议类型,1000	HTTP 2000FTP
    private String locationNbr;//位置编号
    private String longitude;//经度
    private String latitude;//纬度
    private String locationSwitch;//位置切换开关
    private Long topCampaignNum;// 事件允许关联最大活动数
    private String remark;
    private String eventManageType;// 事件区分营销活动还是服务活动

    public String getEventManageType() {
        return eventManageType;
    }

    public void setEventManageType(String eventManageType) {
        this.eventManageType = eventManageType;
    }

    public Long getInterfaceCfgId() {
        return interfaceCfgId;
    }

    public void setInterfaceCfgId(Long interfaceCfgId) {
        this.interfaceCfgId = interfaceCfgId;
    }

    public String getEvtMappedIp() {
        return evtMappedIp;
    }

    public void setEvtMappedIp(String evtMappedIp) {
        this.evtMappedIp = evtMappedIp;
    }

    public String getEvtMappedAddr() {
        return evtMappedAddr;
    }

    public void setEvtMappedAddr(String evtMappedAddr) {
        this.evtMappedAddr = evtMappedAddr;
    }

    public String getEvtProcotolType() {
        return evtProcotolType;
    }

    public void setEvtProcotolType(String evtProcotolType) {
        this.evtProcotolType = evtProcotolType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getContactEvtName() {
        return contactEvtName;
    }

    public void setContactEvtName(String contactEvtName) {
        this.contactEvtName = contactEvtName;
    }

    public String getContactEvtDesc() {
        return contactEvtDesc;
    }

    public void setContactEvtDesc(String contactEvtDesc) {
        this.contactEvtDesc = contactEvtDesc;
    }

    public String getMktCampaignType() {
        return mktCampaignType;
    }

    public void setMktCampaignType(String mktCampaignType) {
        this.mktCampaignType = mktCampaignType;
    }

    public String getRecCampaignAmount() {
        return recCampaignAmount;
    }

    public void setRecCampaignAmount(String recCampaignAmount) {
        this.recCampaignAmount = recCampaignAmount;
    }

    public String getEvtTrigType() {
        return evtTrigType;
    }

    public void setEvtTrigType(String evtTrigType) {
        this.evtTrigType = evtTrigType;
    }

    public Long getContactEvtTypeId() {
        return contactEvtTypeId;
    }

    public void setContactEvtTypeId(Long contactEvtTypeId) {
        this.contactEvtTypeId = contactEvtTypeId;
    }

    public String getLocationNbr() {
        return locationNbr;
    }

    public void setLocationNbr(String locationNbr) {
        this.locationNbr = locationNbr;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLocationSwitch() {
        return locationSwitch;
    }

    public void setLocationSwitch(String locationSwitch) {
        this.locationSwitch = locationSwitch;
    }

    public Long getTopCampaignNum() {
        return topCampaignNum;
    }

    public void setTopCampaignNum(Long topCampaignNum) {
        this.topCampaignNum = topCampaignNum;
    }
}
