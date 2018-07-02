package com.zjtelcom.cpct.request.event;

import com.zjtelcom.cpct.dto.apply.RequestTemplateInst;
import com.zjtelcom.cpct.dto.event.ContactEvtDetail;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 创建事件请求实体类
 * @Author pengy
 * @Date 2018/6/26 13:45
 */
public class CreateContactEvtJtReq implements Serializable{

    private static final long serialVersionUID = -2584042603507583666L;
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
