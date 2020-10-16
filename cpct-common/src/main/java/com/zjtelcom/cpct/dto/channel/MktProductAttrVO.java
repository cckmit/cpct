package com.zjtelcom.cpct.dto.channel;

import com.zjtelcom.cpct.domain.channel.MktProductAttr;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MktProductAttrVO  extends MktProductAttr  implements Serializable {

    private  List<Map<String,Object>> attrValueList;

    public List<Map<String, Object>> getAttrValueList() {
        return attrValueList;
    }

    public void setAttrValueList(List<Map<String, Object>> attrValueList) {
        this.attrValueList = attrValueList;
    }
}