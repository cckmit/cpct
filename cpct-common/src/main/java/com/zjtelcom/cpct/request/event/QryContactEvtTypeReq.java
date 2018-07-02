package com.zjtelcom.cpct.request.event;

import java.io.Serializable;

/**
 * @Description 综合条件查询事件目录列表对象
 * @Author pengy
 * @Date 2018/7/1 15:22
 */
public class QryContactEvtTypeReq implements Serializable {

    private static final long serialVersionUID = 2053309898522694763L;
    private Long evtTypeId;//主键标识
    private String contactEvtTypeCode;//记录事件目录的编码
    private String contactEvtName;//记录事件目录的名称
    private Long parEvtTypeId;//记录父级的事件类型标识
    private String statusCd;//状态
//    private PageInfo pageInfo;//分页

    public Long getEvtTypeId() {
        return evtTypeId;
    }

    public void setEvtTypeId(Long evtTypeId) {
        this.evtTypeId = evtTypeId;
    }

    public String getContactEvtTypeCode() {
        return contactEvtTypeCode;
    }

    public void setContactEvtTypeCode(String contactEvtTypeCode) {
        this.contactEvtTypeCode = contactEvtTypeCode;
    }

    public String getContactEvtName() {
        return contactEvtName;
    }

    public void setContactEvtName(String contactEvtName) {
        this.contactEvtName = contactEvtName;
    }

    public Long getParEvtTypeId() {
        return parEvtTypeId;
    }

    public void setParEvtTypeId(Long parEvtTypeId) {
        this.parEvtTypeId = parEvtTypeId;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }
}
