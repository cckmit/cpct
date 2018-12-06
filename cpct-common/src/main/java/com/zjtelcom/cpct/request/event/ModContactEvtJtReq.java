package com.zjtelcom.cpct.request.event;

import com.zjtelcom.cpct.dto.apply.RequestTemplateInst;
import com.zjtelcom.cpct.dto.event.ContactEvtDetail;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 修改时间请求对象
 * @Author pengy
 * @Date 2018/7/1 14:01
 */
public class ModContactEvtJtReq implements Serializable{

    private static final long serialVersionUID = 9099062938745900386L;
    private List<ContactEvtDetail> ContactEvtDetails;
    private RequestTemplateInst requestTemplateInst;

    public List<ContactEvtDetail> getContactEvtDetails() {
        return ContactEvtDetails;
    }

    public void setContactEvtDetails(List<ContactEvtDetail> contactEvtDetails) {
        ContactEvtDetails = contactEvtDetails;
    }

    public RequestTemplateInst getRequestTemplateInst() {
        return requestTemplateInst;
    }

    public void setRequestTemplateInst(RequestTemplateInst requestTemplateInst) {
        this.requestTemplateInst = requestTemplateInst;
    }
}
