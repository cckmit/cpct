package com.zjtelcom.cpct.dto.campaign;

import java.util.List;

public class MktCamChlConfDetail extends MktCamChlConf{

/*
    private List<MktCamScript> mktCamScriptList;

    private List<MktCamQuest> mktCamQuestList;
*/

    private List<MktCamChlConfAttr> mktCamChlConfAttrList;

    public List<MktCamChlConfAttr> getMktCamChlConfAttrList() {
        return mktCamChlConfAttrList;
    }

    public void setMktCamChlConfAttrList(List<MktCamChlConfAttr> mktCamChlConfAttrList) {
        this.mktCamChlConfAttrList = mktCamChlConfAttrList;
    }
}
