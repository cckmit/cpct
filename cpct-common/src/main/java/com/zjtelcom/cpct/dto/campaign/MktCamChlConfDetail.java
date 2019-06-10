package com.zjtelcom.cpct.dto.campaign;

import com.zjtelcom.cpct.domain.channel.CamScript;
import com.zjtelcom.cpct.dto.channel.VerbalVO;

import java.util.List;

public class MktCamChlConfDetail extends MktCamChlConf{

/*
    private List<MktCamScript> mktCamScriptList;

    private List<MktCamQuest> mktCamQuestList;
*/

    // 是否二次协同(1：是 0：否)
    private String isSecondCoop;
    /**
     * 推送渠道编码
     */
    private String contactChlCode;

    private List<MktCamChlConfAttr> mktCamChlConfAttrList;

    private List<VerbalVO> verbalVOList;

    private CamScript camScript;
    // iSee派单到人=1，派单到区域=2
    private String order0ption;
    // iSee派单到的详细值
    private String order0ptionValue;


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

    public String getContactChlCode() {
        return contactChlCode;
    }

    public void setContactChlCode(String contactChlCode) {
        this.contactChlCode = contactChlCode;
    }

    public String getIsSecondCoop() {
        return isSecondCoop;
    }

    public void setIsSecondCoop(String isSecondCoop) {
        this.isSecondCoop = isSecondCoop;
    }
}
