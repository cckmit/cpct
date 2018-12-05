package com.zjtelcom.cpct.dubbo.model;

import com.zjtelcom.cpct.domain.channel.CamScript;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConf;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfAttr;

import java.util.ArrayList;

public class MktCamChlConfDetail extends MktCamChlConf{

    // 是否二次协同(1：是 0：否)
    private String isSecondCoop;
    /**
     * 推送渠道编码
     */
    private String contactChlCode;

    private ArrayList<MktCamChlConfAttr> mktCamChlConfAttrList;

    private CamScript camScript;


    public String getIsSecondCoop() {
        return isSecondCoop;
    }

    public void setIsSecondCoop(String isSecondCoop) {
        this.isSecondCoop = isSecondCoop;
    }

    public String getContactChlCode() {
        return contactChlCode;
    }

    public void setContactChlCode(String contactChlCode) {
        this.contactChlCode = contactChlCode;
    }

    public ArrayList<MktCamChlConfAttr> getMktCamChlConfAttrList() {
        return mktCamChlConfAttrList;
    }

    public void setMktCamChlConfAttrList(ArrayList<MktCamChlConfAttr> mktCamChlConfAttrList) {
        this.mktCamChlConfAttrList = mktCamChlConfAttrList;
    }

    public CamScript getCamScript() {
        return camScript;
    }

    public void setCamScript(CamScript camScript) {
        this.camScript = camScript;
    }
}
