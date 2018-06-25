package com.zjtelcom.cpct.service.channel;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.dto.CamScriptAddVO;
import com.zjtelcom.cpct.dto.CamScriptEditVO;
import com.zjtelcom.cpct.dto.CamScriptVO;

import java.util.List;

public interface CamScriptService {
    RespInfo addCamScript(Long userId, CamScriptAddVO addVO);

    RespInfo editCamScript(Long userId, CamScriptEditVO editVO);

    RespInfo deleteCamScript(Long userId,List<Long> camScriptIdList);

    List<CamScriptVO> getCamScriptList(Long userId, Long campaignId, Long evtContactConfId );

    CamScriptVO getCamScriptVODetail(Long userId,Long camScriptId);

}
