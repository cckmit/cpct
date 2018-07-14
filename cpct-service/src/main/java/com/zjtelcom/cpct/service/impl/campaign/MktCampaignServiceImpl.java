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
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.system.SysAreaMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.SysArea;
import com.zjtelcom.cpct.domain.User;
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dto.campaign.*;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.EventDTO;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConf;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfDetail;
import com.zjtelcom.cpct.enums.ParamKeyEnum;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.campaign.MktCampaignService;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfService;
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
     * 策略配置和活动关联
     */
    @Autowired
    private MktCamStrategyConfRelMapper mktCamStrategyConfRelMapper;

    /**
     * 策略配置基本信息
     */
    @Autowired
    private MktStrategyConfMapper mktStrategyConfMapper;

    /**
     * 策略配置service
     */
    @Autowired
    private MktStrategyConfService mktStrategyConfService;

    /**
     * 下发城市与活动关联
     */
    @Autowired
    private MktCamCityRelMapper mktCamCityRelMapper;

    /**
     * 下发地市
     */
    @Autowired
    private SysAreaMapper sysAreaMapper;

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
        mktCampaignDO.setCreateDate(new Date());
        mktCampaignDO.setCreateStaff(UserUtil.loginId());
        mktCampaignDO.setUpdateDate(new Date());
        mktCampaignDO.setUpdateStaff(UserUtil.loginId());
        mktCampaignDO.setStatusDate(new Date());
        mktCampaignMapper.insert(mktCampaignDO);
        Long mktCampaignId = mktCampaignDO.getMktCampaignId();
        //创建活动与城市之间的关系
        for (CityProperty cityProperty : mktCampaignVO.getApplyRegionIdList()) {
            MktCamCityRelDO mktCamCityRelDO = new MktCamCityRelDO();
            mktCamCityRelDO.setCityId(cityProperty.getCityPropertyId());
            mktCamCityRelDO.setMktCampaignId(mktCampaignId);
            mktCamCityRelDO.setCreateDate(new Date());
            mktCamCityRelDO.setCreateStaff(UserUtil.loginId());
            mktCamCityRelDO.setUpdateDate(new Date());
            mktCamCityRelDO.setUpdateStaff(UserUtil.loginId());
            mktCamCityRelMapper.insert(mktCamCityRelDO);
        }

/*
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
*/

        //创建活动与事件的关联
        for (EventDTO eventDTO : mktCampaignVO.getEventDTOS()) {
            MktCamEvtRelDO mktCamEvtRelDO = new MktCamEvtRelDO();
            mktCamEvtRelDO.setMktCampaignId(mktCampaignId);
            mktCamEvtRelDO.setEventId(eventDTO.getEventId());
            mktCamEvtRelDO.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
            mktCamEvtRelDO.setCreateStaff(UserUtil.loginId());
            mktCamEvtRelDO.setCreateDate(new Date());
            mktCamEvtRelDO.setUpdateStaff(UserUtil.loginId());
            mktCamEvtRelDO.setUpdateDate(new Date());
            mktCamEvtRelMapper.insert(mktCamEvtRelDO);
        }

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
        // 更新活动基本信息
        mktCampaignMapper.updateByPrimaryKey(mktCampaignDO);
        Long mktCampaignId = mktCampaignDO.getMktCampaignId();

        // 遍历所有策略集合
        for (MktStrategyConfDetail mktStrategyConfDetail : mktCampaignVO.getMktStrategyConfDetailList()) {
            if (mktStrategyConfDetail.getMktStrategyConfId() != 0 && mktStrategyConfDetail.getMktStrategyConfId() != null) {
                mktStrategyConfService.updateMktStrategyConf(mktStrategyConfDetail);
            } else {
                mktStrategyConfService.saveMktStrategyConf(mktStrategyConfDetail);
            }
        }


/*        mktStrategyConfService.saveMktStrategyConf();
        // 删除与原有关系
        mktCampaignRelMapper.deleteByAmktCampaignId(mktCampaignId);
        // 重新创建活动与活动之间的关系
        for (CityProperty cityProperty : mktCampaignVO.getApplyRegionIdList()) {
            MktCampaignRelDO mktCampaignRelDO = new MktCampaignRelDO();
            CopyPropertiesUtil.copyBean2Bean(mktCampaignRelDO, mktCampaignVO);
            mktCampaignRelDO.setaMktCampaignId(mktCampaignId);
            mktCampaignRelDO.setzMktCampaignId(mktCampaignId);
            mktCampaignRelDO.setApplyRegionId(cityProperty.getCityPropertyId());
            mktCampaignRelDO.setUpdateStaff(UserUtil.loginId());
            mktCampaignRelDO.setUpdateDate(new Date());
            mktCampaignRelMapper.insert(mktCampaignRelDO);
        }
*/
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
        List<CityProperty> applyRegionIds = new ArrayList<>();
/*        String relType = null;
        for (MktCampaignRelDO mktCampaignRelDO : mktCampaignRelDOList) {
            applyRegionIds.add(mktCampaignRelDO.getApplyRegionId());
            relType = mktCampaignRelDO.getRelType();
        }*/
        // 获取活动基本信息
        MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCampaignId);
        MktCampaignVO mktCampaignVO = new MktCampaignVO();
        CopyPropertiesUtil.copyBean2Bean(mktCampaignVO, mktCampaignDO);
        List<MktCamCityRelDO> mktCamCityRelDOList = mktCamCityRelMapper.selectByMktCampaignId(mktCampaignVO.getMktCampaignId());
        for (MktCamCityRelDO mktCamCityRelDO : mktCamCityRelDOList) {
            SysArea sysArea = sysAreaMapper.selectByPrimaryKey(mktCamCityRelDO.getCityId().intValue());
            CityProperty cityProperty = new CityProperty();
            cityProperty.setCityPropertyId(sysArea.getAreaId().longValue());
            cityProperty.setCityPropertyName(sysArea.getName());
            applyRegionIds.add(cityProperty);
        }
        mktCampaignVO.setApplyRegionIdList(applyRegionIds);

/*
        mktCampaignVO.setApplyRegionIds(applyRegionIds);
        mktCampaignVO.setRelType(relType);
*/

        // 获取所有的sysParam
        Map<String, String> paramMap = new HashMap<>();
        List<SysParams> sysParamList = sysParamsMapper.selectAll("", 0L);
        for (SysParams sysParams : sysParamList) {
            paramMap.put(sysParams.getParamKey() + sysParams.getParamValue(), sysParams.getParamName());
        }
        mktCampaignVO.setTiggerTypeValue(paramMap.
                get(ParamKeyEnum.TIGGER_TYPE.getParamKey() + mktCampaignDO.getTiggerType()));
        mktCampaignVO.setMktCampaignCategoryValue(paramMap.
                get(ParamKeyEnum.MKT_CAMPAIGN_CATEGORY.getParamKey() + mktCampaignDO.getMktCampaignCategory()));
        mktCampaignVO.setMktCampaignTypeValue(paramMap.
                get(ParamKeyEnum.MKT_CAMPAIGN_TYPE.getParamKey() + mktCampaignDO.getMktCampaignType()));
        mktCampaignVO.setStatusCdValue(paramMap.
                get(ParamKeyEnum.STATUS_CD.getParamKey() + mktCampaignDO.getStatusCd()));
        mktCampaignVO.setExecTypeValue(paramMap.
                get(ParamKeyEnum.EXEC_TYPE.getParamKey() + mktCampaignDO.getExecType()));

        // 获取活动关联的事件
        List<MktCamEvtRelDO> mktCamEvtRelDOList = mktCamEvtRelMapper.qryByMktCampaignId(mktCampaignDO.getMktCampaignId());
        if (mktCamEvtRelDOList != null) {
            List<EventDTO> eventDTOList = new ArrayList<>();
            for (MktCamEvtRelDO mktCamEvtRelDO : mktCamEvtRelDOList) {
                Long eventId = mktCamEvtRelDO.getEventId();
                ContactEvt contactEvt = contactEvtMapper.getEventById(eventId);
                if (contactEvt != null) {
                    EventDTO eventDTO = new EventDTO();
                    eventDTO.setEventId(eventId);
                    eventDTO.setEventName(contactEvt.getContactEvtName());
                    eventDTOList.add(eventDTO);
                }
            }
            mktCampaignVO.setEventDTOS(eventDTOList);
        }

        // 获取活动关联策略集合
        List<MktStrategyConf> mktStrategyConfList = new ArrayList<>();
        List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOList = mktCamStrategyConfRelMapper.selectByMktCampaignId(mktCampaignId);
        for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOList) {
            MktStrategyConfDO mktStrategyConfDO = mktStrategyConfMapper.selectByPrimaryKey(mktCamStrategyConfRelDO.getStrategyConfId());
            MktStrategyConf mktStrategyConf = new MktStrategyConf();
            CopyPropertiesUtil.copyBean2Bean(mktStrategyConf, mktStrategyConfDO);
            mktStrategyConfList.add(mktStrategyConf);
        }
        mktCampaignVO.setMktStrategyConfList(mktStrategyConfList);

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

        // 删出活动和事件的关联
        mktCamEvtRelMapper.deleteByMktCampaignId(mktCampaignId);
        //删除活动下的策略以及规则
        List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOList = mktCamStrategyConfRelMapper.selectByMktCampaignId(mktCampaignId);
        for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOList) {
            mktStrategyConfService.deleteMktStrategyConf(mktCamStrategyConfRelDO.getStrategyConfId());
        }
        // 删除活动基本信息
        mktCampaignMapper.deleteByPrimaryKey(mktCampaignId);


        Map<String, Object> maps = new HashMap<>();
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("mktCampaignId", mktCampaignId);
        return maps;
    }

    /**
     * 查询事件列表
     */
    @Override
    public Map<String, Object> qryMktCampaignListPage(String mktCampaignName, String statusCd, String tiggerType, String mktCampaignType, Integer page, Integer pageSize) {
        Map<String, Object> maps = new HashMap<>();
        MktCampaignDO MktCampaignPar = new MktCampaignDO();
        MktCampaignPar.setMktCampaignName(mktCampaignName);
        MktCampaignPar.setStatusCd(statusCd);
        MktCampaignPar.setTiggerType(tiggerType);
        MktCampaignPar.setMktCampaignType(mktCampaignType);
        PageHelper.startPage(page, pageSize);
        List<MktCampaignDO> mktCampaignDOList = mktCampaignMapper.qryMktCampaignListPage(MktCampaignPar);

        // 获取所有的sysParam
        Map<String, String> paramMap = new HashMap<>();
        List<SysParams> sysParamList = sysParamsMapper.selectAll("", 0L);
        for (SysParams sysParams : sysParamList) {
            paramMap.put(sysParams.getParamKey() + sysParams.getParamValue(), sysParams.getParamName());
        }

        List<MktCampaignVO> mktCampaignVOList = new ArrayList<>();
        for (MktCampaignDO mktCampaignDO : mktCampaignDOList) {
            MktCampaignVO mktCampaignVO = new MktCampaignVO();
            // 从活动关系表中获取
//            List<MktCampaignRelDO> mktCampaignRelDOList = mktCampaignRelMapper.selectByAmktCampaignId(mktCampaignDO.getMktCampaignId());
/*            String relType = null;
            if (mktCampaignRelDOList.size() > 0) {
                relType = mktCampaignRelDOList.get(0).getRelType();
                mktCampaignVO.setRelType(relType);
            }*/

            try {
                CopyPropertiesUtil.copyBean2Bean(mktCampaignVO, mktCampaignDO);
            } catch (Exception e) {
                logger.error("Excetion:", e);
            }
            mktCampaignVO.setTiggerTypeValue(paramMap.
                    get(ParamKeyEnum.TIGGER_TYPE.getParamKey() + mktCampaignDO.getTiggerType()));
            mktCampaignVO.setMktCampaignCategoryValue(paramMap.
                    get(ParamKeyEnum.MKT_CAMPAIGN_CATEGORY.getParamKey() + mktCampaignDO.getMktCampaignCategory()));
            mktCampaignVO.setMktCampaignTypeValue(paramMap.
                    get(ParamKeyEnum.MKT_CAMPAIGN_TYPE.getParamKey() + mktCampaignDO.getMktCampaignType()));
            mktCampaignVO.setStatusCdValue(paramMap.
                    get(ParamKeyEnum.STATUS_CD.getParamKey() + mktCampaignDO.getStatusCd()));
            mktCampaignVO.setExecTypeValue(paramMap.
                    get(ParamKeyEnum.EXEC_TYPE.getParamKey() + mktCampaignDO.getExecType()));

            // 获取活动关联的事件
            List<MktCamEvtRelDO> mktCamEvtRelDOList = mktCamEvtRelMapper.qryByMktCampaignId(mktCampaignDO.getMktCampaignId());
            if (mktCamEvtRelDOList != null) {
                List<EventDTO> eventDTOList = new ArrayList<>();
                for (MktCamEvtRelDO mktCamEvtRelDO : mktCamEvtRelDOList) {
                    Long eventId = mktCamEvtRelDO.getEventId();
                    ContactEvt contactEvt = contactEvtMapper.getEventById(eventId);
                    if (contactEvt != null) {
                        EventDTO eventDTO = new EventDTO();
                        eventDTO.setEventId(eventId);
                        eventDTO.setEventName(contactEvt.getContactEvtName());
                        eventDTOList.add(eventDTO);
                    }
                }
                mktCampaignVO.setEventDTOS(eventDTOList);
            }
            mktCampaignVOList.add(mktCampaignVO);
        }


        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("mktCampaigns", mktCampaignVOList);
        maps.put("pageInfo", new Page(new PageInfo(mktCampaignDOList)));
        return maps;
    }


    /**
     * 修改活动状态
     *
     * @param mktCampaignId
     * @param statusCd
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> changeMktCampaignStatus(Long mktCampaignId, String statusCd) throws Exception {
        Map<String, Object> maps = new HashMap<>();
        mktCampaignMapper.changeMktCampaignStatus(mktCampaignId, statusCd);
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

}