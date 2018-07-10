/**
 * @(#)MktCampaignServiceImpl.java, 2018/6/22.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.impl.campaign;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCamEvtRelMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignRelMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamEvtRelDO;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.campaign.MktCampaignRelDO;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dto.campaign.*;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.enums.ParamKeyEnum;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.campaign.MktCampaignService;
import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description:
 * author: linchao
 * date: 2018/06/22 22:22
 * version: V1.0
 */
@Service
@Transactional
public class MktCampaignServiceImpl extends BaseService implements MktCampaignService {

    @Autowired
    private MktCampaignMapper mktCampaignMapper;

    @Autowired
    private MktCampaignRelMapper mktCampaignRelMapper;

    @Autowired
    private MktCamEvtRelMapper mktCamEvtRelMapper;

    @Autowired
    private SysParamsMapper sysParamsMapper;

    @Autowired
    private ContactEvtMapper contactEvtMapper;

    /**
     * 添加活动基本信息 并建立关系
     *
     * @param mktCampaignVO
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> createMktCampaign(MktCampaignVO mktCampaignVO) throws Exception {
        MktCampaignDO mktCampaignDO = new MktCampaignDO();
        CopyPropertiesUtil.copyBean2Bean(mktCampaignDO, mktCampaignVO);
        // 创建活动基本信息
        mktCampaignMapper.insert(mktCampaignDO);
        Long mktCampaignId = mktCampaignDO.getMktCampaignId();

        // 创建活动与活动之间的关系
        for (Long applyRegionIds : mktCampaignVO.getApplyRegionIds()) {
            MktCampaignRelDO mktCampaignRelDO = new MktCampaignRelDO();
            CopyPropertiesUtil.copyBean2Bean(mktCampaignRelDO, mktCampaignVO);
            mktCampaignRelDO.setaMktCampaignId(mktCampaignId);
            mktCampaignRelDO.setzMktCampaignId(mktCampaignId);
            mktCampaignRelDO.setApplyRegionId(applyRegionIds);
            mktCampaignRelDO.setCreateStaff(UserUtil.loginId());
            mktCampaignRelDO.setCreateDate(new Date());
            mktCampaignRelDO.setUpdateStaff(UserUtil.loginId());
            mktCampaignRelDO.setUpdateDate(new Date());
            mktCampaignRelMapper.insert(mktCampaignRelDO);
        }

        //创建活动与事件的关联
        MktCamEvtRelDO mktCamEvtRelDO = new MktCamEvtRelDO();
        mktCamEvtRelDO.setMktCampaignId(mktCampaignId);
        mktCamEvtRelDO.setEventId(mktCampaignVO.getEventId());
        mktCamEvtRelDO.setCreateStaff(UserUtil.loginId());
        mktCamEvtRelDO.setCreateDate(new Date());
        mktCamEvtRelDO.setUpdateStaff(UserUtil.loginId());
        mktCamEvtRelDO.setUpdateDate(new Date());
        mktCamEvtRelMapper.insert(mktCamEvtRelDO);

        Map<String, Object> maps = new HashMap<>();
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("mktCampaignId", mktCampaignId);
        return maps;
    }

    /**
     * 修改活动基本信息 并重新建立关系
     *
     * @param mktCampaignVO
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> modMktCampaign(MktCampaignVO mktCampaignVO) throws Exception {
        MktCampaignDO mktCampaignDO = new MktCampaignDO();
        CopyPropertiesUtil.copyBean2Bean(mktCampaignDO, mktCampaignVO);
        // 创建活动基本信息
        mktCampaignMapper.updateByPrimaryKey(mktCampaignDO);
        Long mktCampaignId = mktCampaignDO.getMktCampaignId();
        // 删除与原有关系
        mktCampaignRelMapper.deleteByAmktCampaignId(mktCampaignId);
        // 重新创建活动与活动之间的关系
        for (Long applyRegionIds : mktCampaignVO.getApplyRegionIds()) {
            MktCampaignRelDO mktCampaignRelDO = new MktCampaignRelDO();
            CopyPropertiesUtil.copyBean2Bean(mktCampaignRelDO, mktCampaignVO);
            mktCampaignRelDO.setaMktCampaignId(mktCampaignId);
            mktCampaignRelDO.setzMktCampaignId(mktCampaignId);
            mktCampaignRelDO.setApplyRegionId(applyRegionIds);
            mktCampaignRelDO.setCreateStaff(UserUtil.loginId());
            mktCampaignRelDO.setCreateDate(new Date());
            mktCampaignRelDO.setUpdateStaff(UserUtil.loginId());
            mktCampaignRelDO.setUpdateDate(new Date());
            mktCampaignRelMapper.insert(mktCampaignRelDO);
        }

        Map<String, Object> maps = new HashMap<>();
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("mktCampaignId", mktCampaignId);
        return maps;
    }


    /**
     * 获取活动基本信息 并删除建立关系
     *
     * @param mktCampaignId
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> getMktCampaign(Long mktCampaignId) throws Exception {
        // 获取关系
        List<MktCampaignRelDO> mktCampaignRelDOList = mktCampaignRelMapper.selectByAmktCampaignId(mktCampaignId);
        List<Long> applyRegionIds = new ArrayList<>();
        String relType = null;
        for (MktCampaignRelDO mktCampaignRelDO : mktCampaignRelDOList) {
            applyRegionIds.add(mktCampaignRelDO.getApplyRegionId());
            relType = mktCampaignRelDO.getRelType();
        }
        // 获取活动基本信息
        MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCampaignId);
        MktCampaignVO mktCampaignVO = new MktCampaignVO();
        CopyPropertiesUtil.copyBean2Bean(mktCampaignVO, mktCampaignDO);
        mktCampaignVO.setApplyRegionIds(applyRegionIds);
        mktCampaignVO.setRelType(relType);

        // 获取所有的sysParam
        Map<String, String> paramMap = new HashMap<>();
        List<SysParams> sysParamList = sysParamsMapper.selectAll("", 0L);
        for (SysParams sysParams : sysParamList) {
            paramMap.put(sysParams.getParamKey() + sysParams.getParamValue(), sysParams.getParamName());
        }
        mktCampaignVO.setTiggerTypeValue(paramMap.
                get(ParamKeyEnum.TIGGER_TYPE.getParamKey() + mktCampaignDO.getTiggerType()));
        mktCampaignVO.setRelTypeValue(paramMap.
                get(ParamKeyEnum.REL_TYPE.getParamKey() + relType));
        mktCampaignVO.setMktCampaignTypeValue(paramMap.
                get(ParamKeyEnum.MKT_CAMPAIGN_TYPE.getParamKey() + mktCampaignDO.getMktCampaignType()));
        mktCampaignVO.setStatusCdValue(paramMap.
                get(ParamKeyEnum.STATUS_CD.getParamKey() + mktCampaignDO.getStatusCd()));
        mktCampaignVO.setExecTypeValue(paramMap.
                get(ParamKeyEnum.EXEC_TYPE.getParamKey() + mktCampaignDO.getExecType()));

        //TODO 获取活动关联的事件
        MktCamEvtRelDO mktCamEvtRelDO = mktCamEvtRelMapper.qryByMktCampaignId(mktCampaignId);
        if (mktCamEvtRelDO != null) {
            Long eventId = mktCamEvtRelDO.getEventId();
            ContactEvt contactEvt = contactEvtMapper.getEventById(eventId);
            if (contactEvt != null) {
                mktCampaignVO.setEventId(eventId);
                mktCampaignVO.setEventName(contactEvt.getContactEvtName());
            }
        }
        Map<String, Object> maps = new HashMap<>();
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("mktCampaignVO", mktCampaignVO);
        return maps;
    }

    /**
     * 删除活动基本信息 并删除建立关系
     *
     * @param mktCampaignId
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> delMktCampaign(Long mktCampaignId) throws Exception {
        // 删除关系
        mktCampaignRelMapper.deleteByAmktCampaignId(mktCampaignId);
        // 删除活动基本信息
        mktCampaignMapper.deleteByPrimaryKey(mktCampaignId);
        Map<String, Object> maps = new HashMap<>();
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("mktCampaignId", mktCampaignId);
        return maps;
    }


    /**
     * 新增营销活动（编码：5013010002 ）
     *
     * @param mktCampaignDetail
     * @return
     */
/*
    @Override
    public int createMktCampaign(MktCampaignDetail mktCampaignDetail) throws Exception {

        //获取活动主表信息
        MktCampaignDO mktCampaign = new MktCampaignDO();
        CopyPropertiesUtil.copyBean2Bean(mktCampaign, mktCampaignDetail);//映射字段
        //保存主表信息
        int i = mktCampaignMapper.insert(mktCampaign);
        //获取主表id
        Long mainId = mktCampaign.getMktCampaignId();
*/

    //分群信息
/*        List<MktCamGrpRul> mktCamGrpRuls = mktCampaignDetail.getMktCamGrpRuls();
        //todo 保存分群信息


        //策略信息
        List<MktStrategyDetail> mktCampaignStrategyDetails = mktCampaignDetail.getMktCampaignStrategyDetails();
        //todo 保存策略

        //营销活动条目
        List<MktCamItem> mktCamItems = mktCampaignDetail.getMktCamItems();
        //todo 营销活动条目*/

    //事件
//        List<ContactEvt> mktCampaignEvts = mktCampaignDetail.getMktCampaignEvts();
    //事件场景
//        List<EventScene> eventScenes = mktCampaignDetail.getEventScenes();
    //todo 保存事件关联

/*

        return 0;
    }
*/

    /**
     * 修改营销活动（编码：5013010003 ）
     *
     * @param mktCampaignDetail
     * @return
     */
/*    @Override
    public int modMktCampaign(MktCampaignDetail mktCampaignDetail) throws Exception {
        //映射字段
        MktCampaignDO mktCampaign = new MktCampaignDO();
        CopyPropertiesUtil.copyBean2Bean(mktCampaign, mktCampaignDetail);
        mktCampaignMapper.updateByPrimaryKey(mktCampaign);


        return 0;
    }*/

    /**
     * 查询事件列表
     */
    @Override
    public Map<String, Object> qryMktCampaignListPage(String mktCampaignName, String statusCd, String tiggerType, String mktCampaignType, Integer page, Integer pageSize) {
        Map<String, Object> maps = new HashMap<>();
        PageHelper.startPage(page, pageSize);
        MktCampaignDO MktCampaignPar = new MktCampaignDO();
        MktCampaignPar.setMktCampaignName(mktCampaignName);
        MktCampaignPar.setStatusCd(statusCd);
        MktCampaignPar.setTiggerType(tiggerType);
        MktCampaignPar.setMktCampaignType(mktCampaignType);
        List<MktCampaignDO> mktCampaignDOList = mktCampaignMapper.qryMktCampaignListPage(MktCampaignPar);

        // 获取所有的sysParam
        Map<String, String> paramMap = new HashMap<>();
        List<SysParams> sysParamList = sysParamsMapper.selectAll("", 0L);
        for (SysParams sysParams : sysParamList) {
            paramMap.put(sysParams.getParamKey() + sysParams.getParamValue(), sysParams.getParamName());
        }

        List<MktCampaignVO> mktCampaignVOList = new ArrayList<>();
        for (MktCampaignDO mktCampaignDO : mktCampaignDOList) {
            // 从活动关系表中获取
            List<MktCampaignRelDO> mktCampaignRelDOList = mktCampaignRelMapper.selectByAmktCampaignId(mktCampaignDO.getMktCampaignId());
            String relType = null;
            if (mktCampaignRelDOList.size() > 0) {
                relType = mktCampaignRelDOList.get(0).getRelType();
            }
            MktCampaignVO mktCampaignVO = new MktCampaignVO();
            try {
                CopyPropertiesUtil.copyBean2Bean(mktCampaignVO, mktCampaignDO);
            } catch (Exception e) {
                logger.error("Excetion:",e);
            }
            mktCampaignVO.setTiggerTypeValue(paramMap.
                    get(ParamKeyEnum.TIGGER_TYPE.getParamKey() + mktCampaignDO.getTiggerType()));
            mktCampaignVO.setRelTypeValue(paramMap.
                    get(ParamKeyEnum.REL_TYPE.getParamKey() + relType));
            mktCampaignVO.setMktCampaignTypeValue(paramMap.
                    get(ParamKeyEnum.MKT_CAMPAIGN_TYPE.getParamKey() + mktCampaignDO.getMktCampaignType()));
            mktCampaignVO.setStatusCdValue(paramMap.
                    get(ParamKeyEnum.STATUS_CD.getParamKey() + mktCampaignDO.getStatusCd()));
            mktCampaignVO.setExecTypeValue(paramMap.
                    get(ParamKeyEnum.EXEC_TYPE.getParamKey() + mktCampaignDO.getExecType()));
            mktCampaignVOList.add(mktCampaignVO);
        }


        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("mktCampaigns", mktCampaignVOList);
        maps.put("pageInfo", new Page(new PageInfo(mktCampaignVOList)));
        return maps;
    }
}