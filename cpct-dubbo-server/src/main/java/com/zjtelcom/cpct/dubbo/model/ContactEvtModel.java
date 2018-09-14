package com.zjtelcom.cpct.dubbo.model;

import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.ContactEvtItem;

import java.io.Serializable;
import java.util.List;

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
    private List<ContactEvtItem> contactEvtItems;

    public List<ContactEvtItem> getContactEvtItems() {
        return contactEvtItems;
    }

    public void setContactEvtItems(List<ContactEvtItem> contactEvtItems) {
        this.contactEvtItems = contactEvtItems;
    }
}
