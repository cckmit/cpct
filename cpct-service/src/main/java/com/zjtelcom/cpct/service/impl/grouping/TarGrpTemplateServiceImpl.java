/**
 * @(#)TarGrpTemplateServiceImpl.java, 2018/9/6.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.impl.grouping;

import com.github.pagehelper.PageHelper;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.grouping.TarGrpTemplateConditionMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpTemplateMapper;
import com.zjtelcom.cpct.domain.grouping.TarGrpTemplateConditionDO;
import com.zjtelcom.cpct.domain.grouping.TarGrpTemplateDO;
import com.zjtelcom.cpct.dto.grouping.TarGrpTemplateCondition;
import com.zjtelcom.cpct.dto.grouping.TarGrpTemplateDetail;
import com.zjtelcom.cpct.enums.LeftParamType;
import com.zjtelcom.cpct.enums.RightParamType;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.grouping.TarGrpTemplateService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description:
 * author: linchao
 * date: 2018/09/06 16:15
 * version: V1.0
 */
@Service
@Transactional
public class TarGrpTemplateServiceImpl extends BaseService implements TarGrpTemplateService {

    @Autowired
    private TarGrpTemplateMapper tarGrpTemplateMapper;

    @Autowired
    private TarGrpTemplateConditionMapper tarGrpTemplateConditionMapper;

    /**
     * 新增目标分群模板
     *
     * @param tarGrpTemplateDetail
     * @return
     */
    @Override
    public Map<String, Object> saveTarGrpTemplate(TarGrpTemplateDetail tarGrpTemplateDetail) {
        Map<String, Object> tarGrpTemplateMap = new HashMap<>();
        TarGrpTemplateDO tarGrpTemplateDO = BeanUtil.create(tarGrpTemplateDetail, new TarGrpTemplateDO());
        tarGrpTemplateDO.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
        tarGrpTemplateDO.setStatusDate(new Date());
        tarGrpTemplateDO.setCreateStaff(UserUtil.loginId());
        tarGrpTemplateDO.setCreateDate(new Date());
        tarGrpTemplateDO.setUpdateStaff(UserUtil.loginId());
        tarGrpTemplateDO.setUpdateDate(new Date());

        // 新增目标分群模板
        tarGrpTemplateMapper.insert(tarGrpTemplateDO);
        Long tarGrpTemplateId = tarGrpTemplateDO.getTarGrpTemplateId();
        // 新增目标分群模板条件
        if (tarGrpTemplateDetail.getTarGrpTemplateConditionList() != null && tarGrpTemplateDetail.getTarGrpTemplateConditionList().size() > 0) {
            for (TarGrpTemplateCondition tarGrpTemplateCondition : tarGrpTemplateDetail.getTarGrpTemplateConditionList()) {
                if (tarGrpTemplateCondition.getOperType() == null || tarGrpTemplateCondition.getOperType().equals("")) {
                    tarGrpTemplateMap.put("resultCode", CommonConstant.CODE_FAIL);
                    tarGrpTemplateMap.put("resultMsg", "请选择下拉框运算类型");
                    return tarGrpTemplateMap;
                }
                TarGrpTemplateConditionDO tarGrpTemplateConditionDO = BeanUtil.create(tarGrpTemplateCondition, new TarGrpTemplateConditionDO());
                tarGrpTemplateConditionDO.setLeftParamType(LeftParamType.LABEL.getErrorCode());//左参为注智标签
                tarGrpTemplateConditionDO.setRightParamType(RightParamType.FIX_VALUE.getErrorCode());//右参为固定值
                tarGrpTemplateConditionDO.setTarGrpTemplateId(tarGrpTemplateId);
                tarGrpTemplateConditionDO.setCreateDate(new Date());
                tarGrpTemplateConditionDO.setUpdateDate(new Date());
                tarGrpTemplateConditionDO.setStatusDate(new Date());
                tarGrpTemplateConditionDO.setUpdateStaff(UserUtil.loginId());
                tarGrpTemplateConditionDO.setCreateStaff(UserUtil.loginId());
                tarGrpTemplateConditionDO.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                tarGrpTemplateConditionMapper.insert(tarGrpTemplateConditionDO);
            }
        }
        tarGrpTemplateMap.put("resultCode", CommonConstant.CODE_SUCCESS);
        tarGrpTemplateMap.put("tarGrpTemplateId", tarGrpTemplateId);
        return tarGrpTemplateMap;
    }

    /**
     * 更新目标分群模板
     *
     * @param tarGrpTemplateDetail
     * @return
     */
    @Override
    public Map<String, Object> updateTarGrpTemplate(TarGrpTemplateDetail tarGrpTemplateDetail) {
        Map<String, Object> tarGrpTemplateMap = new HashMap<>();
        TarGrpTemplateDO tarGrpTemplateDO = BeanUtil.create(tarGrpTemplateDetail, new TarGrpTemplateDO());
        // 更新目标分群模板
        tarGrpTemplateMapper.updateByPrimaryKey(tarGrpTemplateDO);
        Long tarGrpTemplateId = tarGrpTemplateDetail.getTarGrpTemplateId();
        // 新增目标分群模板条件
        if (tarGrpTemplateDetail.getTarGrpTemplateConditionList() != null && tarGrpTemplateDetail.getTarGrpTemplateConditionList().size() > 0) {
            for (TarGrpTemplateCondition tarGrpTemplateCondition : tarGrpTemplateDetail.getTarGrpTemplateConditionList()) {
                if (tarGrpTemplateCondition.getOperType() == null || tarGrpTemplateCondition.getOperType().equals("")) {
                    tarGrpTemplateMap.put("resultCode", CommonConstant.CODE_FAIL);
                    tarGrpTemplateMap.put("resultMsg", "请选择下拉框运算类型");
                    return tarGrpTemplateMap;
                }
                TarGrpTemplateConditionDO tarGrpTemplateConditionDO = BeanUtil.create(tarGrpTemplateCondition, new TarGrpTemplateConditionDO());
                if (tarGrpTemplateCondition.getConditionId() != null) {
                    tarGrpTemplateConditionDO.setUpdateStaff(UserUtil.loginId());
                    tarGrpTemplateConditionDO.setUpdateDate(new Date());
                    tarGrpTemplateConditionMapper.updateByPrimaryKey(tarGrpTemplateConditionDO);
                } else {
                    tarGrpTemplateConditionDO.setLeftParamType(LeftParamType.LABEL.getErrorCode());//左参为注智标签
                    tarGrpTemplateConditionDO.setRightParamType(RightParamType.FIX_VALUE.getErrorCode());//右参为固定值
                    tarGrpTemplateConditionDO.setTarGrpTemplateId(tarGrpTemplateId);
                    tarGrpTemplateConditionDO.setCreateDate(new Date());
                    tarGrpTemplateConditionDO.setCreateStaff(UserUtil.loginId());
                    tarGrpTemplateConditionDO.setStatusDate(new Date());
                    tarGrpTemplateConditionDO.setUpdateStaff(UserUtil.loginId());
                    tarGrpTemplateConditionDO.setUpdateDate(new Date());
                    tarGrpTemplateConditionDO.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                    tarGrpTemplateConditionMapper.insert(tarGrpTemplateConditionDO);
                }
            }
        }
        tarGrpTemplateMap.put("resultCode", CommonConstant.CODE_SUCCESS);
        tarGrpTemplateMap.put("tarGrpTemplateId", tarGrpTemplateId);
        return tarGrpTemplateMap;
    }

    /**
     * 获取目标分群列表(分页)
     *
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Map<String, Object> listTarGrpTemplatePage(String tarGrpTemplateName, Integer page, Integer pageSize) {
        Map<String, Object> tarGrpTemplateMap = new HashMap<>();
        // 分页获取目标分群模板
        PageHelper.startPage(page, pageSize);
        List<TarGrpTemplateDO> tarGrpTemplateDOList = tarGrpTemplateMapper.selectByName(tarGrpTemplateName);
        List<TarGrpTemplateDetail> tarGrpTemplateDetailList = new ArrayList<>();
        for (TarGrpTemplateDO tarGrpTemplateDO : tarGrpTemplateDOList) {
            TarGrpTemplateDetail tarGrpTemplateDetail = BeanUtil.create(tarGrpTemplateDO, new TarGrpTemplateDetail());
            tarGrpTemplateDetailList.add(tarGrpTemplateDetail);
        }
        tarGrpTemplateMap.put("resultCode", CommonConstant.CODE_SUCCESS);
        tarGrpTemplateMap.put("tarGrpTemplateDetailList", tarGrpTemplateDetailList);
        return tarGrpTemplateMap;
    }

    /**
     * 获取目标分群列表
     *
     * @return
     */
    @Override
    public Map<String, Object> listTarGrpTemplateAll() {
        Map<String, Object> tarGrpTemplateMap = new HashMap<>();
        // 分页获取目标分群模板
        List<TarGrpTemplateDO> tarGrpTemplateDOList = tarGrpTemplateMapper.selectAll();
        List<TarGrpTemplateDetail> tarGrpTemplateDetailList = new ArrayList<>();
        for (TarGrpTemplateDO tarGrpTemplateDO : tarGrpTemplateDOList) {
            TarGrpTemplateDetail tarGrpTemplateDetail = BeanUtil.create(tarGrpTemplateDO, new TarGrpTemplateDetail());
            tarGrpTemplateDetailList.add(tarGrpTemplateDetail);
        }
        tarGrpTemplateMap.put("resultCode", CommonConstant.CODE_SUCCESS);
        tarGrpTemplateMap.put("tarGrpTemplateDetailList", tarGrpTemplateDetailList);
        return tarGrpTemplateMap;
    }


    /**
     * 获取目标分群以及条件详情
     *
     * @param tarGrpTemplateId
     * @return
     */
    @Override
    public Map<String, Object> getTarGrpTemplate(Long tarGrpTemplateId) {
        Map<String, Object> tarGrpTemplateMap = new HashMap<>();
        // 获取目标分群模板的基本信息
        TarGrpTemplateDO tarGrpTemplateDO = tarGrpTemplateMapper.selectByPrimaryKey(tarGrpTemplateId);
        TarGrpTemplateDetail tarGrpTemplateDetail = BeanUtil.create(tarGrpTemplateDO, new TarGrpTemplateDetail());
        // 获取目标分群模板的对应的条件
        List<TarGrpTemplateConditionDO> tarGrpTemplateConditionDOS = tarGrpTemplateConditionMapper.selectByTarGrpTemplateId(tarGrpTemplateId);
        List<TarGrpTemplateCondition> tarGrpTemplateConditionList = new ArrayList<>();
        for (TarGrpTemplateConditionDO tarGrpTemplateConditionDO : tarGrpTemplateConditionDOS) {
            TarGrpTemplateCondition tarGrpTemplateCondition = BeanUtil.create(tarGrpTemplateConditionDO, new TarGrpTemplateCondition());
            tarGrpTemplateConditionList.add(tarGrpTemplateCondition);
        }
        tarGrpTemplateDetail.setTarGrpTemplateConditionList(tarGrpTemplateConditionList);
        tarGrpTemplateMap.put("resultCode", CommonConstant.CODE_SUCCESS);
        tarGrpTemplateMap.put("tarGrpTemplateDetail", tarGrpTemplateDetail);
        return tarGrpTemplateMap;
    }

    /**
     * 删除目标分群模板以及条件详情
     *
     * @param tarGrpTemplateId
     * @return
     */
    @Override
    public Map<String, Object> deleteTarGrpTemplate(Long tarGrpTemplateId) {
        Map<String, Object> tarGrpTemplateMap = new HashMap<>();
        tarGrpTemplateMapper.deleteByPrimaryKey(tarGrpTemplateId);
        tarGrpTemplateConditionMapper.deleteByTarGrpTemplateId(tarGrpTemplateId);
        tarGrpTemplateMap.put("resultCode", CommonConstant.CODE_SUCCESS);
        tarGrpTemplateMap.put("tarGrpTemplateId", tarGrpTemplateId);
        return tarGrpTemplateMap;
    }

    /**
     * 删除目标分群模板条件
     *
     * @param conditionId
     * @return
     */
    @Override
    public Map<String, Object> deleteTarGrpTemplateCondition(Long conditionId) {
        Map<String, Object> tarGrpTemplateMap = new HashMap<>();
        try {
            tarGrpTemplateConditionMapper.deleteByPrimaryKey(conditionId);
            tarGrpTemplateMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            tarGrpTemplateMap.put("conditionId", conditionId);
        } catch (Exception e) {
            tarGrpTemplateMap.put("resultCode", CommonConstant.CODE_FAIL);
            tarGrpTemplateMap.put("conditionId", conditionId);
            logger.error("[op:TarGrpTemplateServiceImpl] failed to delete TarGrpTemplateCondition by conditionId = {}, Expection=", conditionId, e);
        }
        return tarGrpTemplateMap;
    }
}