package com.zjtelcom.cpct.response.event;

import com.zjtelcom.cpct.dto.event.ContactEventDetail;
import java.io.Serializable;

/**
 * @Description 事件场景返回实体类
 * @Author pengy
 * @Date 2018/6/26 13:45
 */
public class ViewContactEvtRsp implements Serializable{

    private static final long serialVersionUID = -2584042603507583666L;
    private ContactEventDetail contactEvtDetail;

    public ContactEventDetail getContactEvtDetail() {
        return contactEvtDetail;
    }

    public void setContactEvtDetail(ContactEventDetail contactEvtDetail) {
        this.contactEvtDetail = contactEvtDetail;
    }
}
