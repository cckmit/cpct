package com.zjtelcom.cpct.dto.campaign;

import com.zjtelcom.cpct.domain.channel.CamScript;
import com.zjtelcom.cpct.dto.channel.VerbalVO;

import java.util.List;

public class MktCamChlConfDetail extends MktCamChlConf{

/*
    private List<MktCamScript> mktCamScriptList;

    private List<MktCamQuest> mktCamQuestList;
*/

    private List<MktCamChlConfAttr> mktCamChlConfAttrList;

    private List<VerbalVO> verbalVOList;

    private CamScript camScript;

    public List<MktCamChlConfAttr> getMktCamChlConfAttrList() {
        return mktCamChlConfAttrList;
    }

    public void setMktCamChlConfAttrList(List<MktCamChlConfAttr> mktCamChlConfAttrList) {
        this.mktCamChlConfAttrList = mktCamChlConfAttrList;
    }

    public List<VerbalVO> getVerbalVOList() {
        return verbalVOList;
    }

    public void setVerbalVOList(List<VerbalVO> verbalVOList) {
        this.verbalVOList = verbalVOList;
    }

    public CamScript getCamScript() {
        return camScript;
    }

    public void setCamScript(CamScript camScript) {
        this.camScript = camScript;
    }
}
