package com.zjtelcom.cpct.dto.event;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;
import java.util.List;

/**
 * @Description 事件详细信息
 * @Author pengy
 * @Date 2018/6/22 9:31
 */
@Data
public class ContactEvtDetail extends BaseEntity {

    private List<ContactEvtItem> contactEvtItems;
    private List<ContactEvtMatchRul> contactEvtMatchRuls;
    private List<ContactEvtTrigRul> contactEvtTrigRuls;
    private InterfaceCfgDetail interfaceCfgDetail;

}
