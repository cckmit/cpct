package com.zjtelcom.cpct.service.channel;

import com.zjtelcom.cpct.dto.channel.MktScript;
import com.zjtelcom.cpct.dto.channel.QryMktScriptReq;

import java.util.Map;

public interface ScriptService {
    Map<String,Object> createMktScript(Long userId, MktScript addVO);

    Map<String,Object> modMktScript(Long userId,MktScript editVO);

    Map<String,Object> delMktScript (Long userId,MktScript script);

    Map<String,Object> qryMktScriptList (Long userId,QryMktScriptReq params );

    Map<String,Object> getScriptVODetail(Long userId,Long scriptId);

    Map<String,Object> getScriptList(Long userId,String scriptName);






}
