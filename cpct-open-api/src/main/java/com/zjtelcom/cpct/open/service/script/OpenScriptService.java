package com.zjtelcom.cpct.open.service.script;

import com.zjtelcom.cpct.dto.channel.MktScript;
import com.zjtelcom.cpct.dto.channel.QryMktScriptReq;

import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/10/30
 * @Description:
 */
public interface OpenScriptService {

    Map<String,Object> createMktScript(Long userId, MktScript addVO);

    Map<String,Object> modMktScript(Long userId,MktScript editVO);

    Map<String,Object> delMktScript (Long scriptId);

    Map<String,Object> qryMktScriptList (Long userId,QryMktScriptReq params );

    Map<String,Object> getScriptVODetail(Long userId,Long scriptId);

    Map<String,Object> selectScriptList(Map<String,Object> params);

    Map<String,Object> selectByPrimaryKey(Long scriptId);
}
