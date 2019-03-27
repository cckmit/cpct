package com.zjtelcom.cpct.dubbo.model;

import com.zjtelcom.cpct.dto.event.ContactEvtItem;

import java.io.Serializable;
import java.util.ArrayList;

public class ContactEvtModel  implements Serializable {

    private Long contactEvtId; //事件主键标识
    private Long interfaceCfgId;//接口配置标识，主键标识
    private String contactEvtCode;//记录事件的编码信息
    private String contactEvtName;//记录事件的名称
    private String evtMappedAddr;//记录事件的映射地址，事件识别时可通过这个映身地址来适配触点事件，可以是URL地址，APP的类包名或其它识别编码
    private String evtMappedIp;//记录事件的映射匹配IP地址，事件识别可通过匹配IP地址进行匹配触点事件
    private String evtProcotolType;//记录接口协议类型,1000	HTTP 2000FTP
    private String evtMappedFunName;//事件匹配映射的方法名，用于事件识别
    private String contactEvtDesc;//记录事件的描述说明
    private String mktCampaignType;//事件分类
    private String recCampaignAmount;//推荐活动数量
    private Long contactChlId;//记录触发事件的触点渠道标识
    private Long contactEvtTypeId;//记录事件的所属事件类型标识
    private String contactEvtTypeName;//记录事件的所属事件类型名称
    private String evtTrigType;//记录事件的触发类型,1000实时触发事件 2000定期触发事件 3000人工触发事件
    private String coopType;//记录推送渠道协同类型，
    private Long extEventId;//记录集团下发的事件标识
    private ArrayList<ContactEvtItem> contactEvtItems;

    public Long getContactEvtId() {
        return contactEvtId;
    }

    public void setContactEvtId(Long contactEvtId) {
        this.contactEvtId = contactEvtId;
    }

    public Long getInterfaceCfgId() {
        return interfaceCfgId;
    }

    public void setInterfaceCfgId(Long interfaceCfgId) {
        this.interfaceCfgId = interfaceCfgId;
    }

    public String getContactEvtCode() {
        return contactEvtCode;
    }

    public void setContactEvtCode(String contactEvtCode) {
        this.contactEvtCode = contactEvtCode;
    }

    public String getContactEvtName() {
        return contactEvtName;
    }

    public void setContactEvtName(String contactEvtName) {
        this.contactEvtName = contactEvtName;
    }

    public String getEvtMappedAddr() {
        return evtMappedAddr;
    }

    public void setEvtMappedAddr(String evtMappedAddr) {
        this.evtMappedAddr = evtMappedAddr;
    }

    public String getEvtMappedIp() {
        return evtMappedIp;
    }

    public void setEvtMappedIp(String evtMappedIp) {
        this.evtMappedIp = evtMappedIp;
    }

    public String getEvtProcotolType() {
        return evtProcotolType;
    }

    public void setEvtProcotolType(String evtProcotolType) {
        this.evtProcotolType = evtProcotolType;
    }

    public String getEvtMappedFunName() {
        return evtMappedFunName;
    }

    public void setEvtMappedFunName(String evtMappedFunName) {
        this.evtMappedFunName = evtMappedFunName;
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

    public Long getContactChlId() {
        return contactChlId;
    }

    public void setContactChlId(Long contactChlId) {
        this.contactChlId = contactChlId;
    }

    public Long getContactEvtTypeId() {
        return contactEvtTypeId;
    }

    public void setContactEvtTypeId(Long contactEvtTypeId) {
        this.contactEvtTypeId = contactEvtTypeId;
    }

    public String getContactEvtTypeName() {
        return contactEvtTypeName;
    }

    public void setContactEvtTypeName(String contactEvtTypeName) {
        this.contactEvtTypeName = contactEvtTypeName;
    }

    public String getEvtTrigType() {
        return evtTrigType;
    }

    public void setEvtTrigType(String evtTrigType) {
        this.evtTrigType = evtTrigType;
    }

    public String getCoopType() {
        return coopType;
    }

    public void setCoopType(String coopType) {
        this.coopType = coopType;
    }

    public Long getExtEventId() {
        return extEventId;
    }

    public void setExtEventId(Long extEventId) {
        this.extEventId = extEventId;
    }

    public ArrayList<ContactEvtItem> getContactEvtItems() {
        return contactEvtItems;
    }

    public void setContactEvtItems(ArrayList<ContactEvtItem> contactEvtItems) {
        this.contactEvtItems = contactEvtItems;
    }
}
