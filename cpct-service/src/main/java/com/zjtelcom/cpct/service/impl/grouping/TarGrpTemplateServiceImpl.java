/**
 * @(#)TarGrpTemplateServiceImpl.java, 2018/9/6.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.impl.grouping;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpTemplateConditionMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpTemplateMapper;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.grouping.TarGrpTemplateConditionDO;
import com.zjtelcom.cpct.domain.grouping.TarGrpTemplateDO;
import com.zjtelcom.cpct.dto.channel.OperatorDetail;
import com.zjtelcom.cpct.dto.grouping.TarGrpTemConditionVO;
import com.zjtelcom.cpct.dto.grouping.TarGrpTemplateCondition;
import com.zjtelcom.cpct.dto.grouping.TarGrpTemplateDetail;
import com.zjtelcom.cpct.enums.FitDomain;
import com.zjtelcom.cpct.enums.LeftParamType;
import com.zjtelcom.cpct.enums.Operator;
import com.zjtelcom.cpct.enums.RightParamType;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.grouping.TarGrpTemplateService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import com.zjtelcom.cpct.util.UserUtil;
import com.zjtelcom.cpct.vo.grouping.TarGrpConditionVO;
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

    @Autowired
    private InjectionLabelMapper injectionLabelMapper;

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
        if (tarGrpTemplateDetail.getTarGrpTemConditionVOList() != null && tarGrpTemplateDetail.getTarGrpTemConditionVOList().size() > 0) {
            for (TarGrpTemConditionVO tarGrpTemConditionVO : tarGrpTemplateDetail.getTarGrpTemConditionVOList()) {
                if (tarGrpTemConditionVO.getOperType() == null || tarGrpTemConditionVO.getOperType().equals("")) {
                    tarGrpTemplateMap.put("resultCode", CommonConstant.CODE_FAIL);
                    tarGrpTemplateMap.put("resultMsg", "请选择下拉框运算类型");
                    return tarGrpTemplateMap;
                }
                TarGrpTemplateConditionDO tarGrpTemplateConditionDO = BeanUtil.create(tarGrpTemConditionVO, new TarGrpTemplateConditionDO());
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
        if (tarGrpTemplateDetail.getTarGrpTemConditionVOList() != null && tarGrpTemplateDetail.getTarGrpTemConditionVOList().size() > 0) {
            for (TarGrpTemConditionVO tarGrpTemConditionVO : tarGrpTemplateDetail.getTarGrpTemConditionVOList()) {
                if (tarGrpTemConditionVO.getOperType() == null || tarGrpTemConditionVO.getOperType().equals("")) {
                    tarGrpTemplateMap.put("resultCode", CommonConstant.CODE_FAIL);
                    tarGrpTemplateMap.put("resultMsg", "请选择下拉框运算类型");
                    return tarGrpTemplateMap;
                }
                TarGrpTemplateConditionDO tarGrpTemplateConditionDO = BeanUtil.create(tarGrpTemConditionVO, new TarGrpTemplateConditionDO());
                if (tarGrpTemConditionVO.getConditionId() != null) {
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
        Page pageInfo = new Page(new PageInfo(tarGrpTemplateDOList));
        List<TarGrpTemplateDetail> tarGrpTemplateDetailList = new ArrayList<>();
        for (TarGrpTemplateDO tarGrpTemplateDO : tarGrpTemplateDOList) {
            TarGrpTemplateDetail tarGrpTemplateDetail = BeanUtil.create(tarGrpTemplateDO, new TarGrpTemplateDetail());
            tarGrpTemplateDetailList.add(tarGrpTemplateDetail);
        }
        tarGrpTemplateMap.put("resultCode", CommonConstant.CODE_SUCCESS);
        tarGrpTemplateMap.put("tarGrpTemplateDetailList", tarGrpTemplateDetailList);
        tarGrpTemplateMap.put("pageInfo", pageInfo);
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
        List<TarGrpTemConditionVO> tarGrpTemConditionVOList = new ArrayList<>();
        for (TarGrpTemplateConditionDO tarGrpTemplateConditionDO : tarGrpTemplateConditionDOS) {
            //TarGrpTemConditionVO tarGrpTemplateCondition = BeanUtil.create(tarGrpTemplateConditionDO, new TarGrpTemConditionVO());
            TarGrpTemConditionVO tarGrpTemConditionVO = BeanUtil.create(tarGrpTemplateConditionDO, new TarGrpTemConditionVO());
            List<String> valueList = new ArrayList<>();
            List<OperatorDetail> operatorList = new ArrayList<>();
            //塞入左参中文名
            Label label = injectionLabelMapper.selectByPrimaryKey(Long.valueOf(tarGrpTemConditionVO.getLeftParam()));
            if (label==null){
                continue;
            }
            tarGrpTemConditionVO.setLeftParamName(label.getInjectionLabelName());
            //塞入领域
//            FitDomain fitDomain = null;
//            if (label.getFitDomain() != null) {
//                fitDomain = FitDomain.getFitDomain(Integer.parseInt(label.getFitDomain()));
//                tarGrpTemConditionVO.setFitDomainId(Long.valueOf(fitDomain.getValue()));
//                tarGrpTemConditionVO.setFitDomainName(fitDomain.getDescription());
//            }
            //将操作符转为中文
            if (tarGrpTemConditionVO.getOperType()!=null && !tarGrpTemConditionVO.getOperType().equals("")){
                Operator op = Operator.getOperator(Integer.parseInt(tarGrpTemConditionVO.getOperType()));
                tarGrpTemConditionVO.setOperTypeName(op.getDescription());
            }
            //todo 通过左参id
            String operators = label.getOperator();
            String[] operator = operators.split(",");
            if (operator.length > 1) {
                for (int i = 0; i < operator.length; i++) {
                    Operator opTT = Operator.getOperator(Integer.parseInt(operator[i]));
                    OperatorDetail operatorDetail = new OperatorDetail();
                    operatorDetail.setOperName(opTT.getDescription());
                    operatorDetail.setOperValue(opTT.getValue());
                    operatorList.add(operatorDetail);
                }
            } else {
                if (operator.length == 1) {
                    OperatorDetail operatorDetail = new OperatorDetail();
                    Operator opTT = Operator.getOperator(Integer.parseInt(operator[0]));
                    operatorDetail.setOperName(opTT.getDescription());
                    operatorDetail.setOperValue(opTT.getValue());
                    operatorList.add(operatorDetail);
                }
            }
            String rightOperand = label.getRightOperand();
            String[] rightOperands = rightOperand.split(",");
            if (rightOperands.length > 1) {
                for (int i = 0; i < rightOperands.length; i++) {
                    valueList.add(rightOperands[i]);
                }
            } else {
                if (rightOperands.length == 1) {
                    valueList.add(rightOperands[0]);
                }
            }
            tarGrpTemConditionVO.setValueList(valueList);
            tarGrpTemConditionVO.setConditionType(label.getConditionType());
            tarGrpTemConditionVO.setOperatorList(operatorList);
            tarGrpTemConditionVOList.add(tarGrpTemConditionVO);
        }
        tarGrpTemplateDetail.setTarGrpTemConditionVOList(tarGrpTemConditionVOList);
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