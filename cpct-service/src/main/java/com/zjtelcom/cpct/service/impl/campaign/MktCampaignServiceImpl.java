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
import com.zjtelcom.cpct.dto.campaign.*;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.EventDTO;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConf;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfDetail;
import com.zjtelcom.cpct.enums.AreaLeveL;
import com.zjtelcom.cpct.enums.ParamKeyEnum;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.campaign.MktCampaignService;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfService;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import com.zjtelcom.cpct.util.UserUtil;
import com.zjtelcom.cpct_prd.dao.MktCampaignPrdMapper;
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
     * 活动（同步生产）
     */
    @Autowired
    private MktCampaignPrdMapper mktCampaignPrdMapper;

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
/*
        String calcDisplay = "";
        String isaleDisplay = "";
        if (mktCampaignVO.getCalcDisplay() != null) {
            for (int i = 0; i < mktCampaignVO.getCalcDisplay().size(); i++) {
                if (i == 0) {
                    calcDisplay += mktCampaignVO.getCalcDisplay().get(i);
                } else {
                    calcDisplay = "/" + mktCampaignVO.getCalcDisplay().get(i);
                }
            }
            mktCampaignDO.setCalcDisplay(calcDisplay);
        }

        if (mktCampaignVO.getIsaleDisplay() != null) {
            for (int i = 0; i < mktCampaignVO.getIsaleDisplay().size(); i++) {
                if (i == 0) {
                    isaleDisplay += mktCampaignVO.getIsaleDisplay().get(i);
                } else {
                    isaleDisplay = "/" + mktCampaignVO.getIsaleDisplay().get(i);
                }
            }
            mktCampaignDO.setIsaleDisplay(isaleDisplay);
        }
*/

        // 创建活动基本信息
        mktCampaignDO.setCreateDate(new Date());
        mktCampaignDO.setCreateStaff(UserUtil.loginId());
        mktCampaignDO.setUpdateDate(new Date());
        mktCampaignDO.setUpdateStaff(UserUtil.loginId());
        mktCampaignDO.setStatusDate(new Date());
        mktCampaignDO.setCreateChannel("市场部");
        // TODO 添加所属地市
        // mktCampaignDO.setLanId();
        mktCampaignMapper.insert(mktCampaignDO);
        Long mktCampaignId = mktCampaignDO.getMktCampaignId();

        // 创建二次营销活动
/*
        if (mktCampaignVO.getPreMktCampaignId() != null && (mktCampaignVO.getPreMktCampaignId() != 0) {
            // 创建两个活动为接续关系
            MktCampaignRelDO mktCampaignRelDO = new MktCampaignRelDO();
            mktCampaignRelDO.setaMktCampaignId(mktCampaignVO.getPreMktCampaignId());
            mktCampaignRelDO.setzMktCampaignId(mktCampaignId);
            mktCampaignRelDO.setRelType("2000");  // 2000 -- 接续关系
            mktCampaignRelDO.setStatusCd("1000"); // 1000 -- 有效
            mktCampaignRelDO.setStatusDate(new Date());
            mktCampaignRelDO.setExpDate(mktCampaignVO.getPlanBeginTime());
            mktCampaignRelDO.setEffDate(mktCampaignVO.getPlanEndTime());
            mktCampaignRelDO.setCreateDate(new Date());
            mktCampaignRelDO.setCreateStaff(UserUtil.loginId());
            mktCampaignRelDO.setUpdateDate(new Date());
            mktCampaignRelDO.setUpdateStaff(UserUtil.loginId());
        }
*/

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
/*
        String calcDisplay = "";
        String isaleDisplay = "";
        if (mktCampaignVO.getCalcDisplay() != null) {
            for (int i = 0; i < mktCampaignVO.getCalcDisplay().size(); i++) {
                if (i == 0) {
                    calcDisplay += mktCampaignVO.getCalcDisplay().get(i);
                } else {
                    calcDisplay = "/" + mktCampaignVO.getCalcDisplay().get(i);
                }
            }
            mktCampaignDO.setCalcDisplay(calcDisplay);
        }

        if (mktCampaignVO.getIsaleDisplay() != null) {
            for (int i = 0; i < mktCampaignVO.getIsaleDisplay().size(); i++) {
                if (i == 0) {
                    isaleDisplay += mktCampaignVO.getIsaleDisplay().get(i);
                } else {
                    isaleDisplay = "/" + mktCampaignVO.getIsaleDisplay().get(i);
                }
            }
            mktCampaignDO.setIsaleDisplay(isaleDisplay);
        }
*/

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
/*        String relType = null;
        for (MktCampaignRelDO mktCampaignRelDO : mktCampaignRelDOList) {
            applyRegionIds.add(mktCampaignRelDO.getApplyRegionId());
            relType = mktCampaignRelDO.getRelType();
        }*/
        // 获取活动基本信息
        MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCampaignId);
        // 获取试运算展示列 和 isale展示列
/*
        List<Long> calcDisplayList = new ArrayList<>();
        List<Long> isaleDisplayList = new ArrayList<>();
        String calcDisplayString = mktCampaignDO.getCalcDisplay();
        String isaleDisplayString = mktCampaignDO.getIsaleDisplay();
        if (calcDisplayString != null && !"".equals(calcDisplayString)) {

            String[] calcDisplays = calcDisplayString.split("/");
            for (String calcDisplay : calcDisplays) {
                calcDisplayList.add(Long.valueOf(calcDisplay));
            }
        }
        if (isaleDisplayString != null && !"".equals(isaleDisplayString)) {
            String[] isaleDisplays = isaleDisplayString.split("/");
            for (String isaleDisplay : isaleDisplays) {
                isaleDisplayList.add(Long.valueOf(isaleDisplay));
            }
        }
*/
        MktCampaignVO mktCampaignVO = new MktCampaignVO();
        CopyPropertiesUtil.copyBean2Bean(mktCampaignVO, mktCampaignDO);
/*
        mktCampaignVO.setCalcDisplay(calcDisplayList);
        mktCampaignVO.setIsaleDisplay(isaleDisplayList);
*/

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
        // 删除关系
        // mktCampaignRelMapper.deleteByAmktCampaignId(mktCampaignId);

/*
// 删出活动和事件的关联
        mktCamEvtRelMapper.deleteByMktCampaignId(mktCampaignId);
        //删除活动下的策略以及规则
        List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOList = mktCamStrategyConfRelMapper.selectByMktCampaignId(mktCampaignId);
        for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOList) {
            mktStrategyConfService.deleteMktStrategyConf(mktCamStrategyConfRelDO.getStrategyConfId());
        }
 */
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
        List<MktCampaignDO> mktCampaignDOList = mktCampaignMapper.qryMktCampaignListPage(MktCampaignPar);
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
//        if (mktCampaignType==null || mktCampaignType.equals("")){
//            maps.put("resultCode", CODE_FAIL);
//            maps.put("resultMsg", "请选择事件分类");
//            return maps;
//        }
        MktCampaignPar.setMktCampaignType(mktCampaignType);
        List<Long> relationCamList = new ArrayList<>();
        if (eventId != null) {
            List<MktCamEvtRel> camEvtRelList = mktCamEvtRelMapper.qryBycontactEvtId(eventId);
            for (MktCamEvtRel rel : camEvtRelList) {
                relationCamList.add(rel.getMktCampaignId());
            }
        }
        List<MktCampaignDO> mktCampaignDOList = mktCampaignMapper.qryMktCampaignListPage(MktCampaignPar);
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
     * 查询活动列表（分页）
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
            Boolean isRelation = false;
            //判断该活动是否有有效的父/子活动
            int countA = mktCampaignRelMapper.selectCountByAmktCampaignId(mktCampaignDO.getMktCampaignId(), StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
            int countZ = mktCampaignRelMapper.selectCountByZmktCampaignId(mktCampaignDO.getMktCampaignId(), StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
            if (countA != 0 || countZ != 0) {
                isRelation = true;
            }
            mktCampaignVO.setRelation(isRelation);
            if(mktCampaignVO.getLanId()!=null){
                SysArea sysArea = (SysArea) redisUtils.get(mktCampaignVO.getLanId().toString());
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
    public Map<String, Object> changeMktCampaignStatus(Long mktCampaignId, String statusCd) throws Exception {
        Map<String, Object> maps = new HashMap<>();
        mktCampaignMapper.changeMktCampaignStatus(mktCampaignId, statusCd);
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
    public Map<String, Object> publishMktCampaign(Long mktCampaignId) throws Exception {
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
                childMktCamEvtRelDO.setCreateDate(new Date());
                childMktCamEvtRelDO.setCreateStaff(UserUtil.loginId());
                childMktCamEvtRelDO.setUpdateDate(new Date());
                childMktCamEvtRelDO.setCreateStaff(UserUtil.loginId());
                mktCamEvtRelMapper.insert(childMktCamEvtRelDO);
            }

            // 遍历活动下策略的集合
            for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOList) {
                Map<String, Object> mktStrategyConfMap = mktStrategyConfService.copyMktStrategyConf(mktCamStrategyConfRelDO.getStrategyConfId());
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
        }
        mktCampaignMap.put("childMktCampaignIdList", childMktCampaignIdList);
        return mktCampaignMap;
    }


}