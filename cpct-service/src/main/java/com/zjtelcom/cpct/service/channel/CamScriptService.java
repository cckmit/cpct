package com.zjtelcom.cpct.service.channel;

import com.zjtelcom.cpct.dto.channel.CamScriptAddVO;
import com.zjtelcom.cpct.dto.channel.CamScriptEditVO;

import java.util.Map;

public interface CamScriptService {
    Map<String,Object> addCamScript(Long userId, CamScriptAddVO addVO);

    Map<String,Object> editCamScript(Long userId, CamScriptEditVO editVO);

    Map<String,Object> deleteCamScript(Long userId,Long camScriptId);

    Map<String,Object> getCamScriptList(Long userId, Long evtContactConfId );

    Map<String,Object> getCamScriptVODetail(Long userId,Long camScriptId);

    Map<String,Object> copyCamScript(Long contactConfId,String scriptDesc, Long newConfId);

}
