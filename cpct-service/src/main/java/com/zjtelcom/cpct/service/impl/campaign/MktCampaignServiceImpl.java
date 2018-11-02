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
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.domain.system.SysStaff;
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
import com.zjtelcom.cpct.service.synchronize.campaign.SyncActivityService;
import com.zjtelcom.cpct.service.synchronize.campaign.SynchronizeCampaignService;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import com.zjtelcom.cpct.util.UserUtil;
import com.zjtelcom.cpct_prd.dao.campaign.MktCampaignPrdMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

/**
 * Description:
 * author: linchao
 * date: 2018/06/22 22:22
 * version: V1.0
 */
@Service
@Transactional
public class MktCampaignServiceImpl extends BaseService implements MktCampaignService {

    /**
     * 营销活动
     */
    @Autowired
    private MktCampaignMapper mktCampaignMapper;

    /**
     * 活动关联
     */
    @Autowired
    private MktCampaignRelMapper mktCampaignRelMapper;

    /**
     * 活动与事件关联
     */
    @Autowired
    private MktCamEvtRelMapper mktCamEvtRelMapper;
    /**
     * 系统参数
     */
    @Autowired
    private SysParamsMapper sysParamsMapper;
    /**
     * 事件
     */
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
     *
     */
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private MktCamDirectoryMapper mktCamDirectoryMapper;

    @Autowired
    private MktCamResultRelMapper mktCamResultRelMapper;

    @Autowired
    private SyncActivityService syncActivityService;

    @Autowired
    private SynchronizeCampaignService synchronizeCampaignService;

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
        mktCampaignDO.setCreateChannel("市场部");
        // TODO 添加所属地市
        mktCampaignDO.setLanId(8330000L);
        mktCampaignMapper.insert(mktCampaignDO);
        Long mktCampaignId = mktCampaignDO.getMktCampaignId();

        // 创建二次营销活动

        if (mktCampaignVO.getPreMktCampaignId() != null && (mktCampaignVO.getPreMktCampaignId() != 0)) {
            // 创建两个活动为接续关系
            MktCampaignRelDO mktCampaignRelDO = new MktCampaignRelDO();
            mktCampaignRelDO.setaMktCampaignId(mktCampaignVO.getPreMktCampaignId());
            mktCampaignRelDO.setzMktCampaignId(mktCampaignId);
            mktCampaignRelDO.setRelType("2000");  // 2000 -- 接续关系
            mktCampaignRelDO.setStatusCd(StatusCode.STATUS_CODE_NOTACTIVE.getStatusCode()); // 1000 -- 有效
            mktCampaignRelDO.setApplyRegionId(mktCampaignDO.getLanId());
            mktCampaignRelDO.setStatusDate(new Date());
            mktCampaignRelDO.setExpDate(mktCampaignVO.getPlanBeginTime());
            mktCampaignRelDO.setEffDate(mktCampaignVO.getPlanEndTime());
            mktCampaignRelDO.setCreateDate(new Date());
            mktCampaignRelDO.setCreateStaff(UserUtil.loginId());
            mktCampaignRelDO.setUpdateDate(new Date());
            mktCampaignRelDO.setUpdateStaff(UserUtil.loginId());
            mktCampaignRelMapper.insert(mktCampaignRelDO);
        }


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

        //创建活动与事件的关联
        for (EventDTO eventDTO : mktCampaignVO.getEventDTOS()) {
            MktCamEvtRelDO mktCamEvtRelDO = new MktCamEvtRelDO();
            mktCamEvtRelDO.setMktCampaignId(mktCampaignId);
            mktCamEvtRelDO.setEventId(eventDTO.getEventId());
            mktCamEvtRelDO.setCampaignSeq(0); // 默认等级为 0
            mktCamEvtRelDO.setLevelConfig(0); // 默认为资产级 0
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
        mktCampaignDO.setUpdateStaff(UserUtil.loginId());
        mktCampaignDO.setUpdateDate(new Date());
        mktCampaignMapper.updateByPrimaryKey(mktCampaignDO);
        Long mktCampaignId = mktCampaignDO.getMktCampaignId();

        //删除原来的活动与城市之间的关系
        mktCamCityRelMapper.deleteByMktCampaignId(mktCampaignId);
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

        // 遍历所有策略集合
        for (MktStrategyConfDetail mktStrategyConfDetail : mktCampaignVO.getMktStrategyConfDetailList()) {
            mktStrategyConfDetail.setMktCampaignId(mktCampaignVO.getMktCampaignId());
            if (mktStrategyConfDetail.getMktStrategyConfId() != null) {
                mktStrategyConfService.updateMktStrategyConf(mktStrategyConfDetail);
            } else {
                mktStrategyConfService.saveMktStrategyConf(mktStrategyConfDetail);
            }
        }

        List<MktCamEvtRelDO> mktCamEvtRelDOList = mktCamEvtRelMapper.selectByMktCampaignId(mktCampaignId);

        if (mktCampaignVO.getEventDTOS() != null) {
            List<EventDTO> comList = new ArrayList<>();
            List<MktCamEvtRelDO> delList = new ArrayList<>();
            for (int i = 0; i < mktCamEvtRelDOList.size(); i++) {
                for (int j = 0; j < mktCampaignVO.getEventDTOS().size(); j++) {
                    if (mktCampaignVO.getEventDTOS().get(j).getEventId().equals(mktCamEvtRelDOList.get(i).getEventId())) {
                        comList.add(mktCampaignVO.getEventDTOS().get(j));
                        break;
                    }
                    if (j == mktCampaignVO.getEventDTOS().size() - 1) {
                        delList.add(mktCamEvtRelDOList.get(i));
                    }
                }
            }

            // 删除去掉的关系
            for (MktCamEvtRelDO mktCamEvtRelDO : delList) {
                mktCamEvtRelMapper.deleteByPrimaryKey(mktCamEvtRelDO.getMktCampEvtRelId());
            }
            //
            mktCampaignVO.getEventDTOS().removeAll(comList);

            //创建活动与事件的关联
            for (EventDTO eventDTO : mktCampaignVO.getEventDTOS()) {
                MktCamEvtRelDO mktCamEvtRelDO = new MktCamEvtRelDO();
                mktCamEvtRelDO.setMktCampaignId(mktCampaignId);
                mktCamEvtRelDO.setEventId(eventDTO.getEventId());
                mktCamEvtRelDO.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
                mktCamEvtRelDO.setCampaignSeq(0); // 默认等级为 0
                mktCamEvtRelDO.setLevelConfig(0); // 默认为资产级 0
                mktCamEvtRelDO.setCreateStaff(UserUtil.loginId());
                mktCamEvtRelDO.setCreateDate(new Date());
                mktCamEvtRelDO.setUpdateStaff(UserUtil.loginId());
                mktCamEvtRelDO.setUpdateDate(new Date());
                mktCamEvtRelMapper.insert(mktCamEvtRelDO);
            }
        }
        Map<String, Object> maps = new HashMap<>();
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("mktCampaignId", mktCampaignId);
        return maps;
    }


    /**
     * 获取活动基本信息
     *
     * @param mktCampaignId
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> getMktCampaign(Long mktCampaignId) throws Exception {
        // 获取关系
        List<MktCampaignRelDO> mktCampaignRelDOList = mktCampaignRelMapper.selectByAmktCampaignId(mktCampaignId, StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
        List<CityProperty> applyRegionIds = new ArrayList<>();
        // 获取活动基本信息
        MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCampaignId);

        MktCampaignVO mktCampaignVO = new MktCampaignVO();
        CopyPropertiesUtil.copyBean2Bean(mktCampaignVO, mktCampaignDO);
        // 获取下发城市集合
        List<MktCamCityRelDO> mktCamCityRelDOList = mktCamCityRelMapper.selectByMktCampaignId(mktCampaignVO.getMktCampaignId());
        for (MktCamCityRelDO mktCamCityRelDO : mktCamCityRelDOList) {
            SysArea sysArea = sysAreaMapper.selectByPrimaryKey(mktCamCityRelDO.getCityId().intValue());
            CityProperty cityProperty = new CityProperty();
            cityProperty.setCityPropertyId(sysArea.getAreaId().longValue());
            cityProperty.setCityPropertyName(sysArea.getName());
            applyRegionIds.add(cityProperty);
        }
        mktCampaignVO.setApplyRegionIdList(applyRegionIds);

        MktCamDirectoryDO mktCamDirectoryDO = mktCamDirectoryMapper.selectByPrimaryKey(mktCampaignDO.getDirectoryId());
        if (mktCamDirectoryDO != null) {
            mktCampaignVO.setDirectoryName(mktCamDirectoryDO.getMktCamDirectoryName());
        }
        // 获取所有的sysParam
        Map<String, String> paramMap = new HashMap<>();
        List<SysParams> sysParamList = sysParamsMapper.selectAll("", "");
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
        List<MktCamEvtRelDO> mktCamEvtRelDOList = mktCamEvtRelMapper.selectByMktCampaignId(mktCampaignDO.getMktCampaignId());
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
        List<MktStrategyConfDetail> mktStrategyConfDetailList = new ArrayList<>();
        List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOList = mktCamStrategyConfRelMapper.selectByMktCampaignId(mktCampaignId);
        for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOList) {
            MktStrategyConfDO mktStrategyConfDO = mktStrategyConfMapper.selectByPrimaryKey(mktCamStrategyConfRelDO.getStrategyConfId());
            MktStrategyConf mktStrategyConf = new MktStrategyConf();

            Map<String, Object> mktStrategyConfMap = mktStrategyConfService.getMktStrategyConf(mktCamStrategyConfRelDO.getStrategyConfId());
            MktStrategyConfDetail mktStrategyConfDetail = (MktStrategyConfDetail) mktStrategyConfMap.get("mktStrategyConfDetail");
            mktStrategyConfDetailList.add(mktStrategyConfDetail);
        }
        mktCampaignVO.setMktStrategyConfDetailList(mktStrategyConfDetailList);

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
        // 删除活动基本信息
        mktCampaignMapper.deleteByPrimaryKey(mktCampaignId);
        Map<String, Object> maps = new HashMap<>();
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("mktCampaignId", mktCampaignId);
        return maps;
    }

    @Override
    public Map<String, Object> getCampaignList4EventScene(String mktCampaignName) {
        Map<String, Object> maps = new HashMap<>();
        MktCampaignDO MktCampaignPar = new MktCampaignDO();
        MktCampaignPar.setMktCampaignName(mktCampaignName);
        List<MktCampaignCountDO> mktCampaignDOList = mktCampaignMapper.qryMktCampaignListPage(MktCampaignPar);
        List<CampaignVO> voList = new ArrayList<>();
        for (MktCampaignDO campaignDO : mktCampaignDOList) {
            CampaignVO vo = ChannelUtil.map2CampaignVO(campaignDO);
            voList.add(vo);
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", voList);
        return maps;
    }

    @Override
    public Map<String, Object> getCampaignList(String mktCampaignName, String mktCampaignType, Long eventId) {
        Map<String, Object> maps = new HashMap<>();
        MktCampaignDO MktCampaignPar = new MktCampaignDO();
        MktCampaignPar.setMktCampaignName(mktCampaignName);
        MktCampaignPar.setMktCampaignType(mktCampaignType);
        List<Long> relationCamList = new ArrayList<>();
        if (eventId != null) {
            List<MktCamEvtRel> camEvtRelList = mktCamEvtRelMapper.qryBycontactEvtId(eventId);
            for (MktCamEvtRel rel : camEvtRelList) {
                relationCamList.add(rel.getMktCampaignId());
            }
        }
        List<MktCampaignCountDO> mktCampaignDOList = mktCampaignMapper.qryMktCampaignListPage(MktCampaignPar);
        List<CampaignVO> voList = new ArrayList<>();
        for (MktCampaignDO campaignDO : mktCampaignDOList) {
            if (relationCamList.contains(campaignDO.getMktCampaignId())) {
                continue;
            }
            CampaignVO vo = ChannelUtil.map2CampaignVO(campaignDO);
            voList.add(vo);
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", voList);
        return maps;
    }

    /**
     * 活动同步列表
     *
     * @param params
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Map<String, Object> qryMktCampaignList4Sync(Map<String, Object> params, Integer page, Integer pageSize) {
        Map<String, Object> maps = new HashMap<>();
        PageHelper.startPage(page, pageSize);

        List<MktCampaignCountDO> mktCampaignDOList = mktCampaignMapper.qryMktCampaignListPage4Sync(params);

        // 获取所有的sysParam
        Map<String, String> paramMap = new HashMap<>();
        List<SysParams> sysParamList = sysParamsMapper.selectAll("", "");
        for (SysParams sysParams : sysParamList) {
            paramMap.put(sysParams.getParamKey() + sysParams.getParamValue(), sysParams.getParamName());
        }

        List<MktCampaignVO> mktCampaignVOList = new ArrayList<>();
        for (MktCampaignCountDO mktCampaignCountDO : mktCampaignDOList) {
            MktCampaignVO mktCampaignVO = new MktCampaignVO();
            try {
                mktCampaignVO.setMktCampaignId(mktCampaignCountDO.getMktCampaignId());
                mktCampaignVO.setMktCampaignName(mktCampaignCountDO.getMktCampaignName());
                mktCampaignVO.setMktActivityNbr(mktCampaignCountDO.getMktActivityNbr());
                mktCampaignVO.setPlanEndTime(mktCampaignCountDO.getPlanEndTime());
                mktCampaignVO.setPlanBeginTime(mktCampaignCountDO.getPlanBeginTime());
                mktCampaignVO.setCreateChannel(mktCampaignCountDO.getCreateChannel());
                mktCampaignVO.setCreateDate(mktCampaignCountDO.getCreateDate());
                mktCampaignVO.setUpdateDate(mktCampaignCountDO.getUpdateDate());
                if (mktCampaignCountDO.getStatusCd().equals(StatusCode.STATUS_CODE_PUBLISHED.getStatusCode()) || mktCampaignCountDO.getStatusCd().equals(StatusCode.STATUS_CODE_PASS.getStatusCode())) {
                    mktCampaignVO.setStatusExamine(StatusCode.STATUS_CODE_PASS.getStatusMsg());
                } else if (mktCampaignCountDO.getStatusCd().equals(StatusCode.STATUS_CODE_UNPASS.getStatusCode())) {
                    mktCampaignVO.setStatusExamine(StatusCode.STATUS_CODE_UNPASS.getStatusMsg());
                } else {
                    mktCampaignVO.setStatusExamine(StatusCode.STATUS_CODE_CHECKING.getStatusMsg());
                }
            } catch (Exception e) {
                logger.error("Excetion:", e);
            }
            mktCampaignVO.setMktCampaignCategoryValue(paramMap.
                    get(ParamKeyEnum.MKT_CAMPAIGN_CATEGORY.getParamKey() + mktCampaignCountDO.getMktCampaignCategory()));
            mktCampaignVO.setMktCampaignTypeValue(paramMap.
                    get(ParamKeyEnum.MKT_CAMPAIGN_TYPE.getParamKey() + mktCampaignCountDO.getMktCampaignType()));
            mktCampaignVO.setStatusCdValue(paramMap.
                    get(ParamKeyEnum.STATUS_CD.getParamKey() + mktCampaignCountDO.getStatusCd()));
            Boolean isRelation = false;
            //判断该活动是否有有效的父/子活动
            if (mktCampaignCountDO.getRelCount() != 0) {
                isRelation = true;
            }
            mktCampaignVO.setRelation(isRelation);
            mktCampaignVOList.add(mktCampaignVO);
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("mktCampaigns", mktCampaignVOList);
        maps.put("pageInfo", new Page(new PageInfo(mktCampaignDOList)));
        return maps;
    }


    /**
     * 活动审核--同步列表
     *
     * @param campaignId
     * @return
     */
    @Override
    public Map<String, Object> examineCampaign4Sync(Long campaignId, String statusCd) {
        Map<String, Object> maps = new HashMap<>();
        MktCampaignDO campaignDO = mktCampaignMapper.selectByPrimaryKey(campaignId);
        if (campaignDO == null) {
            maps.put("resultCode", CODE_FAIL);
            maps.put("resultMsg", "活动不存在");
            return maps;
        }
        if (campaignDO.getStatusCd().equals(StatusCode.STATUS_CODE_PUBLISHED.getStatusCode()) || campaignDO.getStatusCd().equals(StatusCode.STATUS_CODE_PASS.getStatusCode())) {
            maps.put("resultCode", CODE_FAIL);
            maps.put("resultMsg", "非待审核活动");
            return maps;
        }
        campaignDO.setStatusCd(statusCd);
        campaignDO.setUpdateDate(new Date());
        mktCampaignMapper.updateByPrimaryKey(campaignDO);
        maps.put("resultCode", CODE_SUCCESS);
        if (statusCd.equals(StatusCode.STATUS_CODE_PASS.getStatusCode())) {
            maps.put("resultMsg", "已通过");
        } else {
            maps.put("resultMsg", "已拒绝");
        }
        return maps;
    }


    /**
     * 活动当前结束时间
     *
     * @param campaignId
     * @return
     */
    @Override
    public Map<String, Object> getCampaignEndTime4Sync(Long campaignId) {
        Map<String, Object> maps = new HashMap<>();
        MktCampaignDO campaignDO = mktCampaignMapper.selectByPrimaryKey(campaignId);
        if (campaignDO == null) {
            maps.put("resultCode", CODE_FAIL);
            maps.put("resultMsg", "活动不存在");
            return maps;
        }
        maps.put("resultCode", CODE_SUCCESS);
        maps.put("resultMsg", campaignDO.getPlanEndTime());
        return maps;
    }

    /**
     * 活动延期--同步列表
     *
     * @param campaignId
     * @param lastTime
     * @return
     */
    @Override
    public Map<String, Object> delayCampaign4Sync(Long campaignId, Date lastTime) {
        Map<String, Object> maps = new HashMap<>();
        MktCampaignDO campaignDO = mktCampaignMapper.selectByPrimaryKey(campaignId);
        if (campaignDO == null) {
            maps.put("resultCode", CODE_FAIL);
            maps.put("resultMsg", "活动不存在");
            return maps;
        }
        if (lastTime.before(campaignDO.getPlanEndTime())) {
            maps.put("resultCode", CODE_FAIL);
            maps.put("resultMsg", "时间只能后延");
            return maps;
        }
        List<MktStrategyConfDO> strategyConfList = mktStrategyConfMapper.selectByCampaignId(campaignId);
        for (MktStrategyConfDO strategy : strategyConfList) {
            strategy.setEndTime(lastTime);
            mktStrategyConfMapper.updateByPrimaryKey(strategy);
        }
        campaignDO.setPlanEndTime(lastTime);
        mktCampaignMapper.updateByPrimaryKey(campaignDO);
        maps.put("resultCode", CODE_SUCCESS);
        maps.put("resultMsg", "延期成功");
        return maps;
    }

    /**
     * 查询活动列表（分页）
     */
    @Override
    public Map<String, Object> qryMktCampaignListPage(Map<String, Object> params) {
        Map<String, Object> maps = new HashMap<>();
        MktCampaignDO mktCampaignDO = new MktCampaignDO();
        mktCampaignDO.setMktCampaignName(params.get("mktCampaignName").toString());  // 活动名称
        mktCampaignDO.setStatusCd(params.get("statusCd").toString());                 // 活动状态
        mktCampaignDO.setTiggerType(params.get("tiggerType").toString());             // 活动触发类型 - 实时，批量
        mktCampaignDO.setMktCampaignCategory(params.get("mktCampaignCategory").toString());  // 活动分类 - 框架，强制，自主
        mktCampaignDO.setMktCampaignType(params.get("mktCampaignType").toString());   // 活动类别 - 服务，营销，服务+营销
        List<Integer> landIdList = (List) params.get("landIds");
        if (landIdList.size() > 0 && !"".equals(landIdList.get(0))) {
            Long landId = Long.valueOf(landIdList.get(landIdList.size() - 1));
            mktCampaignDO.setLanId(landId);        // 所属地市
        }
        mktCampaignDO.setCreateChannel(params.get("createChannel").toString());       // 创建渠道
        PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("pageSize").toString())); // 分页
        List<MktCampaignCountDO> mktCampaignDOList = mktCampaignMapper.qryMktCampaignListPage(mktCampaignDO);

        // 获取所有的sysParam
        Map<String, String> paramMap = new HashMap<>();
        List<SysParams> sysParamList = sysParamsMapper.selectAll("", "");
        for (SysParams sysParams : sysParamList) {
            paramMap.put(sysParams.getParamKey() + sysParams.getParamValue(), sysParams.getParamName());
        }

        List<MktCampaignVO> mktCampaignVOList = new ArrayList<>();
        for (MktCampaignCountDO mktCampaignCountDO : mktCampaignDOList) {
            MktCampaignVO mktCampaignVO = new MktCampaignVO();
            try {
                mktCampaignVO.setMktCampaignId(mktCampaignCountDO.getMktCampaignId());
                mktCampaignVO.setMktCampaignName(mktCampaignCountDO.getMktCampaignName());
                mktCampaignVO.setMktActivityNbr(mktCampaignCountDO.getMktActivityNbr());
                mktCampaignVO.setPlanBeginTime(mktCampaignCountDO.getPlanBeginTime());
                mktCampaignVO.setPlanEndTime(mktCampaignCountDO.getPlanEndTime());
                mktCampaignVO.setCreateChannel(mktCampaignCountDO.getCreateChannel());
                mktCampaignVO.setCreateDate(mktCampaignCountDO.getCreateDate());
                mktCampaignVO.setPreMktCampaignId(mktCampaignCountDO.getPreMktCampaignId());
            } catch (Exception e) {
                logger.error("Excetion:", e);
            }
            mktCampaignVO.setMktCampaignCategoryValue(paramMap.
                    get(ParamKeyEnum.MKT_CAMPAIGN_CATEGORY.getParamKey() + mktCampaignCountDO.getMktCampaignCategory()));
            mktCampaignVO.setMktCampaignTypeValue(paramMap.
                    get(ParamKeyEnum.MKT_CAMPAIGN_TYPE.getParamKey() + mktCampaignCountDO.getMktCampaignType()));
            mktCampaignVO.setStatusCdValue(paramMap.
                    get(ParamKeyEnum.STATUS_CD.getParamKey() + mktCampaignCountDO.getStatusCd()));

            Boolean isRelation = false;
            //判断该活动是否有有效的父/子活动
            if (mktCampaignCountDO.getRelCount() != 0) {
                isRelation = true;
            }

            mktCampaignVO.setRelation(isRelation);
            if (mktCampaignCountDO.getLanId() != null) {
                SysArea sysArea = (SysArea) redisUtils.get("CITY_" + mktCampaignCountDO.getLanId().toString());
                mktCampaignVO.setLandName(sysArea.getName());
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
    public Map<String, Object> changeMktCampaignStatus(final Long mktCampaignId, String statusCd) throws Exception {
        Map<String, Object> maps = new HashMap<>();
        mktCampaignMapper.changeMktCampaignStatus(mktCampaignId, statusCd, new Date(), UserUtil.loginId());
        // 判断是否是发布活动, 是该状态生效
        if (StatusCode.STATUS_CODE_PUBLISHED.getStatusCode().equals(statusCd)) {
            MktCamResultRelDO mktCamResultRelDO = new MktCamResultRelDO();
            mktCamResultRelDO.setStatus(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
            mktCamResultRelDO.setMktCampaignId(mktCampaignId);
            mktCamResultRelDO.setUpdateDate(new Date());
            mktCamResultRelDO.setUpdateStaff(UserUtil.loginId());
            mktCamResultRelMapper.changeStatusByMktCampaignId(mktCamResultRelDO);
        } else if (StatusCode.STATUS_CODE_ROLL.getStatusCode().equals(statusCd) || StatusCode.STATUS_CODE_STOP.getStatusCode().equals(statusCd)) {
            // 暂停或者下线, 该状态为未生效
            MktCamResultRelDO mktCamResultRelDO = new MktCamResultRelDO();
            mktCamResultRelDO.setStatus(StatusCode.STATUS_CODE_NOTACTIVE.getStatusCode());
            mktCamResultRelDO.setMktCampaignId(mktCampaignId);
            mktCamResultRelDO.setUpdateDate(new Date());
            mktCamResultRelDO.setUpdateStaff(UserUtil.loginId());
            mktCamResultRelMapper.changeStatusByMktCampaignId(mktCamResultRelDO);
        } else if (StatusCode.STATUS_CODE_PASS.getStatusCode().equals(statusCd)) {
            // 审核通过后异步同步活动到生产环境
            new Thread() {
                @Override
                public void run() {
                    try {
                        SysStaff sysStaff = (SysStaff) SecurityUtils.getSubject().getPrincipal();
                        String roleName = "admin";
                        if (sysStaff != null) {
                            roleName = sysStaff.getRoleName();
                        }
                        synchronizeCampaignService.synchronizeCampaign(mktCampaignId, roleName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        }
        return maps;
    }


    /**
     * 发布并下发活动
     *
     * @param mktCampaignId
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> publishMktCampaign(final Long mktCampaignId) throws Exception {
        Map<String, Object> mktCampaignMap = new HashMap<>();
        // 获取当前活动信息
        MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCampaignId);
        // 获取当前活动标识
        Long parentMktCampaignId = mktCampaignDO.getMktCampaignId();
        // 获取活动下策略的集合
        List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOList = mktCamStrategyConfRelMapper.selectByMktCampaignId(parentMktCampaignId);
        // 获取生失效时间
        Date effDate = mktCampaignDO.getPlanBeginTime();
        Date expDate = mktCampaignDO.getPlanEndTime();
        // 获取活动与事件的关系
        List<MktCamEvtRelDO> MktCamEvtRelDOList = mktCamEvtRelMapper.selectByMktCampaignId(parentMktCampaignId);
        // 获取当前活动的下发城市集合
        List<MktCamCityRelDO> mktCamCityRelDOList = mktCamCityRelMapper.selectByMktCampaignId(parentMktCampaignId);
        List<Long> childMktCampaignIdList = new ArrayList<>();
        // 遍历活动下发城市集合
        for (MktCamCityRelDO mktCamCityRelDO : mktCamCityRelDOList) {
            // 为下发城市生成新的活动
            mktCampaignDO.setMktCampaignId(null);
            mktCampaignDO.setLanId(mktCamCityRelDO.getCityId()); // 本地网标识
            mktCampaignDO.setCreateDate(new Date());
            mktCampaignDO.setCreateStaff(UserUtil.loginId());
            mktCampaignDO.setUpdateDate(new Date());
            mktCampaignDO.setUpdateStaff(UserUtil.loginId());
            mktCampaignDO.setStatusCd(StatusCode.STATUS_CODE_DRAFT.getStatusCode());
            mktCampaignMapper.insert(mktCampaignDO);
            // 获取新的活动的Id
            Long childMktCampaignId = mktCampaignDO.getMktCampaignId();
            childMktCampaignIdList.add(childMktCampaignId);
            // 与父活动进行关联
            MktCampaignRelDO mktCampaignRelDO = new MktCampaignRelDO();
            mktCampaignRelDO.setaMktCampaignId(parentMktCampaignId);
            mktCampaignRelDO.setzMktCampaignId(childMktCampaignId);
            mktCampaignRelDO.setApplyRegionId(mktCamCityRelDO.getCityId());
            mktCampaignRelDO.setEffDate(effDate);
            mktCampaignRelDO.setExpDate(expDate);
            mktCampaignRelDO.setRelType("1000");   //  1000-父子关系
            mktCampaignRelDO.setCreateDate(new Date());
            mktCampaignRelDO.setCreateStaff(UserUtil.loginId());
            mktCampaignRelDO.setUpdateDate(new Date());
            mktCampaignRelDO.setCreateStaff(UserUtil.loginId());
            mktCampaignRelDO.setStatusCd(StatusCode.STATUS_CODE_NOTACTIVE.getStatusCode());  // 1000-有效
            mktCampaignRelDO.setStatusDate(new Date());
            mktCampaignRelMapper.insert(mktCampaignRelDO);

            //事件与新活动建立关联
            for (MktCamEvtRelDO mktCamEvtRelDO : MktCamEvtRelDOList) {
                MktCamEvtRelDO childMktCamEvtRelDO = new MktCamEvtRelDO();
                childMktCamEvtRelDO.setMktCampaignId(childMktCampaignId);
                childMktCamEvtRelDO.setEventId(childMktCamEvtRelDO.getEventId());
                childMktCamEvtRelDO.setStatusCd("1000");
                childMktCamEvtRelDO.setStatusDate(new Date());
                childMktCamEvtRelDO.setCreateDate(new Date());
                childMktCamEvtRelDO.setCreateStaff(UserUtil.loginId());
                childMktCamEvtRelDO.setUpdateDate(new Date());
                childMktCamEvtRelDO.setCreateStaff(UserUtil.loginId());
                mktCamEvtRelMapper.insert(childMktCamEvtRelDO);
            }

            // 遍历活动下策略的集合
            for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOList) {
                Map<String, Object> mktStrategyConfMap = mktStrategyConfService.copyMktStrategyConf(mktCamStrategyConfRelDO.getStrategyConfId(), true);
                Long childMktStrategyConfId = (Long) mktStrategyConfMap.get("childMktStrategyConfId");
                // 建立活动和策略的关系
                MktCamStrategyConfRelDO chaildMktCamStrategyConfRelDO = new MktCamStrategyConfRelDO();
                chaildMktCamStrategyConfRelDO.setMktCampaignId(childMktCampaignId);
                chaildMktCamStrategyConfRelDO.setStrategyConfId(childMktStrategyConfId);
//                chaildMktCamStrategyConfRelDO.setStatusCd("1000"); // 1000-有效
//                chaildMktCamStrategyConfRelDO.setStatusDate(new Date());
                chaildMktCamStrategyConfRelDO.setCreateDate(new Date());
                chaildMktCamStrategyConfRelDO.setCreateStaff(UserUtil.loginId());
                chaildMktCamStrategyConfRelDO.setUpdateDate(new Date());
                chaildMktCamStrategyConfRelDO.setUpdateStaff(UserUtil.loginId());
                mktCamStrategyConfRelMapper.insert(chaildMktCamStrategyConfRelDO);
            }
            //  发布活动时异步去同步大数据
            new Thread() {
                @Override
                public void run() {
                    syncActivityService.syncActivity(mktCampaignId);
                }
            }.start();
        }
        mktCampaignMap.put("childMktCampaignIdList", childMktCampaignIdList);
        return mktCampaignMap;
    }

    /**
     * 升级活动
     *
     * @param mktCampaignId
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> upgradeMktCampaign(Long mktCampaignId) throws Exception {
        Map<String, Object> upgradeMap = new HashMap<>();
        MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCampaignId);
        // 获取原活动Id
        Long parentsMktCampaignId = mktCampaignDO.getMktCampaignId();
        mktCampaignDO.setMktCampaignId(null);
        // 升级后为 服务+营销活动
        mktCampaignDO.setMktCampaignType("3000");
        mktCampaignDO.setStatusCd(StatusCode.STATUS_CODE_DRAFT.getStatusCode());
        mktCampaignDO.setStatusDate(new Date());
        mktCampaignDO.setCreateDate(new Date());
        mktCampaignDO.setCreateStaff(UserUtil.loginId());
        mktCampaignDO.setUpdateDate(new Date());
        mktCampaignDO.setUpdateStaff(UserUtil.loginId());
        mktCampaignMapper.insert(mktCampaignDO);
        Long childMktCampaignId = mktCampaignDO.getMktCampaignId();

        MktCampaignRelDO mktCampaignRelDO = new MktCampaignRelDO();
        // 设置2个活动的关系为升级关系
        mktCampaignRelDO.setRelType("3000");
        mktCampaignRelDO.setaMktCampaignId(parentsMktCampaignId);
        mktCampaignRelDO.setzMktCampaignId(childMktCampaignId);
        mktCampaignRelDO.setApplyRegionId(mktCampaignDO.getLanId());
        mktCampaignRelDO.setEffDate(mktCampaignDO.getPlanBeginTime());
        mktCampaignRelDO.setExpDate(mktCampaignDO.getPlanEndTime());
        mktCampaignRelDO.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
        mktCampaignRelDO.setStatusDate(new Date());
        mktCampaignRelDO.setCreateDate(new Date());
        mktCampaignRelDO.setCreateStaff(UserUtil.loginId());
        mktCampaignRelDO.setUpdateDate(new Date());
        mktCampaignRelDO.setUpdateStaff(UserUtil.loginId());
        mktCampaignRelMapper.insert(mktCampaignRelDO);

        // 获取事件
        List<MktCamEvtRelDO> mktCamEvtRelDOList = mktCamEvtRelMapper.selectByMktCampaignId(parentsMktCampaignId);
        for (MktCamEvtRelDO mktCamEvtRelDO : mktCamEvtRelDOList) {
            mktCamEvtRelDO.setMktCampEvtRelId(null);
            mktCamEvtRelDO.setMktCampaignId(childMktCampaignId);
            mktCamEvtRelMapper.insert(mktCamEvtRelDO);
        }

        // 下发地市
        List<MktCamCityRelDO> mktCamCityRelDOList = mktCamCityRelMapper.selectByMktCampaignId(parentsMktCampaignId);
        for (MktCamCityRelDO mktCamCityRelDO : mktCamCityRelDOList) {
            mktCamCityRelDO.setMktCamCityRelId(null);
            mktCamCityRelDO.setMktCampaignId(childMktCampaignId);
            mktCamCityRelMapper.insert(mktCamCityRelDO);
        }
        List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOList = mktCamStrategyConfRelMapper.selectByMktCampaignId(parentsMktCampaignId);
        for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOList) {
            Map<String, Object> map = mktStrategyConfService.copyMktStrategyConf(mktCamStrategyConfRelDO.getStrategyConfId(), false);
            Long childMktStrategyConfId = (Long) map.get("childMktStrategyConfId");
            MktCamStrategyConfRelDO childtCamStrRelDO = new MktCamStrategyConfRelDO();
            childtCamStrRelDO.setMktCampaignId(childMktCampaignId);
            childtCamStrRelDO.setStrategyConfId(childMktStrategyConfId);
            childtCamStrRelDO.setCreateDate(new Date());
            childtCamStrRelDO.setCreateStaff(UserUtil.loginId());
            childtCamStrRelDO.setUpdateDate(new Date());
            childtCamStrRelDO.setUpdateStaff(UserUtil.loginId());
            mktCamStrategyConfRelMapper.insert(childtCamStrRelDO);
        }
        upgradeMap.put("resultCode", CommonConstant.CODE_SUCCESS);
        upgradeMap.put("resultMsg", "success");
        upgradeMap.put("childMktCampaignId", childMktCampaignId);
        return upgradeMap;
    }
}