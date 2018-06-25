package com.zjtelcom.cpct.service.channel;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.domain.channel.Script;
import com.zjtelcom.cpct.dto.ScriptAddVO;
import com.zjtelcom.cpct.dto.ScriptEditVO;
import com.zjtelcom.cpct.dto.ScriptVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ScriptService {
    RespInfo addScript(Long userId, ScriptAddVO addVO);

    RespInfo editScript(Long userId, ScriptEditVO editVO);

    RespInfo deleteScript(Long userId,Long scriptId);

    List<ScriptVO> getScriptList(Long userId,Map<String,Object> params,Integer page,Integer pageSize);

    ScriptVO getScriptVODetail(Long userId,Long scriptId);






}
