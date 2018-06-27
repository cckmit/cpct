package com.zjtelcom.cpct.request.event;

import com.zjtelcom.cpct.dto.apply.RequestTemplateInst;
import com.zjtelcom.cpct.dto.event.ContactEvtDetail;
import lombok.Data;

import java.util.List;

/**
 * @Description 创建事件请求实体类
 * @Author pengy
 * @Date 2018/6/26 13:45
 */
@Data
public class CreateContactEvtJtReq {

    private List<ContactEvtDetail> ContactEvtDetails;

    private RequestTemplateInst requestTemplateInst;

}
