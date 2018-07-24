package com.zjtelcom.cpct.service.channel;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.dto.channel.CamScriptAddVO;
import com.zjtelcom.cpct.dto.channel.CamScriptEditVO;
import com.zjtelcom.cpct.dto.channel.CamScriptVO;

import java.util.List;
import java.util.Map;

public interface CamScriptService {
    Map<String,Object> addCamScript(Long userId, CamScriptAddVO addVO);

    Map<String,Object> editCamScript(Long userId, CamScriptEditVO editVO);

    Map<String,Object> deleteCamScript(Long userId,Long camScriptId);

    Map<String,Object> getCamScriptList(Long userId, Long evtContactConfId );

    Map<String,Object> getCamScriptVODetail(Long userId,Long camScriptId);

}
