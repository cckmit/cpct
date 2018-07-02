package com.zjtelcom.cpct.service.channel;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.dto.channel.ScriptAddVO;
import com.zjtelcom.cpct.dto.channel.ScriptEditVO;
import com.zjtelcom.cpct.dto.channel.ScriptVO;

import java.util.List;
import java.util.Map;

public interface ScriptService {
    Map<String,Object> addScript(Long userId, ScriptAddVO addVO);

    Map<String,Object> editScript(Long userId, ScriptEditVO editVO);

    Map<String,Object> deleteScript(Long userId,Long scriptId);

    Map<String,Object> getScriptList(Long userId,Map<String,Object> params,Integer page,Integer pageSize);

    Map<String,Object> getScriptVODetail(Long userId,Long scriptId);






}
