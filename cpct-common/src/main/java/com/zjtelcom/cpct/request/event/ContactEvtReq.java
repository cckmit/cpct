package com.zjtelcom.cpct.request.event;

import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.dto.event.ContactEvt;

import java.io.Serializable;

/**
 * @Description 事件前端请求req
 * @Author pengy
 * @Date 2018/7/3 11:24
 */
public class ContactEvtReq implements Serializable{

    private static final long serialVersionUID = 8558652959060897767L;
    private ContactEvt contactEvt;
    private Page page;

    public ContactEvt getContactEvt() {
        return contactEvt;
    }

    public void setContactEvt(ContactEvt contactEvt) {
        this.contactEvt = contactEvt;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
