package com.zjtelcom.cpct.service.channel;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.dto.*;

import java.util.List;
import java.util.Map;

public interface CamScriptService {
    RespInfo addCamScript(Long userId, CamScriptAddVO addVO);

    RespInfo editCamScript(Long userId, CamScriptEditVO editVO);

    RespInfo deleteCamScript(Long userId,List<Long> camScriptIdList);

    List<CamScriptVO> getCamScriptList(Long userId,Long campaignId,Long evtContactConfId );

    CamScriptVO getCamScriptVODetail(Long userId,Long camScriptId);

}
