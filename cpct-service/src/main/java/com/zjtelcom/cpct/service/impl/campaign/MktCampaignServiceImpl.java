package com.zjtelcom.cpct.service.impl.campaign;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.channel.ObjMktCampaignRelMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.system.SysAreaMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.SysArea;
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.channel.ObjMktCampaignRel;
import com.zjtelcom.cpct.domain.channel.RequestInstRel;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.domain.system.SysStaff;
import com.zjtelcom.cpct.dto.campaign.CampaignVO;
import com.zjtelcom.cpct.dto.campaign.MktCamEvtRel;
import com.zjtelcom.cpct.dto.campaign.MktCampaignVO;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.EventDTO;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConf;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfDetail;
import com.zjtelcom.cpct.enums.*;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.campaign.MktCampaignService;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfService;
import com.zjtelcom.cpct.service.synchronize.campaign.SynchronizeCampaignService;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import com.zjtelcom.cpct.util.UserUtil;
import com.zjtelcom.cpct_offer.dao.inst.RequestInstRelMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;
import static com.zjtelcom.cpct.constants.CommonConstant.STATUSCD_EFFECTIVE;

/**
 * Description:
 * author: linchao
 * date: 2018/06/22 22:22
 * version: V1.0
 */
@Service
@Transactional
public class MktCampaignServiceImpl extends BaseService implements MktCampaignService {
    @Value("${sync.value}")
    private String value;


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
     * redis
     */
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private MktCamDirectoryMapper mktCamDirectoryMapper;

    @Autowired
    private MktCamResultRelMapper mktCamResultRelMapper;

    @Autowired
    private MktStrategyConfRuleMapper mktStrategyConfRuleMapper;

    @Autowired
    private SynchronizeCampaignService synchronizeCampaignService;

    @Autowired
    private ObjMktCampaignRelMapper objMktCampaignRelMapper;
    //需求涵id 跟活动关联关系
    @Autowired
    private RequestInstRelMapper requestInstRelMapper;

    private final static String createChannel = "cpcp0005";


    /**
     * 添加活动基本信息 并建立关系
     *
     * @param mktCampaignVO
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> createMktCampaign(MktCampaignVO mktCampaignVO) throws Exception {
        Map<String, Object> maps = null;
        try {
            MktCampaignDO mktCampaignDO = new MktCampaignDO();
            CopyPropertiesUtil.copyBean2Bean(mktCampaignDO, mktCampaignVO);
            // 创建活动基本信息
            mktCampaignDO.setCreateDate(new Date());
            mktCampaignDO.setCreateStaff(UserUtil.loginId());
            mktCampaignDO.setUpdateDate(new Date());
            mktCampaignDO.setUpdateStaff(UserUtil.loginId());
            mktCampaignDO.setStatusDate(new Date());
            //添加所属地市
            if(UserUtil.getUser()!=null){
                // 获取当前用户
                mktCampaignDO.setRegionId(UserUtil.getUser().getLanId());
                // 获取当前用户的岗位编码
                mktCampaignDO.setCreateChannel(UserUtil.getRoleCode());
            } else{
                mktCampaignDO.setLanId(UserUtil.loginId());
                mktCampaignDO.setCreateChannel(PostEnum.ADMIN.getPostCode());
            }

            mktCampaignDO.setServiceType(StatusCode.CUST_TYPE.getStatusCode()); // 1000 - 客账户类
            mktCampaignDO.setLanId(AreaCodeEnum.getLandIdByRegionId(mktCampaignDO.getRegionId()));
            mktCampaignMapper.insert(mktCampaignDO);
            Long mktCampaignId = mktCampaignDO.getMktCampaignId();
            // 活动编码
            mktCampaignDO.setMktActivityNbr("MKT" + String.format("%06d", mktCampaignId));
            mktCampaignMapper.updateByPrimaryKey(mktCampaignDO);
            // 创建二次营销活动

            if (mktCampaignVO.getPreMktCampaignId() != null && (mktCampaignVO.getPreMktCampaignId() != 0)) {
                // 创建两个活动为接续关系
                MktCampaignRelDO mktCampaignRelDO = new MktCampaignRelDO();
                mktCampaignRelDO.setaMktCampaignId(mktCampaignVO.getPreMktCampaignId());
                mktCampaignRelDO.setzMktCampaignId(mktCampaignId);
                mktCampaignRelDO.setRelType(StatusCode.SERIAL_RELATION.getStatusCode());  // 2000 -- 接续关系
                mktCampaignRelDO.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode()); // 1000 -- 有效
                mktCampaignRelDO.setApplyRegionId(mktCampaignDO.getRegionId());
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

            //需求涵id不为空添加与活动的关系
            if (mktCampaignVO.getRequestId()!=null){
                RequestInstRel requestInstRel = new RequestInstRel();
                requestInstRel.setRequestInfoId(mktCampaignVO.getRequestId());
                requestInstRel.setRequestObjId(mktCampaignId);
                requestInstRel.setStatusCd(STATUSCD_EFFECTIVE);
                requestInstRel.setStatusDate(new Date());
                requestInstRel.setUpdateDate(new Date());
                requestInstRel.setCreateStaff(UserUtil.loginId());
                requestInstRel.setCreateDate(new Date());
                requestInstRel.setRequestObjType("mkt");
                requestInstRelMapper.insert(requestInstRel);
            }

            maps = new HashMap<>();
            maps.put("resultCode", CommonConstant.CODE_SUCCESS);
            if (StatusCode.STATUS_CODE_DRAFT.getStatusCode().equals(mktCampaignVO.getStatusCd())) {
                maps.put("resultMsg", ErrorCode.SAVE_MKT_CAMPAIGN_SUCCESS.getErrorMsg());
            } else if (StatusCode.STATUS_CODE_CHECKING.getStatusCode().equals(mktCampaignVO.getStatusCd())) {
                maps.put("resultMsg", ErrorCode.STATUS_CODE_CHECKING_SUCCESS.getErrorMsg());
            }

            maps.put("mktCampaignId", mktCampaignId);
        } catch (Exception e) {
            logger.error("Expersion = ", e);
            maps.put("resultCode", CommonConstant.CODE_FAIL);
            if (StatusCode.STATUS_CODE_DRAFT.getStatusCode().equals(mktCampaignVO.getStatusCd())) {
                maps.put("resultMsg", ErrorCode.SAVE_MKT_CAMPAIGN_FAILURE.getErrorMsg());
            } else if (StatusCode.STATUS_CODE_CHECKING.getStatusCode().equals(mktCampaignVO.getStatusCd())) {
                maps.put("resultMsg", ErrorCode.STATUS_CODE_CHECKING_FAILURE.getErrorMsg());
            }
        }
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
        Map<String, Object> maps = new HashMap<>();
        try {
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
                mktStrategyConfDetail.setMktCampaignName(mktCampaignVO.getMktCampaignName());
                mktStrategyConfDetail.setMktCampaignType(mktCampaignVO.getMktCampaignType());
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
                if (mktCampaignVO.getEventDTOS().size() > 0) {
                    for (MktCamEvtRelDO mktCamEvtRelDO : delList) {
                        mktCamEvtRelMapper.deleteByPrimaryKey(mktCamEvtRelDO.getMktCampEvtRelId());
                    }
                } else {
                    for (MktCamEvtRelDO mktCamEvtRelDO : mktCamEvtRelDOList) {
                        mktCamEvtRelMapper.deleteByPrimaryKey(mktCamEvtRelDO.getMktCampEvtRelId());
                    }
                }
                if (mktCampaignVO.getEventDTOS() != null) {
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
            }
            maps.put("resultCode", CommonConstant.CODE_SUCCESS);
            if (StatusCode.STATUS_CODE_DRAFT.getStatusCode().equals(mktCampaignVO.getStatusCd())) {
                maps.put("resultMsg", ErrorCode.UPDATE_MKT_CAMPAIGN_SUCCESS.getErrorMsg());
            } else if (StatusCode.STATUS_CODE_CHECKING.getStatusCode().equals(mktCampaignVO.getStatusCd())) {
                maps.put("resultMsg", ErrorCode.STATUS_CODE_CHECKING_SUCCESS.getErrorMsg());
            }
            maps.put("mktCampaignId", mktCampaignId);
        } catch (Exception e) {
            logger.error("Expersion = ", e);
            maps.put("resultCode", CommonConstant.CODE_FAIL);
            if (StatusCode.STATUS_CODE_DRAFT.getStatusCode().equals(mktCampaignVO.getStatusCd())) {
                maps.put("resultMsg", ErrorCode.UPDATE_MKT_CAMPAIGN_FAILURE.getErrorMsg());
            } else if (StatusCode.STATUS_CODE_CHECKING.getStatusCode().equals(mktCampaignVO.getStatusCd())) {
                maps.put("resultMsg", ErrorCode.STATUS_CODE_CHECKING_FAILURE.getErrorMsg());
            }
        }
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
        maps.put("resultMsg", "查询活动成功");
        maps.put("mktCampaignVO", mktCampaignVO);
        return maps;
    }


    /**
     * 根据活动Id查询所有的策略和规则名称集合
     *
     * @param mktCampaignId
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> getAllConfRuleName(Long mktCampaignId) throws Exception {
        Map<String, Object> maps = null;
        try {
            maps = new HashMap<>();
            List<MktStrategyConfDO> mktStrategyConfDOS = mktStrategyConfMapper.selectByCampaignId(mktCampaignId);
            List<Map<String, Object>> strConfList = new ArrayList<>();
            for (MktStrategyConfDO mktStrategyConfDO : mktStrategyConfDOS) {
                List<MktStrategyConfRuleDO> mktStrategyConfRuleDOList = mktStrategyConfRuleMapper.selectByMktStrategyConfId(mktStrategyConfDO.getMktStrategyConfId());
                Map<String, Object> strMap = new HashMap<>();
                List<Map<String, Object>> ruleMapList = new ArrayList<>();
                for (MktStrategyConfRuleDO mktStrategyConfRuleDO : mktStrategyConfRuleDOList) {
                    Map<String, Object> ruleMap = new HashMap<>();
                    ruleMap.put("ruleId", mktStrategyConfRuleDO.getMktStrategyConfRuleId());
                    ruleMap.put("ruleName", mktStrategyConfRuleDO.getMktStrategyConfRuleName());
                    ruleMapList.add(ruleMap);
                }
                List<Long> checkboxGroup = new ArrayList<>();
                strMap.put("strConfId", mktStrategyConfDO.getMktStrategyConfId());
                strMap.put("strCofName", mktStrategyConfDO.getMktStrategyConfName());
                strMap.put("ruleMapList", ruleMapList);
                strMap.put("checkboxGroup", checkboxGroup);// 该字段为空集合，前端需要
                strConfList.add(strMap);
            }

            maps.put("resultCode", CommonConstant.CODE_SUCCESS);
            maps.put("resultMsg", "查询成功！");
            maps.put("strConfList", strConfList);
        } catch (Exception e) {
            maps.put("resultCode", CommonConstant.CODE_SUCCESS);
            maps.put("strConfList", "查询失败！");
        }
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
        Map<String, Object> maps = new HashMap<>();
        // 删除活动基本信息
        try {
            mktCampaignMapper.deleteByPrimaryKey(mktCampaignId);
            // 删除活动与事件的关系
            mktCamEvtRelMapper.deleteByMktCampaignId(mktCampaignId);
            maps.put("resultCode", CommonConstant.CODE_SUCCESS);
            maps.put("resultMsg", "删除成功！");
            maps.put("mktCampaignId", mktCampaignId);
        } catch (Exception e) {
            logger.error("[op:delMktCampaign] 删除活动mktCampaignId = {}失败！Exception = ", mktCampaignId, e);
            maps.put("resultCode", CommonConstant.CODE_FAIL);
            maps.put("resultMsg", "删除失败！");
            maps.put("mktCampaignId", mktCampaignId);
        }
        return maps;
    }

    @Override
    public Map<String, Object> getCampaignList4EventScene(String mktCampaignName) {
        Map<String, Object> maps = new HashMap<>();
        try {
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
        } catch (Exception e) {
            maps.put("resultCode", CommonConstant.CODE_FAIL);
            maps.put("resultCode", "查询失败！");
        }
        return maps;
    }

    @Override
    public Map<String, Object> getCampaignList(String mktCampaignName, String mktCampaignType, Long eventId) {
        Map<String, Object> maps = new HashMap<>();
        try {
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
        } catch (Exception e) {
            maps.put("resultCode", CommonConstant.CODE_FAIL);
        }
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
        try {
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
        } catch (Exception e) {
            maps.put("resultCode", CommonConstant.CODE_FAIL);
        }
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
        try {
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
        } catch (Exception e) {
            maps.put("resultCode", CODE_FAIL);
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
        Map<String, Object> maps = null;
        try {
            maps = new HashMap<>();
            MktCampaignDO campaignDO = mktCampaignMapper.selectByPrimaryKey(campaignId);
            if (campaignDO == null) {
                maps.put("resultCode", CODE_FAIL);
                maps.put("resultMsg", "活动不存在");
                return maps;
            }
            maps.put("resultCode", CODE_SUCCESS);
            maps.put("resultMsg", campaignDO.getPlanEndTime());
        } catch (Exception e) {
            maps.put("resultCode", CODE_FAIL);
        }
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
        try {
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
        } catch (Exception e) {
            maps.put("resultCode", CODE_FAIL);
            maps.put("resultMsg", "延期失败！");
        }
        return maps;
    }

    /**
     * 查询活动列表（分页）
     */
    @Override
    public Map<String, Object> qryMktCampaignListPage(Map<String, Object> params) {
        Map<String, Object> maps = new HashMap<>();
        try {
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
                    mktCampaignVO.setCreateChannel(PostEnum.getNameByCode(mktCampaignCountDO.getCreateChannel()));
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
                    if (sysArea != null) {
                        mktCampaignVO.setLandName(sysArea.getName());
                    }

                }
                mktCampaignVOList.add(mktCampaignVO);
            }
            maps.put("resultCode", CommonConstant.CODE_SUCCESS);
            maps.put("resultMsg", "查询活动列表成功！");
            maps.put("mktCampaigns", mktCampaignVOList);
            maps.put("pageInfo", new Page(new PageInfo(mktCampaignDOList)));
        } catch (NumberFormatException e) {
            maps.put("resultCode", CommonConstant.CODE_FAIL);
            maps.put("resultMsg", "查询活动列表失败！");
        }
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
        try {
            mktCampaignMapper.changeMktCampaignStatus(mktCampaignId, statusCd, new Date(), UserUtil.loginId());
            // 判断是否是发布活动, 是该状态生效
            if (StatusCode.STATUS_CODE_PUBLISHED.getStatusCode().equals(statusCd)) {
               /* MktCamResultRelDO mktCamResultRelDO = new MktCamResultRelDO();
                mktCamResultRelDO.setStatus(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
                mktCamResultRelDO.setMktCampaignId(mktCampaignId);
                mktCamResultRelDO.setUpdateDate(new Date());
                mktCamResultRelDO.setUpdateStaff(UserUtil.loginId());
                mktCamResultRelMapper.changeStatusByMktCampaignId(mktCamResultRelDO);*/

                List<MktCamResultRelDO> mktCamResultRelDOS = mktCamResultRelMapper.selectResultByMktCampaignId(mktCampaignId);
                for (MktCamResultRelDO mktCamResultRelDO:mktCamResultRelDOS) {
                    mktCamResultRelDO.setStatus(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
                    mktCamResultRelMapper.updateByPrimaryKey(mktCamResultRelDO);
                }

                // 发布活动异步同步活动到生产环境
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
            } else if (StatusCode.STATUS_CODE_ROLL.getStatusCode().equals(statusCd) || StatusCode.STATUS_CODE_STOP.getStatusCode().equals(statusCd)) {
                // 暂停或者下线, 该状态为未生效
                /*MktCamResultRelDO mktCamResultRelDO = new MktCamResultRelDO();
                mktCamResultRelDO.setStatus(StatusCode.STATUS_CODE_NOTACTIVE.getStatusCode());

                mktCamResultRelDO.setMktCampaignId(mktCampaignId);
                mktCamResultRelDO.setUpdateDate(new Date());
                mktCamResultRelDO.setUpdateStaff(UserUtil.loginId());
                mktCamResultRelMapper.changeStatusByMktCampaignId(mktCamResultRelDO);*/
                List<MktCamResultRelDO> mktCamResultRelDOS = mktCamResultRelMapper.selectResultByMktCampaignId(mktCampaignId);
                for (MktCamResultRelDO mktCamResultRelDO:mktCamResultRelDOS) {
                    mktCamResultRelDO.setStatus(StatusCode.STATUS_CODE_NOTACTIVE.getStatusCode());
                    mktCamResultRelMapper.updateByPrimaryKey(mktCamResultRelDO);
                }
            }

            if (StatusCode.STATUS_CODE_PUBLISHED.getStatusCode().equals(statusCd)) {
                maps.put("resultMsg", ErrorCode.STATUS_CODE_PUBLISHED_SUCCESS.getErrorMsg());
            } else if (StatusCode.STATUS_CODE_CHECKING.getStatusCode().equals(statusCd)) {
                maps.put("resultMsg", ErrorCode.STATUS_CODE_CHECKING_SUCCESS.getErrorMsg());
            } else if (StatusCode.STATUS_CODE_STOP.getStatusCode().equals(statusCd)) {
                maps.put("resultMsg", ErrorCode.STATUS_CODE_STOP_SUCCESS.getErrorMsg());
            } else if (StatusCode.STATUS_CODE_ROLL.getStatusCode().equals(statusCd)) {
                maps.put("resultMsg", ErrorCode.STATUS_CODE_ROLL_SUCCESS.getErrorMsg());
            }
            maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        } catch (Exception e) {
            logger.error("[op:changeMktCampaignStatus] 修改活动状态statusCd = {}失败,Exception = ", statusCd, e);
            if (StatusCode.STATUS_CODE_PUBLISHED.getStatusCode().equals(statusCd)) {
                maps.put("resultMsg", ErrorCode.STATUS_CODE_PUBLISHED_FAILURE.getErrorMsg());
            } else if (StatusCode.STATUS_CODE_CHECKING.getStatusCode().equals(statusCd)) {
                maps.put("resultMsg", ErrorCode.STATUS_CODE_CHECKING_FAILURE.getErrorMsg());
            } else if (StatusCode.STATUS_CODE_STOP.getStatusCode().equals(statusCd)) {
                maps.put("resultMsg", ErrorCode.STATUS_CODE_STOP_FAILURE.getErrorMsg());
            } else if (StatusCode.STATUS_CODE_ROLL.getStatusCode().equals(statusCd)) {
                maps.put("resultMsg", ErrorCode.STATUS_CODE_ROLL_FAILURE.getErrorMsg());
            }
            maps.put("resultCode", CommonConstant.CODE_FAIL);
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
        try {
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
                mktCampaignDO.setCreateChannel(createChannel);
                mktCampaignMapper.insert(mktCampaignDO);
                // 获取新的活动的Id
                Long childMktCampaignId = mktCampaignDO.getMktCampaignId();
                // 活动编码
                mktCampaignDO.setMktActivityNbr("MKT" + String.format("%06d", mktCampaignId));
                mktCampaignMapper.updateByPrimaryKey(mktCampaignDO);

                childMktCampaignIdList.add(childMktCampaignId);
                // 与父活动进行关联
                MktCampaignRelDO mktCampaignRelDO = new MktCampaignRelDO();
                mktCampaignRelDO.setaMktCampaignId(parentMktCampaignId);
                mktCampaignRelDO.setzMktCampaignId(childMktCampaignId);
                mktCampaignRelDO.setApplyRegionId(mktCamCityRelDO.getCityId());
                mktCampaignRelDO.setEffDate(effDate);
                mktCampaignRelDO.setExpDate(expDate);
                mktCampaignRelDO.setRelType(StatusCode.PARENT_CHILD_RELATION.getStatusCode());   //  1000-父子关系
                mktCampaignRelDO.setCreateDate(new Date());
                mktCampaignRelDO.setCreateStaff(UserUtil.loginId());
                mktCampaignRelDO.setUpdateDate(new Date());
                mktCampaignRelDO.setCreateStaff(UserUtil.loginId());
                mktCampaignRelDO.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());  // 1000-有效
                mktCampaignRelDO.setStatusDate(new Date());
                mktCampaignRelMapper.insert(mktCampaignRelDO);

                //事件与新活动建立关联
                for (MktCamEvtRelDO mktCamEvtRelDO : MktCamEvtRelDOList) {
                    MktCamEvtRelDO childMktCamEvtRelDO = new MktCamEvtRelDO();
                    childMktCamEvtRelDO.setMktCampaignId(childMktCampaignId);
                    childMktCamEvtRelDO.setEventId(childMktCamEvtRelDO.getEventId());
                    childMktCamEvtRelDO.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
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
                //  发布活动时异步去同步到生产
/*                if ("1".equals(value)) {
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                synchronizeCampaignService.synchronizeCampaign(mktCampaignId, "admin");
                            } catch (Exception e) {
                                logger.error("[op:publishMktCampaign] 发布活动 id = {} 时，同步到生产失败！Exception= ", mktCampaignId, e);
                            }
                        }
                    }.start();
                }*/

                // 协同中心活动信息同步
    /*            new Thread(){
                    @Override
                    public void run(){
                        Map<String, Object> map = iMktCampaignService.campaignPublishDetail();
                    }
                }*/


            }
            mktCampaignMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCampaignMap.put("resultMsg", "发布活动成功！");
            mktCampaignMap.put("childMktCampaignIdList", childMktCampaignIdList);
        } catch (Exception e) {
            mktCampaignMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktCampaignMap.put("resultMsg", "发布活动失败！");
        }
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
        try {
            MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCampaignId);
            // 获取原活动Id
            Long parentsMktCampaignId = mktCampaignDO.getMktCampaignId();
            mktCampaignDO.setMktCampaignId(null);
            // 升级后为 服务+营销活动
            mktCampaignDO.setMktCampaignType(StatusCode.SERVICE_SALES_CAMPAIGN.getStatusCode()); //6000 - 升级关系
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
            mktCampaignRelDO.setRelType(StatusCode.UPDATE_RELATION.getStatusCode()); //3000 - 升级关系
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
            upgradeMap.put("resultMsg", "升级活动成功！");
            upgradeMap.put("childMktCampaignId", childMktCampaignId);
        } catch (Exception e) {
            upgradeMap.put("resultCode", CommonConstant.CODE_FAIL);
            upgradeMap.put("resultMsg", "升级活动失败！");
        }
        return upgradeMap;
    }


    /**
     * 选择模板
     *
     * @param preMktCampaignId
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> getMktCampaignTemplate(Long preMktCampaignId) throws Exception {
        Map<String, Object> maps = new HashMap<>();
        try {
            // 获取关系
            List<MktCampaignRelDO> mktCampaignRelDOList = mktCampaignRelMapper.selectByAmktCampaignId(preMktCampaignId, StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
            List<CityProperty> applyRegionIds = new ArrayList<>();
            // 获取活动基本信息
            MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(preMktCampaignId);

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

            Map<String, Object> strategyTemplateMap = mktStrategyConfService.getStrategyTemplate(preMktCampaignId);
            List<MktStrategyConfDetail> mktStrategyConfDetailList = (List<MktStrategyConfDetail>) strategyTemplateMap.get("mktStrategyConfDetailList");

            mktCampaignVO.setMktStrategyConfDetailList(mktStrategyConfDetailList);

            maps.put("resultCode", CommonConstant.CODE_SUCCESS);
            maps.put("mktCampaignVO", mktCampaignVO);
        } catch (Exception e) {
            logger.error("[op:MktCampaignServiceImpl] failed to getMktCampaignTemplate by preMktCampaignId = {}, Exception = ", preMktCampaignId, e);
            maps.put("resultCode", CommonConstant.CODE_FAIL);
        }
        return maps;
    }

}