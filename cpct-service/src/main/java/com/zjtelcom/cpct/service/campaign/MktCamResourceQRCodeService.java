package com.zjtelcom.cpct.service.campaign;

import com.jcraft.jsch.SftpException;

import java.util.Map;

public interface MktCamResourceQRCodeService {
    //海报获取二维码元素 参数：规则id,渠道id
    Map<String,Object> generatePoster(Map<String,Object> params) throws Exception;

    //保存海报url到二维码综合表中
    Map<String,Object> savePostUrl(Map<String,Object> params) throws Exception;

    //保存海报背景图到二维码综合表中
    Map<String,Object> savePostBackgroundUrl(Map<String,Object> params) throws Exception;

    //海报分页查询接口
    Map<String,Object> getPostgroundPathPage(Map<String,Object> params) throws SftpException;
    //根据规则获取获取海报
    Map<String,Object> getPostUrlByRuleId(Map<String,Object> params) throws SftpException;


}
