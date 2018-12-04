/**
 * @(#)MktStrategyConfRuleRelServiceImpl.java, 2018/7/4.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.impl.strategy;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleRelMapper;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRuleRel;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfRuleRelService;
import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/07/04 09:56
 * version: V1.0
 */
public class MktStrategyConfRuleRelServiceImpl extends BaseService implements MktStrategyConfRuleRelService {

    @Autowired
    private MktStrategyConfRuleRelMapper mktStrategyConfRuleRelMapper;

    /**
     * 添加策略配置与规则的关联关系
     *
     * @param mktStrategyConfRuleRel
     * @return
     */
    @Override
    public Map<String, Object> saveMktStrConfRuleRel(MktStrategyConfRuleRel mktStrategyConfRuleRel) {
        Map<String, Object> mktStryConfRuleRelMap = new HashMap<>();
        MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO = new MktStrategyConfRuleRelDO();
        try {
            CopyPropertiesUtil.copyBean2Bean(mktStrategyConfRuleRelDO, mktStrategyConfRuleRel);
            mktStrategyConfRuleRelMapper.insert(mktStrategyConfRuleRelDO);
            mktStryConfRuleRelMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStryConfRuleRelMap.put("resultMsg", ErrorCode.SAVE_MKT_STR_CONF_RULE_REL_SUCCESS.getErrorMsg());
            mktStryConfRuleRelMap.put("mktStrategyConfRuleRelId", mktStrategyConfRuleRelDO.getMktStrategyConfRuleRelId());
        } catch (Exception e) {
            logger.error("[op:MktStrategyConfRuleRelServiceImpl] failed to save MktStrategyConfRuleRel = {}, Exception = ", JSON.toJSON(mktStrategyConfRuleRel), e);
            mktStryConfRuleRelMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktStryConfRuleRelMap.put("resultMsg", ErrorCode.SAVE_MKT_STR_CONF_RULE_REL_FAILURE.getErrorMsg());
            mktStryConfRuleRelMap.put("mktStrategyConfRuleRelId", mktStrategyConfRuleRelDO.getMktStrategyConfRuleRelId());
        }
        return mktStryConfRuleRelMap;
    }

    /**
     * 修改策略配置与规则的关联关系
     *
     * @param mktStrategyConfRuleRel
     * @return
     */
    @Override
    public Map<String, Object> updateMktStrConfRuleRel(MktStrategyConfRuleRel mktStrategyConfRuleRel) {
        Map<String, Object> mktStryConfRuleRelMap = new HashMap<>();
        MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO = new MktStrategyConfRuleRelDO();
        try {
            CopyPropertiesUtil.copyBean2Bean(mktStrategyConfRuleRelDO, mktStrategyConfRuleRel);
            mktStrategyConfRuleRelMapper.updateByPrimaryKey(mktStrategyConfRuleRelDO);
            mktStryConfRuleRelMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStryConfRuleRelMap.put("resultMsg", ErrorCode.UPDATE_MKT_RULE_STR_CONF_RULE_SUCCESS.getErrorMsg());
            mktStryConfRuleRelMap.put("mktStrategyConfRuleRelId", mktStrategyConfRuleRelDO.getMktStrategyConfRuleRelId());
        } catch (Exception e) {
            logger.error("[op:MktStrategyConfRuleRelServiceImpl] failed to updateMktStrConfRuleRel = {} , Exception = ", JSON.toJSON(mktStrategyConfRuleRel), e);
            mktStryConfRuleRelMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktStryConfRuleRelMap.put("resultMsg", ErrorCode.UPDATE_MKT_RULE_STR_CONF_RULE_SUCCESS.getErrorMsg());
        }
        return mktStryConfRuleRelMap;
    }

    /**
     * 通过配置规则关系Id查出对应的记录
     *
     * @param mktStrategyConfRuleRelId
     * @return
     */
    @Override
    public Map<String, Object> getMktStrConfRuleRel(Long mktStrategyConfRuleRelId) {
        Map<String, Object> mktStryConfRuleRelMap = new HashMap<>();
        MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO = new MktStrategyConfRuleRelDO();
        MktStrategyConfRuleRel mktStrategyConfRuleRel = new MktStrategyConfRuleRel();
        try {
            mktStrategyConfRuleRelMapper.selectByPrimaryKey(mktStrategyConfRuleRelId);
            CopyPropertiesUtil.copyBean2Bean(mktStrategyConfRuleRel, mktStrategyConfRuleRelDO);
            mktStryConfRuleRelMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStryConfRuleRelMap.put("resultMsg", ErrorCode.UPDATE_MKT_RULE_STR_CONF_RULE_SUCCESS.getErrorMsg());
            mktStryConfRuleRelMap.put("mktStrategyConfRuleRel", mktStrategyConfRuleRel);
        } catch (Exception e) {
            logger.error("[op:MktStrategyConfRuleRelServiceImpl] failed to mktStrategyConfRuleRelId = {}， Exception = ", mktStrategyConfRuleRelId, e);
            mktStryConfRuleRelMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktStryConfRuleRelMap.put("resultMsg", ErrorCode.UPDATE_MKT_RULE_STR_CONF_RULE_SUCCESS.getErrorMsg());
        }
        return mktStryConfRuleRelMap;
    }


    /**
     * 获取所有策略配置对应的规则id集合
     *
     * @return
     */
    @Override
    public Map<String, Object> listAllMktStrConfRuleRel() {
        Map<String, Object> mktStryConfRuleRelMap = new HashMap<>();
        List<MktStrategyConfRuleRel> mktStrConfRuleRelList = new ArrayList<>();
        try {
            List<MktStrategyConfRuleRelDO> mktStrConfRuleRelDOList = mktStrategyConfRuleRelMapper.selectAll();
            for (MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO : mktStrConfRuleRelDOList) {
                MktStrategyConfRuleRel mktStrConfRuleRel = new MktStrategyConfRuleRel();
                CopyPropertiesUtil.copyBean2Bean(mktStrConfRuleRel, mktStrategyConfRuleRelDO);
                mktStrConfRuleRelList.add(mktStrConfRuleRel);
            }
            mktStryConfRuleRelMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStryConfRuleRelMap.put("resultMsg", ErrorCode.GET_MKT_RULE_STR_CONF_RULE_SUCCESS.getErrorMsg());
            mktStryConfRuleRelMap.put("mktStrConfRuleRelList", mktStrConfRuleRelList);
        } catch (Exception e) {
            logger.error("[op:MktStrategyConfRuleRelServiceImpl] failed to listAllMktStrConfRuleRel， Exception = ", e);
            mktStryConfRuleRelMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktStryConfRuleRelMap.put("resultMsg", ErrorCode.GET_MKT_RULE_STR_CONF_RULE_FAILURE.getErrorMsg());
        }
        return mktStryConfRuleRelMap;
    }

    /**
     * 通过策略配置查询对应的规则id集合
     *
     * @param mktStrategyConfId
     * @return
     */
    @Override
    public Map<String, Object> listMktStrConfRuleRel(Long mktStrategyConfId) {
        Map<String, Object> mktStryConfRuleRelMap = new HashMap<>();
        List<MktStrategyConfRuleRel> mktStrConfRuleRelList = new ArrayList<>();
        try {
            List<MktStrategyConfRuleRelDO> mktStrConfRuleRelDOList = mktStrategyConfRuleRelMapper.selectByMktStrategyConfId(mktStrategyConfId);
            for (MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO : mktStrConfRuleRelDOList) {
                MktStrategyConfRuleRel mktStrConfRuleRel = new MktStrategyConfRuleRel();
                CopyPropertiesUtil.copyBean2Bean(mktStrConfRuleRel, mktStrategyConfRuleRelDO);
                mktStrConfRuleRelList.add(mktStrConfRuleRel);
            }
            mktStryConfRuleRelMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStryConfRuleRelMap.put("resultMsg", ErrorCode.GET_MKT_RULE_STR_CONF_RULE_SUCCESS.getErrorMsg());
            mktStryConfRuleRelMap.put("mktStrConfRuleRelList", mktStrConfRuleRelList);
        } catch (Exception e) {
            logger.error("[op:MktStrategyConfRuleRelServiceImpl] failed to listMktStrConfRuleRel mktStrategyConfId = {}， Exception = ", mktStrategyConfId, e);
            mktStryConfRuleRelMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktStryConfRuleRelMap.put("resultMsg", ErrorCode.GET_MKT_RULE_STR_CONF_RULE_FAILURE.getErrorMsg());
        }
        return mktStryConfRuleRelMap;
    }

    /**
     * 删除略配置和对应的规则的某一条
     *
     * @param mktStrategyConfRuleRelId
     * @return
     */
    @Override
    public Map<String, Object> deleteMktStrConfRuleRel(Long mktStrategyConfRuleRelId) {
        Map<String, Object> mktStryConfRuleRelMap = null;
        try {
            mktStryConfRuleRelMap = new HashMap<>();
            mktStrategyConfRuleRelMapper.deleteByPrimaryKey(mktStrategyConfRuleRelId);
            mktStryConfRuleRelMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStryConfRuleRelMap.put("resultMsg", "删除成功！");
        } catch (Exception e) {
            logger.error("[op:MktStrategyConfRuleRelServiceImpl] failed to deleteMktStrConfRuleRel mktStrategyConfId = {}， Exception = ", mktStrategyConfRuleRelId, e);
            mktStryConfRuleRelMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktStryConfRuleRelMap.put("resultMsg", "删除失败！");
        }
        return mktStryConfRuleRelMap;
    }

    /**
     * 删除略配置和对应的规则所有关系
     *
     * @param mktStrategyConfId
     * @return
     */
    @Override
    public Map<String, Object> deleteMktStrConfRuleRelByConfId(Long mktStrategyConfId) {
        Map<String, Object> mktStryConfRuleRelMap = new HashMap<>();
        try {
            mktStrategyConfRuleRelMapper.deleteByMktStrategyConfId(mktStrategyConfId);
            mktStryConfRuleRelMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStryConfRuleRelMap.put("resultMsg", "删除成功！");
        } catch (Exception e) {
            logger.error("[op:MktStrategyConfRuleRelServiceImpl] failed to deleteMktStrConfRuleRelByConfId mktStrategyConfId = {}， Exception = ", mktStrategyConfId, e);
            mktStryConfRuleRelMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktStryConfRuleRelMap.put("resultMsg", "删除失败！");
        }
        return mktStryConfRuleRelMap;
    }
}