package com.zjtelcom.cpct.dubbo.model;

import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.ContactEvtItem;

import java.io.Serializable;
import java.util.List;

public class ContactEvtModel extends ContactEvt implements Serializable {

    private List<ContactEvtItem> contactEvtItems;

    public List<ContactEvtItem> getContactEvtItems() {
        return contactEvtItems;
    }

    public void setContactEvtItems(List<ContactEvtItem> contactEvtItems) {
        this.contactEvtItems = contactEvtItems;
    }
}
