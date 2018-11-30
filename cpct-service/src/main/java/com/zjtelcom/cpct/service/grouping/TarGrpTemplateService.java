/**
 * @(#)TarGrpTemplateService.java, 2018/9/6.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.grouping;

import com.zjtelcom.cpct.dto.grouping.TarGrpTemplateDetail;

import java.util.Map;

/**
 * @Description:
 * @author: linchao
 * @date: 2018/09/06 16:14
 * @version: V1.0
 */
public interface TarGrpTemplateService {

    Map<String, Object> saveTarGrpTemplate(TarGrpTemplateDetail tarGrpTemplateDetail);

    Map<String, Object> getTarGrpTemplate(Long tarGrpTemplateId);

    Map<String, Object> updateTarGrpTemplate(TarGrpTemplateDetail tarGrpTemplateDetail);

    Map<String, Object> deleteTarGrpTemplate(Long tarGrpTemplateId);

    Map<String, Object> deleteTarGrpTemplateCondition(Long conditionId);

    Map<String, Object> listTarGrpTemplateAll();

    Map<String, Object> listTarGrpTemplatePage(String tarGrpTemplateName,String tarGrpType, Integer page, Integer pageSize);

    Map<String, Object> getTarGrpTemByOfferId(Long requestId);
}