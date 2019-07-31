package com.zjtelcom.cpct.service.impl.campaign;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ctzj.smt.bss.sysmgr.model.common.SysmgrResultObject;
import com.ctzj.smt.bss.sysmgr.model.dataobject.SystemPost;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemPostDto;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemUserDto;
import com.ctzj.smt.bss.sysmgr.model.query.QrySystemPostReq;
import com.ctzj.smt.bss.sysmgr.privilege.service.dubbo.api.ISystemPostDubboService;
import com.ctzj.smt.bss.sysmgr.privilege.service.dubbo.api.ISystemUserDtoDubboService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.channel.DisplayColumnLabelMapper;
import com.zjtelcom.cpct.dao.channel.ObjMktCampaignRelMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.dao.filter.MktStrategyCloseRuleRelMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyFilterRuleRelMapper;
import com.zjtelcom.cpct.dao.system.SysAreaMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.SysArea;
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.strategy.MktStrategyCloseRuleRelDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyFilterRuleRelDO;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.domain.system.SysStaff;
import com.zjtelcom.cpct.dto.campaign.CampaignVO;
import com.zjtelcom.cpct.dto.campaign.MktCamEvtRel;
import com.zjtelcom.cpct.dto.campaign.MktCampaignDetailVO;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.EventDTO;
import com.zjtelcom.cpct.dto.grouping.TarGrp;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConf;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfDetail;
import com.zjtelcom.cpct.enums.*;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.campaign.MktCamDisplayColumnRelService;
import com.zjtelcom.cpct.service.campaign.MktCampaignService;
import com.zjtelcom.cpct.service.campaign.MktOperatorLogService;
import com.zjtelcom.cpct.service.channel.ProductService;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfService;
import com.zjtelcom.cpct.service.synchronize.campaign.SyncActivityService;
import com.zjtelcom.cpct.service.synchronize.campaign.SynchronizeCampaignService;
import com.zjtelcom.cpct.util.*;
import com.zjtelcom.cpct_offer.dao.inst.RequestInfoMapper;
import com.zjtelcom.cpct_offer.dao.inst.RequestInstRelMapper;
import com.zjtelcom.cpct_prd.dao.campaign.MktCamDisplayColumnRelPrdMapper;
import com.zjtelcom.cpct_prod.dao.offer.OfferProdMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.*;

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

    @Autowired
    private OfferProdMapper offerMapper;

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

    @Autowired(required = false)
    private SynchronizeCampaignService synchronizeCampaignService;

    @Autowired
    private ObjMktCampaignRelMapper objMktCampaignRelMapper;
    //需求涵id 跟活动关联关系
    @Autowired
    private RequestInstRelMapper requestInstRelMapper;

    @Autowired
    private MktCamItemMapper mktCamItemMapper;

    @Autowired
    private MktStrategyCloseRuleRelMapper mktStrategyCloseRuleRelMapper;


    /**
     * 过滤规则与策略关联 Mapper
     */
    @Autowired
    private MktStrategyFilterRuleRelMapper mktStrategyFilterRuleRelMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private MktOperatorLogService mktOperatorLogService;

    @Autowired
    private TarGrpMapper tarGrpMapper;

    @Autowired(required = false)
    private ISystemUserDtoDubboService iSystemUserDtoDubboService;

    @Autowired
    private SyncActivityService syncActivityService;


    @Autowired(required = false)
    private ISystemPostDubboService iSystemPostDubboService;

    private final static String createChannel = "cpcpcj0001";

    @Autowired
    private RequestInfoMapper requestInfoMapper;

    /**
     * 展示列
     */
    @Autowired
    private DisplayColumnLabelMapper displayColumnLabelMapper;

    /**
     * 活动与展示列关联
     */
    @Autowired
    private MktCamDisplayColumnRelMapper mktCamDisplayColumnRelMapper;

    @Autowired
    private MktCamDisplayColumnRelService mktCamDisplayColumnRelService;

    @Autowired
    private MktCamDisplayColumnRelPrdMapper mktCamDisplayColumnRelPrdMapper;

    @Autowired
    private MktCamChlConfAttrMapper mktCamChlConfAttrMapper;

    //指定下发地市人员的数据集合
    private final static String CITY_PUBLISH="CITY_PUBLISH";



    @Override
    public Map<String, Object> searchByCampaignId(Long campaignId) {
        Map<String,Object> result = new HashMap<>();
        Map<String,Object> strategyMap = new HashMap<>();
        List<MktStrategyConfDO> strategyConfList = mktStrategyConfMapper.selectByCampaignId(campaignId);
        for (MktStrategyConfDO strategyConfDO : strategyConfList){
            strategyMap.put("strategyId",strategyConfDO.getMktStrategyConfId());
            strategyMap.put("strategyName",strategyConfDO.getMktStrategyConfName());
            Map<String,Object> ruleMap = new HashMap<>();
            List<MktStrategyConfRuleDO> ruleDOList = mktStrategyConfRuleMapper.selectByMktStrategyConfId(strategyConfDO.getMktStrategyConfId());
            for (MktStrategyConfRuleDO ruleDO : ruleDOList){
                ruleMap.put("ruleId",ruleDO.getMktStrategyConfRuleId());
                ruleMap.put("ruleName",ruleDO.getMktStrategyConfRuleName());
                TarGrp tarGrp = tarGrpMapper.selectByPrimaryKey(ruleDO.getTarGrpId());
                Map<String,Object> tarMap = new HashMap<>();
                if (tarGrp!=null){
                    tarMap.put("tarGrpId",tarGrp.getTarGrpId());
                    ruleMap.put("tarGrp",tarMap);
                }
            }
            strategyMap.put("rule",ruleMap);
        }
        result.put("campaignId",campaignId);
        result.put("strategy",strategyMap);
        return result;
    }

    /**
     * 添加活动基本信息 并建立关系
     *
     * @param mktCampaignVO
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> createMktCampaign(MktCampaignDetailVO mktCampaignVO) throws Exception {
        Map<String, Object> maps = null;
        try {
            MktCampaignDO mktCampaignDO = BeanUtil.create(mktCampaignVO, new MktCampaignDO());
            // 创建活动基本信息
            mktCampaignDO.setCreateDate(new Date());
            mktCampaignDO.setCreateStaff(UserUtil.loginId());
            mktCampaignDO.setUpdateDate(new Date());
            mktCampaignDO.setUpdateStaff(UserUtil.loginId());
            mktCampaignDO.setStatusDate(new Date());
            //添加所属地市
            if (UserUtil.getUser() != null) {
                // 获取当前用户
                mktCampaignDO.setRegionId(UserUtil.getUser().getLanId());
                // 获取当前用户的岗位编码包含“cpcpch”
                SystemUserDto userDetail = UserUtil.getRoleCode();
                for (SystemPostDto role : userDetail.getSystemPostDtoList()) {
                    // 判断是否为超级管理员
                    if(role.getSysPostCode().contains(PostEnum.ADMIN.getPostCode())){
                        mktCampaignDO.setCreateChannel(role.getSysPostCode());
                        break;
                    } else if (role.getSysPostCode().contains("cpcpch")) {
                        mktCampaignDO.setCreateChannel(role.getSysPostCode());
                        continue;
                    }
                }
            } else{
                mktCampaignDO.setRegionId(AreaCodeEnum.ZHEJIAGN.getRegionId());
                mktCampaignDO.setCreateChannel(PostEnum.ADMIN.getPostCode());
            }

            mktCampaignDO.setServiceType(StatusCode.CUST_TYPE.getStatusCode()); // 1000 - 客账户类
            mktCampaignDO.setLanId(AreaCodeEnum.getLandIdByRegionId(mktCampaignDO.getRegionId()));
            mktCampaignMapper.insert(mktCampaignDO);
            Long mktCampaignId = mktCampaignDO.getMktCampaignId();
            // 活动编码
            mktCampaignDO.setMktActivityNbr("MKT" + String.format("%06d", mktCampaignId));
            mktCampaignDO.setInitId(mktCampaignId);
            mktCampaignMapper.updateByPrimaryKey(mktCampaignDO);
            // 记录活动操作
            mktOperatorLogService.addMktOperatorLog(mktCampaignDO.getMktCampaignName(), mktCampaignId, mktCampaignDO.getMktActivityNbr(), null, mktCampaignDO.getStatusCd(), UserUtil.loginId(), OperatorLogEnum.ADD.getOperatorValue());

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

            //更新推荐条目
            if (mktCampaignVO.getMktCamItemIdList()!=null && !mktCampaignVO.getMktCamItemIdList().isEmpty()){
                List<MktCamItem> mktCamItemList = mktCamItemMapper.selectByBatch(mktCampaignVO.getMktCamItemIdList());
                for (MktCamItem mktCamItem : mktCamItemList) {
                    mktCamItem.setMktCampaignId(mktCampaignId);
                    mktCamItemMapper.updateByPrimaryKey(mktCamItem);
                }
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
                List<RequestInstRel> requestInstRels = requestInstRelMapper.selectByRequestId(mktCampaignVO.getRequestId(),"offer");
                for (RequestInstRel request : requestInstRels){
                    Long offerId = request.getRequestObjId();
                    Offer offer = offerMapper.selectByPrimaryKey(Integer.valueOf(offerId.toString()));
                    if (offer==null){
                        continue;
                    }
                    ObjMktCampaignRel objMktCam = new ObjMktCampaignRel();
                    objMktCam.setRelType("1000");
                    objMktCam.setObjType("1000");
                    objMktCam.setObjId(offerId);
                    objMktCam.setMktCampaignId(mktCampaignId);
                    objMktCam.setStatusCd(STATUSCD_EFFECTIVE);
                    objMktCam.setStatusDate(new Date());
                    objMktCam.setUpdateDate(new Date());
                    objMktCam.setCreateStaff(UserUtil.loginId());
                    objMktCam.setCreateDate(new Date());
                    objMktCampaignRelMapper.insert(objMktCam);
                }
            }
            //保存活动与过滤规则关系
            if (mktCampaignVO.getFilterRuleIdList() != null && mktCampaignVO.getFilterRuleIdList().size() > 0) {
                for (Long closeRuleId : mktCampaignVO.getFilterRuleIdList()) {
                    MktStrategyFilterRuleRelDO mktStrategyFilterRuleRelDO = new MktStrategyFilterRuleRelDO();
                    mktStrategyFilterRuleRelDO.setRuleId(closeRuleId);
                    mktStrategyFilterRuleRelDO.setStrategyId(mktCampaignId); //表结构不能变更, 逻辑变更, 策略字段放活动Id
                    mktStrategyFilterRuleRelDO.setCreateStaff(UserUtil.loginId());
                    mktStrategyFilterRuleRelDO.setCreateDate(new Date());
                    mktStrategyFilterRuleRelDO.setUpdateStaff(UserUtil.loginId());
                    mktStrategyFilterRuleRelDO.setUpdateDate(new Date());
                    mktStrategyFilterRuleRelMapper.insert(mktStrategyFilterRuleRelDO);
                }
            }

            //保存活动与关单规则关系
            if (mktCampaignVO.getCloseRuleIdList() != null && mktCampaignVO.getCloseRuleIdList().size() > 0) {
                for (Long closeRuleId : mktCampaignVO.getCloseRuleIdList()) {
                    MktStrategyCloseRuleRelDO mktStrategyCloseRuleRelDO = new MktStrategyCloseRuleRelDO();
                    mktStrategyCloseRuleRelDO.setRuleId(closeRuleId);
                    mktStrategyCloseRuleRelDO.setStrategyId(mktCampaignId); //表结构不能变更, 逻辑变更, 策略字段放活动Id
                    mktStrategyCloseRuleRelDO.setCreateStaff(UserUtil.loginId());
                    mktStrategyCloseRuleRelDO.setCreateDate(new Date());
                    mktStrategyCloseRuleRelDO.setUpdateStaff(UserUtil.loginId());
                    mktStrategyCloseRuleRelDO.setUpdateDate(new Date());
                    mktStrategyCloseRuleRelMapper.insert(mktStrategyCloseRuleRelDO);
                }
            }

            //试运算展示列实例化
            if(mktCampaignVO.getCalcDisplay() != null) {
                List<DisplayColumnLabel> displayColumnLabelList = displayColumnLabelMapper.findListByDisplayId(mktCampaignVO.getCalcDisplay());
                for(DisplayColumnLabel displayColumnLabel : displayColumnLabelList) {
                    MktCamDisplayColumnRel mktCamDisplayColumnRel = new MktCamDisplayColumnRel();
                    mktCamDisplayColumnRel.setMktCampaignId(mktCampaignId);
                    mktCamDisplayColumnRel.setInjectionLabelId(displayColumnLabel.getInjectionLabelId());
                    mktCamDisplayColumnRel.setDisplayId(mktCampaignVO.getCalcDisplay());
                    mktCamDisplayColumnRel.setDisplayColumnType("1000");
                    mktCamDisplayColumnRel.setLabelDisplayType(displayColumnLabel.getLabelDisplayType());
                    mktCamDisplayColumnRel.setRemark(displayColumnLabel.getMessageType().toString());
                    mktCamDisplayColumnRel.setStatusCd(STATUSCD_EFFECTIVE);
                    mktCamDisplayColumnRel.setStatusDate(new Date());
                    mktCamDisplayColumnRel.setCreateStaff(UserUtil.loginId());
                    mktCamDisplayColumnRel.setCreateDate(new Date());
                    mktCamDisplayColumnRel.setUpdateStaff(UserUtil.loginId());
                    mktCamDisplayColumnRel.setUpdateDate(new Date());
                    mktCamDisplayColumnRelMapper.insert(mktCamDisplayColumnRel);
                }
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
    public Map<String, Object> modMktCampaign(MktCampaignDetailVO mktCampaignVO) throws Exception {
        Map<String, Object> maps = new HashMap<>();
        try {
            MktCampaignDO mktCampaignDO = new MktCampaignDO();
            CopyPropertiesUtil.copyBean2Bean(mktCampaignDO, mktCampaignVO);
            // 更新活动基本信息
            mktCampaignDO.setUpdateStaff(UserUtil.loginId());
            mktCampaignDO.setUpdateDate(new Date());
            mktCampaignMapper.updateByPrimaryKey(mktCampaignDO);
            Long mktCampaignId = mktCampaignDO.getMktCampaignId();
            MktCampaignDO campaign = mktCampaignMapper.selectByPrimaryKey(mktCampaignId);
            // 记录活动操作
            mktOperatorLogService.addMktOperatorLog(mktCampaignDO.getMktCampaignName(), mktCampaignId, mktCampaignDO.getMktActivityNbr(), campaign.getStatusCd(), mktCampaignDO.getStatusCd(), UserUtil.loginId(), OperatorLogEnum.UPDATE.getOperatorValue());

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

            //更新推荐条目
            if (mktCampaignVO.getMktCamItemIdList() !=null && !mktCampaignVO.getMktCamItemIdList().isEmpty()){
                List<MktCamItem> mktCamItemList = mktCamItemMapper.selectByBatch(mktCampaignVO.getMktCamItemIdList());
                for (MktCamItem mktCamItem : mktCamItemList) {
                    mktCamItem.setMktCampaignId(mktCampaignId);
                    mktCamItemMapper.updateByPrimaryKey(mktCamItem);
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

            //重建活动与过滤规则关系
            mktStrategyFilterRuleRelMapper.deleteByStrategyId(mktCampaignId);
            if (mktCampaignVO.getFilterRuleIdList() != null && mktCampaignVO.getFilterRuleIdList().size() > 0) {
                for (Long FilterRuleId : mktCampaignVO.getFilterRuleIdList()) {
                    MktStrategyFilterRuleRelDO mktStrategyFilterRuleRelDO = new MktStrategyFilterRuleRelDO();
                    mktStrategyFilterRuleRelDO.setRuleId(FilterRuleId);
                    mktStrategyFilterRuleRelDO.setStrategyId(mktCampaignId);
                    mktStrategyFilterRuleRelDO.setCreateStaff(UserUtil.loginId());
                    mktStrategyFilterRuleRelDO.setCreateDate(new Date());
                    mktStrategyFilterRuleRelDO.setUpdateStaff(UserUtil.loginId());
                    mktStrategyFilterRuleRelDO.setUpdateDate(new Date());
                    mktStrategyFilterRuleRelMapper.insert(mktStrategyFilterRuleRelDO);
                }
            }

            //重建活动与关单规则关系
            mktStrategyCloseRuleRelMapper.deleteByStrategyId(mktCampaignId);
            if (mktCampaignVO.getCloseRuleIdList() != null && mktCampaignVO.getCloseRuleIdList().size() > 0) {
                for (Long closeRuleId : mktCampaignVO.getCloseRuleIdList()) {
                    MktStrategyCloseRuleRelDO mktStrategyCloseRuleRelDO = new MktStrategyCloseRuleRelDO();
                    mktStrategyCloseRuleRelDO.setRuleId(closeRuleId);
                    mktStrategyCloseRuleRelDO.setStrategyId(mktCampaignId);
                    mktStrategyCloseRuleRelDO.setCreateStaff(UserUtil.loginId());
                    mktStrategyCloseRuleRelDO.setCreateDate(new Date());
                    mktStrategyCloseRuleRelDO.setUpdateStaff(UserUtil.loginId());
                    mktStrategyCloseRuleRelDO.setUpdateDate(new Date());
                    mktStrategyCloseRuleRelMapper.insert(mktStrategyCloseRuleRelDO);
                }
            }

            //重建展示列实例
            mktCamDisplayColumnRelMapper.deleteByMktCampaignId(mktCampaignId);
            if(mktCampaignVO.getCalcDisplay() != null) {
                List<DisplayColumnLabel> displayColumnLabelList = displayColumnLabelMapper.findListByDisplayId(mktCampaignVO.getCalcDisplay());
                for(DisplayColumnLabel displayColumnLabel : displayColumnLabelList) {
                    MktCamDisplayColumnRel mktCamDisplayColumnRel = new MktCamDisplayColumnRel();
                    mktCamDisplayColumnRel.setMktCampaignId(mktCampaignId);
                    mktCamDisplayColumnRel.setInjectionLabelId(displayColumnLabel.getInjectionLabelId());
                    mktCamDisplayColumnRel.setDisplayId(mktCampaignVO.getCalcDisplay());
                    mktCamDisplayColumnRel.setDisplayColumnType("1000");
                    mktCamDisplayColumnRel.setLabelDisplayType(displayColumnLabel.getLabelDisplayType());
                    mktCamDisplayColumnRel.setRemark(displayColumnLabel.getMessageType().toString());
                    mktCamDisplayColumnRel.setStatusCd(STATUSCD_EFFECTIVE);
                    mktCamDisplayColumnRel.setStatusDate(new Date());
                    mktCamDisplayColumnRel.setCreateStaff(UserUtil.loginId());
                    mktCamDisplayColumnRel.setCreateDate(new Date());
                    mktCamDisplayColumnRel.setUpdateStaff(UserUtil.loginId());
                    mktCamDisplayColumnRel.setUpdateDate(new Date());
                    mktCamDisplayColumnRelMapper.insert(mktCamDisplayColumnRel);
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

        MktCampaignDetailVO mktCampaignVO = new MktCampaignDetailVO();
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

        //查询父活动信息
        List<MktCampaignRelDO> mktCampaignRelDOS = mktCampaignRelMapper.selectByZmktCampaignId(mktCampaignId, "1000");
        if(mktCampaignRelDOS!=null && mktCampaignRelDOS.size()>0){
            MktCampaignDO mktCampaignDOPre = mktCampaignMapper.selectByPrimaryKey(Long.valueOf(mktCampaignRelDOS.get(0).getaMktCampaignId()));
            mktCampaignVO.setPreMktCampaignId(mktCampaignDOPre.getMktCampaignId());
            if("1000".equals(mktCampaignDOPre.getMktCampaignCategory())){
                mktCampaignVO.setPreMktCampaignType("框架活动");
            }else if("2000".equals(mktCampaignDOPre.getMktCampaignCategory())){
                mktCampaignVO.setPreMktCampaignType("强制活动");
            }else if("3000".equals(mktCampaignDOPre.getMktCampaignCategory())){
                mktCampaignVO.setPreMktCampaignType("自主活动");
            }
        }
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

        // 获取过滤规则集合
        List<Long> filterRuleIdList = mktStrategyFilterRuleRelMapper.selectByStrategyId(mktCampaignId);
        mktCampaignVO.setFilterRuleIdList(filterRuleIdList);

        // 获取关单规则集合
        List<Long> closeRuleIdList = mktStrategyCloseRuleRelMapper.selectByStrategyId(mktCampaignId);
        mktCampaignVO.setCloseRuleIdList(closeRuleIdList);

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

            MktCampaignDO mktCampaignDO =mktCampaignMapper.selectByPrimaryKey(mktCampaignId);

            // 查询initId为mktCampaignId且状态为调整中
            MktCampaignDO mktCampaignDOAdjust= mktCampaignMapper.selectPrimaryKeyByInitId(mktCampaignDO.getInitId(), StatusCode.STATUS_CODE_ADJUST.getStatusCode());
            // 回滚父活动
            if (mktCampaignDOAdjust != null) {
                changeMktCampaignStatus(mktCampaignDOAdjust.getMktCampaignId(), StatusCode.STATUS_CODE_PUBLISHED.getStatusCode());
            }

            // 记录活动操作
            mktOperatorLogService.addMktOperatorLog(mktCampaignDO.getMktCampaignName(), mktCampaignId, mktCampaignDO.getMktActivityNbr(), mktCampaignDO.getStatusCd(), OperatorLogEnum.DELETE.getOperatorValue(), UserUtil.loginId(), OperatorLogEnum.DELETE.getOperatorValue());

            mktCampaignMapper.deleteByPrimaryKey(mktCampaignId);
            List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOList = mktCamStrategyConfRelMapper.selectByMktCampaignId(mktCampaignId);
            for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOList) {
                mktStrategyConfService.deleteMktStrategyConf(mktCamStrategyConfRelDO.getStrategyConfId());
            }

            // 删除活动策略关系
            mktCamStrategyConfRelMapper.deleteByMktCampaignId(mktCampaignId);

            // 删除活动与事件的关系
            mktCamEvtRelMapper.deleteByMktCampaignId(mktCampaignId);
            // 删除活动与规则集合
            mktStrategyFilterRuleRelMapper.deleteByStrategyId(mktCampaignId);
            // 删除活动与关单规则集合
            mktStrategyCloseRuleRelMapper.deleteByStrategyId(mktCampaignId);
            // 删除活动与试运算展示列关系
            mktCamDisplayColumnRelMapper.deleteByMktCampaignId(mktCampaignId);

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

            List<MktCampaignDetailVO> mktCampaignVOList = new ArrayList<>();
            for (MktCampaignCountDO mktCampaignCountDO : mktCampaignDOList) {
                MktCampaignDetailVO mktCampaignVO = new MktCampaignDetailVO();
                try {
                    mktCampaignVO.setMktCampaignId(mktCampaignCountDO.getMktCampaignId());
                    mktCampaignVO.setMktCampaignName(mktCampaignCountDO.getMktCampaignName());
                    mktCampaignVO.setMktActivityNbr(mktCampaignCountDO.getMktActivityNbr());
                    mktCampaignVO.setPlanEndTime(mktCampaignCountDO.getPlanEndTime());
                    mktCampaignVO.setPlanBeginTime(mktCampaignCountDO.getPlanBeginTime());
                    mktCampaignVO.setCreateChannel(mktCampaignCountDO.getCreateChannel());
                    mktCampaignVO.setCreateDate(mktCampaignCountDO.getCreateDate());
                    mktCampaignVO.setUpdateDate(mktCampaignCountDO.getUpdateDate());
                    if (mktCampaignCountDO.getStatusCd().equals(StatusCode.STATUS_CODE_PUBLISHED.getStatusCode()) || mktCampaignCountDO.getStatusCd().equals(StatusCode.STATUS_CODE_PASS.getStatusCode()) || mktCampaignCountDO.getStatusCd().equals(StatusCode.STATUS_CODE_ROLL.getStatusCode())|| mktCampaignCountDO.getStatusCd().equals(StatusCode.STATUS_CODE_ADJUST.getStatusCode()) || mktCampaignCountDO.getStatusCd().equals(StatusCode.STATUS_CODE_STOP.getStatusCode()) || mktCampaignCountDO.getStatusCd().equals(StatusCode.STATUS_CODE_PRE_PAUSE.getStatusCode())) {
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
            if(!(StatusCode.STATUS_CODE_PUBLISHED.getStatusCode().equals(campaignDO.getStatusCd())
                    || StatusCode.STATUS_CODE_PRE_PAUSE.getStatusCode().equals(campaignDO.getStatusCd()))){
                maps.put("resultCode", CODE_FAIL);
                maps.put("resultMsg", "活动状态不为发布或者过期");
                return maps;
            }

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
            // 策略生失效时间延期
            List<MktStrategyConfDO> strategyConfList = mktStrategyConfMapper.selectByCampaignId(campaignId);
            for (MktStrategyConfDO strategy : strategyConfList) {
                strategy.setEndTime(lastTime);
                mktStrategyConfMapper.updateByPrimaryKey(strategy);
            }

            // 渠道生失效时间延期
            List<MktCamChlConfAttrDO> mktCamChlConfAttrDOList = mktCamChlConfAttrMapper.selectAttrEndDateByCampaignId(campaignId);
            for (MktCamChlConfAttrDO mktCamChlConfAttrDO:mktCamChlConfAttrDOList) {
                mktCamChlConfAttrDO.setAttrValue(String.valueOf(lastTime.getTime()));
            }
            mktCamChlConfAttrMapper.updateByPrimaryKeyBatch(mktCamChlConfAttrDOList);

            campaignDO.setPlanEndTime(lastTime);
            mktCampaignMapper.updateByPrimaryKey(campaignDO);
            maps.put("resultCode", CODE_SUCCESS);
            maps.put("resultMsg", "延期成功");
        } catch (Exception e) {
            logger.error("Excepiton = " + e);
            maps.put("resultCode", CODE_FAIL);
            maps.put("resultMsg", "延期失败！");
        }
        return maps;
    }

    /**
     * 查询活动列表（分页） -- 活动总览
     */
    @Override
    public Map<String, Object> qryMktCampaignListPage(Map<String, Object> params) {
        Map<String, Object> maps = new HashMap<>();
        try {
            MktCampaignDO mktCampaignDO = new MktCampaignDO();
            mktCampaignDO.setMktCampaignName(params.get("mktCampaignName").toString());  // 活动名称
            mktCampaignDO.setStatusCd("2002");                 // 活动状态
            mktCampaignDO.setTiggerType(params.get("tiggerType").toString());             // 活动触发类型 - 实时，批量
            mktCampaignDO.setMktCampaignCategory(params.get("mktCampaignCategory").toString());  // 活动分类 - 框架，强制，自主
            mktCampaignDO.setMktCampaignType(params.get("mktCampaignType").toString());   // 活动类别 - 服务，营销，服务+营销
            if(params.get("createStaff").toString()!=null && !"".equals(params.get("createStaff").toString())){
                mktCampaignDO.setCreateStaff(Long.valueOf(params.get("createStaff").toString()));  // 创建人
            }

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

            List<MktCampaignDetailVO> mktCampaignVOList = new ArrayList<>();
            for (MktCampaignCountDO mktCampaignCountDO : mktCampaignDOList) {
                MktCampaignDetailVO mktCampaignVO = new MktCampaignDetailVO();
                try {
                    mktCampaignVO.setMktCampaignId(mktCampaignCountDO.getMktCampaignId());
                    mktCampaignVO.setMktCampaignName(mktCampaignCountDO.getMktCampaignName());
                    mktCampaignVO.setMktActivityNbr(mktCampaignCountDO.getMktActivityNbr());
                    mktCampaignVO.setPlanBeginTime(mktCampaignCountDO.getPlanBeginTime());
                    mktCampaignVO.setPlanEndTime(mktCampaignCountDO.getPlanEndTime());

                    String postName = "";
                    try {
                        SystemPost systemPost = new SystemPost();
                        systemPost.setSysPostCode(mktCampaignCountDO.getCreateChannel());
                        logger.info("[op:qryMktCampaignListPage] SysPostCode = " + mktCampaignCountDO.getCreateChannel());
                        QrySystemPostReq qrySystemPostReq = new QrySystemPostReq();
                        qrySystemPostReq.setSystemPost(systemPost);
                        long before1 = System.currentTimeMillis();
                        SysmgrResultObject<com.ctzj.smt.bss.sysmgr.model.common.Page> pageSysmgrResultObject =
                                iSystemPostDubboService.qrySystemPostPage(new com.ctzj.smt.bss.sysmgr.model.common.Page(), qrySystemPostReq);
                        logger.info("iSystemPostDubboService.qrySystemPostPage 消耗时间：" + (System.currentTimeMillis() - before1) + " ms");
                        if(pageSysmgrResultObject!=null){
                            if( pageSysmgrResultObject.getResultObject()!=null){
                                List<SystemPost> dataList = (List<SystemPost>) pageSysmgrResultObject.getResultObject().getDataList();
                                if(dataList!=null){
                                    if(dataList.get(0)!=null){
                                        postName = dataList.get(0).getSysPostName();
                                        logger.info("--->>> 岗位信息：" + postName);
                                    }
                                }
                            }
                        }
                    }catch (Exception e){
                        logger.error("iSystemPostDubboService.qrySystemPostPage接口错误！Exception = " , e);
                    }
                    mktCampaignVO.setCreateChannelName(postName);
                    mktCampaignVO.setCreateDate(mktCampaignCountDO.getCreateDate());
                    mktCampaignVO.setPreMktCampaignId(mktCampaignCountDO.getPreMktCampaignId());
                    MktCampaignDO mktCampaignDOPre = mktCampaignMapper.selectByPrimaryKey(mktCampaignCountDO.getPreMktCampaignId());
                    if (mktCampaignDOPre != null) {
                        mktCampaignVO.setPreMktCampaignId(mktCampaignDOPre.getMktCampaignId());
                        if ("1000".equals(mktCampaignDOPre.getMktCampaignCategory())) {
                            mktCampaignVO.setPreMktCampaignType("框架活动");
                        } else if ("2000".equals(mktCampaignDOPre.getMktCampaignCategory())) {
                            mktCampaignVO.setPreMktCampaignType("强制活动");
                        } else if ("3000".equals(mktCampaignDOPre.getMktCampaignCategory())) {
                            mktCampaignVO.setPreMktCampaignType("自主活动");
                        }
                    }

                    // 获取创建人信息
                    long before2 = System.currentTimeMillis();
                    SysmgrResultObject<SystemUserDto> systemUserDtoSysmgrResultObject = iSystemUserDtoDubboService.qrySystemUserDto(mktCampaignCountDO.getCreateStaff(), new ArrayList<Long>());
                    logger.info(" iSystemUserDtoDubboService.qrySystemUserDto 消耗时间：" + (System.currentTimeMillis() - before2) + "ms");
                    if (systemUserDtoSysmgrResultObject != null) {
                        if (systemUserDtoSysmgrResultObject.getResultObject() != null) {
                            mktCampaignVO.setCreateStaffName(systemUserDtoSysmgrResultObject.getResultObject().getStaffName());
                            logger.info("--->>> 创建人信息：" + systemUserDtoSysmgrResultObject.getResultObject().getStaffName());
                        }
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
     * 查询活动列表（分页） -- 活动总览(没有发布状态的活动)
     */
    @Override
    public Map<String, Object> qryMktCampaignListPageForNoPublish(Map<String, Object> params) {
        Map<String, Object> maps = new HashMap<>();
        try {
            MktCampaignDO mktCampaignDO = new MktCampaignDO();
            mktCampaignDO.setMktCampaignName(params.get("mktCampaignName").toString());  // 活动名称
            mktCampaignDO.setStatusCd(params.get("statusCd").toString());                 // 活动状态
            mktCampaignDO.setTiggerType(params.get("tiggerType").toString());             // 活动触发类型 - 实时，批量
            mktCampaignDO.setMktCampaignCategory(params.get("mktCampaignCategory").toString());  // 活动分类 - 框架，强制，自主
            mktCampaignDO.setMktCampaignType(params.get("mktCampaignType").toString());   // 活动类别 - 服务，营销，服务+营销
            if(params.get("createStaff").toString()!=null && !"".equals(params.get("createStaff").toString())){
                mktCampaignDO.setCreateStaff(Long.valueOf(params.get("createStaff").toString()));  // 创建人
            }

            List<Integer> landIdList = (List) params.get("landIds");
            if (landIdList.size() > 0 && !"".equals(landIdList.get(0))) {
                Long landId = Long.valueOf(landIdList.get(landIdList.size() - 1));
                mktCampaignDO.setLanId(landId);        // 所属地市
            }
            mktCampaignDO.setCreateChannel(params.get("createChannel").toString());       // 创建渠道
            PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("pageSize").toString())); // 分页
            List<MktCampaignCountDO> mktCampaignDOList = mktCampaignMapper.qryMktCampaignListPageForNoPublish(mktCampaignDO);

            // 获取所有的sysParam
            Map<String, String> paramMap = new HashMap<>();
            List<SysParams> sysParamList = sysParamsMapper.selectAll("", "");
            for (SysParams sysParams : sysParamList) {
                paramMap.put(sysParams.getParamKey() + sysParams.getParamValue(), sysParams.getParamName());
            }

            List<MktCampaignDetailVO> mktCampaignVOList = new ArrayList<>();
            for (MktCampaignCountDO mktCampaignCountDO : mktCampaignDOList) {
                MktCampaignDetailVO mktCampaignVO = new MktCampaignDetailVO();
                try {
                    mktCampaignVO.setMktCampaignId(mktCampaignCountDO.getMktCampaignId());
                    mktCampaignVO.setMktCampaignName(mktCampaignCountDO.getMktCampaignName());
                    mktCampaignVO.setMktActivityNbr(mktCampaignCountDO.getMktActivityNbr());
                    mktCampaignVO.setPlanBeginTime(mktCampaignCountDO.getPlanBeginTime());
                    mktCampaignVO.setPlanEndTime(mktCampaignCountDO.getPlanEndTime());

                    String postName = "";
                    try {
                        SystemPost systemPost = new SystemPost();
                        systemPost.setSysPostCode(mktCampaignCountDO.getCreateChannel());
                        logger.info("[op:qryMktCampaignListPage] SysPostCode = " + mktCampaignCountDO.getCreateChannel());
                        QrySystemPostReq qrySystemPostReq = new QrySystemPostReq();
                        qrySystemPostReq.setSystemPost(systemPost);
                        long before1 = System.currentTimeMillis();
                        SysmgrResultObject<com.ctzj.smt.bss.sysmgr.model.common.Page> pageSysmgrResultObject =
                                iSystemPostDubboService.qrySystemPostPage(new com.ctzj.smt.bss.sysmgr.model.common.Page(), qrySystemPostReq);
                        logger.info("iSystemPostDubboService.qrySystemPostPage 消耗时间：" + (System.currentTimeMillis() - before1) + " ms");
                        if(pageSysmgrResultObject!=null){
                            if( pageSysmgrResultObject.getResultObject()!=null){
                                List<SystemPost> dataList = (List<SystemPost>) pageSysmgrResultObject.getResultObject().getDataList();
                                if(dataList!=null){
                                    if(dataList.get(0)!=null){
                                        postName = dataList.get(0).getSysPostName();
                                        logger.info("--->>> 岗位信息：" + postName);
                                    }
                                }
                            }
                        }
                    }catch (Exception e){
                        logger.error("iSystemPostDubboService.qrySystemPostPage接口错误！Exception = " , e);
                    }
                    mktCampaignVO.setCreateChannelName(postName);
                    mktCampaignVO.setCreateDate(mktCampaignCountDO.getCreateDate());
                    mktCampaignVO.setPreMktCampaignId(mktCampaignCountDO.getPreMktCampaignId());
                    MktCampaignDO mktCampaignDOPre = mktCampaignMapper.selectByPrimaryKey(mktCampaignCountDO.getPreMktCampaignId());
                    if (mktCampaignDOPre != null) {
                        mktCampaignVO.setPreMktCampaignId(mktCampaignDOPre.getMktCampaignId());
                        if ("1000".equals(mktCampaignDOPre.getMktCampaignCategory())) {
                            mktCampaignVO.setPreMktCampaignType("框架活动");
                        } else if ("2000".equals(mktCampaignDOPre.getMktCampaignCategory())) {
                            mktCampaignVO.setPreMktCampaignType("强制活动");
                        } else if ("3000".equals(mktCampaignDOPre.getMktCampaignCategory())) {
                            mktCampaignVO.setPreMktCampaignType("自主活动");
                        }
                    }

                    // 获取创建人信息
                    long before2 = System.currentTimeMillis();
                    SysmgrResultObject<SystemUserDto> systemUserDtoSysmgrResultObject = iSystemUserDtoDubboService.qrySystemUserDto(mktCampaignCountDO.getCreateStaff(), new ArrayList<Long>());
                    logger.info(" iSystemUserDtoDubboService.qrySystemUserDto 消耗时间：" + (System.currentTimeMillis() - before2) + "ms");
                    if (systemUserDtoSysmgrResultObject != null) {
                        if (systemUserDtoSysmgrResultObject.getResultObject() != null) {
                            mktCampaignVO.setCreateStaffName(systemUserDtoSysmgrResultObject.getResultObject().getStaffName());
                            logger.info("--->>> 创建人信息：" + systemUserDtoSysmgrResultObject.getResultObject().getStaffName());
                        }
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
                mktCampaignVO.setStatusCd(mktCampaignCountDO.getStatusCd());
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
     * 查询活动列表（分页） -- 活动总览(发布或者调整中状态的活动)
     */
    @Override
    public Map<String, Object> qryMktCampaignListPageForPublish(Map<String, Object> params) {
        Map<String, Object> maps = new HashMap<>();
        try {
            MktCampaignDO mktCampaignDO = new MktCampaignDO();
            mktCampaignDO.setMktCampaignName(params.get("mktCampaignName").toString());  // 活动名称
            mktCampaignDO.setStatusCd(params.get("statusCd").toString());                 // 活动状态
            mktCampaignDO.setTiggerType(params.get("tiggerType").toString());             // 活动触发类型 - 实时，批量
            mktCampaignDO.setMktCampaignCategory(params.get("mktCampaignCategory").toString());  // 活动分类 - 框架，强制，自主
            mktCampaignDO.setMktCampaignType(params.get("mktCampaignType").toString());   // 活动类别 - 服务，营销，服务+营销
            if(params.get("createStaff").toString()!=null && !"".equals(params.get("createStaff").toString())){
                mktCampaignDO.setCreateStaff(Long.valueOf(params.get("createStaff").toString()));  // 创建人
            }

            List<Integer> landIdList = (List) params.get("landIds");
            if (landIdList.size() > 0 && !"".equals(landIdList.get(0))) {
                Long landId = Long.valueOf(landIdList.get(landIdList.size() - 1));
                mktCampaignDO.setLanId(landId);        // 所属地市
            }
            mktCampaignDO.setCreateChannel(params.get("createChannel").toString());       // 创建渠道
            PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("pageSize").toString())); // 分页
            List<MktCampaignCountDO> mktCampaignDOList = mktCampaignMapper.qryMktCampaignListPageForPublish(mktCampaignDO);

            // 获取所有的sysParam
            Map<String, String> paramMap = new HashMap<>();
            List<SysParams> sysParamList = sysParamsMapper.selectAll("", "");
            for (SysParams sysParams : sysParamList) {
                paramMap.put(sysParams.getParamKey() + sysParams.getParamValue(), sysParams.getParamName());
            }

            List<MktCampaignDetailVO> mktCampaignVOList = new ArrayList<>();
            for (MktCampaignCountDO mktCampaignCountDO : mktCampaignDOList) {
                MktCampaignDetailVO mktCampaignVO = new MktCampaignDetailVO();
                try {
                    mktCampaignVO.setMktCampaignId(mktCampaignCountDO.getMktCampaignId());
                    mktCampaignVO.setMktCampaignName(mktCampaignCountDO.getMktCampaignName());
                    mktCampaignVO.setMktActivityNbr(mktCampaignCountDO.getMktActivityNbr());
                    mktCampaignVO.setPlanBeginTime(mktCampaignCountDO.getPlanBeginTime());
                    mktCampaignVO.setPlanEndTime(mktCampaignCountDO.getPlanEndTime());

                    String postName = "";
                    try {
                        SystemPost systemPost = new SystemPost();
                        systemPost.setSysPostCode(mktCampaignCountDO.getCreateChannel());
                        logger.info("[op:qryMktCampaignListPage] SysPostCode = " + mktCampaignCountDO.getCreateChannel());
                        QrySystemPostReq qrySystemPostReq = new QrySystemPostReq();
                        qrySystemPostReq.setSystemPost(systemPost);
                        long before1 = System.currentTimeMillis();
                        SysmgrResultObject<com.ctzj.smt.bss.sysmgr.model.common.Page> pageSysmgrResultObject =
                                iSystemPostDubboService.qrySystemPostPage(new com.ctzj.smt.bss.sysmgr.model.common.Page(), qrySystemPostReq);
                        logger.info("iSystemPostDubboService.qrySystemPostPage 消耗时间：" + (System.currentTimeMillis() - before1) + " ms");
                        if(pageSysmgrResultObject!=null){
                            if( pageSysmgrResultObject.getResultObject()!=null){
                                List<SystemPost> dataList = (List<SystemPost>) pageSysmgrResultObject.getResultObject().getDataList();
                                if(dataList!=null){
                                    if(dataList.get(0)!=null){
                                        postName = dataList.get(0).getSysPostName();
                                        logger.info("--->>> 岗位信息：" + postName);
                                    }
                                }
                            }
                        }
                    }catch (Exception e){
                        logger.error("iSystemPostDubboService.qrySystemPostPage接口错误！Exception = " , e);
                    }
                    mktCampaignVO.setCreateChannelName(postName);
                    mktCampaignVO.setCreateDate(mktCampaignCountDO.getCreateDate());
                    mktCampaignVO.setPreMktCampaignId(mktCampaignCountDO.getPreMktCampaignId());
                    MktCampaignDO mktCampaignDOPre = mktCampaignMapper.selectByPrimaryKey(mktCampaignCountDO.getPreMktCampaignId());
                    if (mktCampaignDOPre != null) {
                        mktCampaignVO.setPreMktCampaignId(mktCampaignDOPre.getMktCampaignId());
                        if ("1000".equals(mktCampaignDOPre.getMktCampaignCategory())) {
                            mktCampaignVO.setPreMktCampaignType("框架活动");
                        } else if ("2000".equals(mktCampaignDOPre.getMktCampaignCategory())) {
                            mktCampaignVO.setPreMktCampaignType("强制活动");
                        } else if ("3000".equals(mktCampaignDOPre.getMktCampaignCategory())) {
                            mktCampaignVO.setPreMktCampaignType("自主活动");
                        }
                    }

                    // 获取创建人信息
                    long before2 = System.currentTimeMillis();
                    SysmgrResultObject<SystemUserDto> systemUserDtoSysmgrResultObject = iSystemUserDtoDubboService.qrySystemUserDto(mktCampaignCountDO.getCreateStaff(), new ArrayList<Long>());
                    logger.info(" iSystemUserDtoDubboService.qrySystemUserDto 消耗时间：" + (System.currentTimeMillis() - before2) + "ms");
                    if (systemUserDtoSysmgrResultObject != null) {
                        if (systemUserDtoSysmgrResultObject.getResultObject() != null) {
                            mktCampaignVO.setCreateStaffName(systemUserDtoSysmgrResultObject.getResultObject().getStaffName());
                            logger.info("--->>> 创建人信息：" + systemUserDtoSysmgrResultObject.getResultObject().getStaffName());
                        }
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
            MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCampaignId);
            String oldStatusCd = mktCampaignDO.getStatusCd();
            Long initId = mktCampaignDO.getInitId();
            // 记录活动操作
            mktOperatorLogService.addMktOperatorLog(mktCampaignDO.getMktCampaignName(), mktCampaignId, mktCampaignDO.getMktActivityNbr(), mktCampaignDO.getStatusCd(), statusCd, UserUtil.loginId(), statusCd);

            mktCampaignMapper.changeMktCampaignStatus(mktCampaignId, statusCd, new Date(), UserUtil.loginId());
            // 判断是否是发布活动, 是该状态生效
            if (StatusCode.STATUS_CODE_PUBLISHED.getStatusCode().equals(statusCd) || StatusCode.STATUS_CODE_ROLL.getStatusCode().equals(statusCd)) {
                List<MktCamResultRelDO> mktCamResultRelDOS = mktCamResultRelMapper.selectResultByMktCampaignId(mktCampaignId);
                for (MktCamResultRelDO mktCamResultRelDO:mktCamResultRelDOS) {
                    mktCamResultRelDO.setStatus(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
                    if(StatusCode.STATUS_CODE_STOP.getStatusCode().equals(statusCd)){
                        mktCamResultRelMapper.updateByPrimaryKey(mktCamResultRelDO);
                    }

                }

                // 发布调整的活动，下线源活动
                if(StatusCode.STATUS_CODE_PUBLISHED.getStatusCode().equals(statusCd)
                        && !mktCampaignId.equals(initId)){
                    // 查询initId为mktCampaignId且状态为调整中
                    MktCampaignDO mktCampaignDOAdjust= mktCampaignMapper.selectPrimaryKeyByInitId(initId, StatusCode.STATUS_CODE_ADJUST.getStatusCode());
                    changeMktCampaignStatus(mktCampaignDOAdjust.getMktCampaignId(), StatusCode.STATUS_CODE_ROLL.getStatusCode());

                }
                if(StatusCode.STATUS_CODE_ROLL.getStatusCode().equals(statusCd)){
                    // 活动下线清缓存
                    redisUtils.del("MKT_CAMPAIGN_" + mktCampaignId);
                    // 删除下线活动与事件的关系
                    mktCamEvtRelMapper.deleteByMktCampaignId(mktCampaignId);
                }

                // 删除准生产的redis缓存
                synchronizeCampaignService.deleteCampaignRedisPre(mktCampaignId);
                if (SystemParamsUtil.isCampaignSync()) {
                    // 发布活动异步同步活动到生产环境
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                String roleName = "admin";
                                //synchronizeCampaignService.synchronizeCampaign(mktCampaignId, roleName);
                                // 删除生产redis缓存
                                synchronizeCampaignService.deleteCampaignRedisProd(mktCampaignId);
                            } catch (Exception e) {
                                e.printStackTrace();
                                logger.error("[op:MktCampaignServiceImpl] 活动同步失败 by mktCampaignId = {}, Expection = ",mktCampaignId, e);
                            }
                        }
                    }.start();
                }
            } else if(StatusCode.STATUS_CODE_STOP.getStatusCode().equals(statusCd)){
                // 活动下线清缓存
                redisUtils.del("MKT_CAMPAIGN_" + mktCampaignId);
                if(StatusCode.STATUS_CODE_STOP.getStatusCode().equals(statusCd)){
                    List<MktCamResultRelDO> mktCamResultRelDOS = mktCamResultRelMapper.selectResultByMktCampaignId(mktCampaignId);
                    for (MktCamResultRelDO mktCamResultRelDO:mktCamResultRelDOS) {
                        mktCamResultRelDO.setStatus(StatusCode.STATUS_CODE_NOTACTIVE.getStatusCode());
                        mktCamResultRelMapper.updateByPrimaryKey(mktCamResultRelDO);
                    }
                }
            } else if(StatusCode.STATUS_CODE_ROLL_BACK.getStatusCode().equals(statusCd)){
                // 查询initId为mktCampaignId且状态为调整待发布
                MktCampaignDO mktCampaignDONew = mktCampaignMapper.selectByInitForRollBack(mktCampaignDO.getInitId());
                // 删除“调整待发布”活动,并保证不能删除源活动
                if (mktCampaignDONew != null && mktCampaignDONew.getMktCampaignId()!= mktCampaignDONew.getInitId()) {
                    delMktCampaign(mktCampaignDONew.getMktCampaignId());
                }
                // 回滚活动, 改变原来状态为发布
                changeMktCampaignStatus(mktCampaignId, StatusCode.STATUS_CODE_PUBLISHED.getStatusCode());
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
            // 获取当前活动名称
            String parentMktCampaignName = mktCampaignDO.getMktCampaignName();
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
                if (!mktCamCityRelDOList.isEmpty()) {
                    mktCampaignDO.setMktCampaignId(null);
                    // 活动名称加上地市
                    String areaName = AreaNameEnum.getNameByLandId(mktCamCityRelDO.getCityId());
                    mktCampaignDO.setMktCampaignName(parentMktCampaignName + "-" + areaName );
                    mktCampaignDO.setMktCampaignCategory(StatusCode.AUTONOMICK_CAMPAIGN.getStatusCode()); // 子活动默认为自主活动
                    mktCampaignDO.setLanId(mktCamCityRelDO.getCityId()); // 本地网标识
                    mktCampaignDO.setRegionId(AreaCodeEnum.getRegionIdByLandId(mktCamCityRelDO.getCityId()));
                    mktCampaignDO.setCreateDate(new Date());
                    mktCampaignDO.setUpdateDate(new Date());
                    mktCampaignDO.setUpdateStaff(UserUtil.loginId());
                    mktCampaignDO.setStatusCd(StatusCode.STATUS_CODE_DRAFT.getStatusCode());
                    mktCampaignDO.setCreateChannel(createChannel);
                    mktCampaignMapper.insert(mktCampaignDO);
                    // 获取新的活动的Id
                    Long childMktCampaignId = mktCampaignDO.getMktCampaignId();
                    // 活动编码
                    mktCampaignDO.setMktActivityNbr("MKT" + String.format("%06d", childMktCampaignId));
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
                        childMktCamEvtRelDO.setEventId(mktCamEvtRelDO.getEventId());
                        childMktCamEvtRelDO.setCampaignSeq(mktCamEvtRelDO.getCampaignSeq());
                        childMktCamEvtRelDO.setLevelConfig(mktCamEvtRelDO.getLevelConfig());
                        childMktCamEvtRelDO.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
                        childMktCamEvtRelDO.setStatusDate(new Date());
                        childMktCamEvtRelDO.setCreateDate(new Date());
                        childMktCamEvtRelDO.setCreateStaff(UserUtil.loginId());
                        childMktCamEvtRelDO.setUpdateDate(new Date());
                        childMktCamEvtRelDO.setCreateStaff(UserUtil.loginId());
                        mktCamEvtRelMapper.insert(childMktCamEvtRelDO);
                    }

                    // 推荐条目下发
                    productService.copyItemByCampaignPublish(parentMktCampaignId, childMktCampaignId, mktCampaignDO.getMktCampaignCategory());

                    // 试运算展示列实例化
                    mktCamDisplayColumnRelService.copyDisplayLabelByCamId(parentMktCampaignId, childMktCampaignId);

                    // 遍历活动下策略的集合
                    for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOList) {
                        Map<String, Object> mktStrategyConfMap = mktStrategyConfService.copyMktStrategyConf(mktCamStrategyConfRelDO.getStrategyConfId(), parentMktCampaignId, childMktCampaignId, true, mktCampaignDO.getLanId());
                        Long childMktStrategyConfId = (Long) mktStrategyConfMap.get("childMktStrategyConfId");
                        // 建立活动和策略的关系
                        MktCamStrategyConfRelDO chaildMktCamStrategyConfRelDO = new MktCamStrategyConfRelDO();
                        chaildMktCamStrategyConfRelDO.setMktCampaignId(childMktCampaignId);
                        chaildMktCamStrategyConfRelDO.setStrategyConfId(childMktStrategyConfId);
                        chaildMktCamStrategyConfRelDO.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode()); // 1000-有效
                        chaildMktCamStrategyConfRelDO.setStatusDate(new Date());
                        chaildMktCamStrategyConfRelDO.setCreateDate(new Date());
                        chaildMktCamStrategyConfRelDO.setCreateStaff(UserUtil.loginId());
                        chaildMktCamStrategyConfRelDO.setUpdateDate(new Date());
                        chaildMktCamStrategyConfRelDO.setUpdateStaff(UserUtil.loginId());
                        mktCamStrategyConfRelMapper.insert(chaildMktCamStrategyConfRelDO);
                    }

                    // 活动下过滤规则
                    List<MktStrategyFilterRuleRelDO> mktStrategyFilterRuleRelDOS = mktStrategyFilterRuleRelMapper.selectRuleByStrategyId(mktCampaignId);
                    for (MktStrategyFilterRuleRelDO mktStrategyFilterRuleRelDO : mktStrategyFilterRuleRelDOS) {
                        mktStrategyFilterRuleRelDO.setMktStrategyFilterRuleRelId(null);
                        mktStrategyFilterRuleRelDO.setStrategyId(childMktCampaignId);
                        mktStrategyFilterRuleRelMapper.insert(mktStrategyFilterRuleRelDO);
                    }

                    // 活动下关单规则
                    List<MktStrategyCloseRuleRelDO> mktStrategyCloseRuleRelDOS = mktStrategyCloseRuleRelMapper.selectRuleByStrategyId(mktCampaignId);
                    for (MktStrategyCloseRuleRelDO mktStrategyCloseRuleRelDO : mktStrategyCloseRuleRelDOS) {
                        mktStrategyCloseRuleRelDO.setMktStrategyFilterRuleRelId(null);
                        mktStrategyCloseRuleRelDO.setStrategyId(childMktCampaignId);
                        mktStrategyCloseRuleRelMapper.insert(mktStrategyCloseRuleRelDO);
                    }
                    //如果是框架活动 生成子活动后  生成对应的子需求函 下发给指定岗位的指定人员
                    generateRequest(mktCampaignDO, mktCamCityRelDO.getCityId());
                }
            }
            mktCampaignMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCampaignMap.put("resultMsg", "发布活动成功！");
            mktCampaignMap.put("childMktCampaignIdList", childMktCampaignIdList);
            if (SystemParamsUtil.isCampaignSync()) {
                new Thread() {
                    public void run() {
                        logger.info("活动同步大数据：" + mktCampaignId);
                        syncActivityService.syncActivity(mktCampaignId);
                    }
                }.start();
            }
        } catch (Exception e) {
            logger.error("[op:MktCampaignServiceImpl] failed to publishMktCampaign by mktCampaignId = {}, Exception = ", mktCampaignId, e);
            mktCampaignMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktCampaignMap.put("resultMsg", "发布活动失败！");
        }
        return mktCampaignMap;
    }



    /**
     * 根据地市生成子需求函，子活动和子需求函的关联，和指定的承接人员
     * @param mktCampaignDO 新生成的子活动
     * @param lanId 地市id
     */
    public void generateRequest(MktCampaignDO mktCampaignDO,Long lanId){
        RequestInfo requestInfo=new RequestInfo();
        requestInfo.setRequestType("mkt");
        //需求函批次号按规律递增1
        requestInfo.setBatchNo(getBatchNo(requestInfoMapper.selectMaxBatchNo()));
        requestInfo.setName(mktCampaignDO.getMktCampaignName());
        requestInfo.setDesc(mktCampaignDO.getMktCampaignName());
        requestInfo.setReason(mktCampaignDO.getMktCampaignName());
        requestInfo.setStartDate(mktCampaignDO.getPlanBeginTime());
        requestInfo.setExpectFinishDate(mktCampaignDO.getPlanEndTime());
        requestInfo.setStatusCd("1000");
        requestInfo.setStatusDate(new Date());
        requestInfo.setCreateDate(new Date());
        requestInfo.setUpdateDate(new Date());
        requestInfo.setActionType("add");
        requestInfo.setActivitiKey("mkt_force_province_city");  //需求函活动类型
        requestInfo.setRequestUrgentType("一般");
        requestInfo.setProcessType("0");
        requestInfo.setReportTag("0");
        //得到指定下发的人员信息集合
        List<SysParams> sysParams = sysParamsMapper.listParamsByKeyForCampaign(CITY_PUBLISH);
        if(!sysParams.isEmpty()){
                SysParams s=sysParams.get(0);
                String paramValue = s.getParamValue();
                if(StringUtils.isNotBlank(paramValue)){
                    JSONArray jsonArray = JSONArray.parseArray(paramValue);
                    for (int i = 0; i <jsonArray.size() ; i++) {
                        JSONObject o = JSONObject.parseObject(JSON.toJSONString(jsonArray.get(i)));
                        String lan =o.getString("lanId");
                        if(lanId-Long.valueOf(lan)==0){
                            requestInfo.setContName(o.getString("name"));
                            requestInfo.setDeptCode(o.getString("department"));
                            requestInfo.setCreateStaff(o.getLong("employeeId"));   //创建人,目前指定到承接人的工号
                            break;
                        }
                    }
                }
        }
        requestInfoMapper.insert(requestInfo);
        //开始增加子活动和需求函的关系
        RequestInstRel rel=new RequestInstRel();
        rel.setRequestObjId(mktCampaignDO.getMktCampaignId());
        rel.setRequestInfoId(requestInfo.getRequestTemplateInstId());
        rel.setRequestObjType("mkt");
        rel.setStatusDate(new Date());
        rel.setUpdateDate(new Date());
        rel.setCreateDate(new Date());
        rel.setStatusCd(STATUSCD_EFFECTIVE);
        requestInstRelMapper.insertInfo(rel);

    }

    /**
     * 得到最新的批次编号
     * 浙电产品套餐需求浙【2019】1002116号
     * @param batchNo
     * @return
     */
    public String getBatchNo(String batchNo){
        String substring = batchNo.substring(0,batchNo.length() - 8);
        Long s1=Long.valueOf(batchNo.substring(batchNo.length() - 8, batchNo.length() - 1))+1;
        String path=substring+s1+"号";
        return  path;
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
                Map<String, Object> map = mktStrategyConfService.copyMktStrategyConf(mktCamStrategyConfRelDO.getStrategyConfId(), parentsMktCampaignId, childMktCampaignId, false, mktCampaignDO.getLanId());
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

            MktCampaignDetailVO mktCampaignVO = new MktCampaignDetailVO();
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

            List<Long> camItemIdList = mktCamItemMapper.selectCamItemIdByCampaignId(preMktCampaignId);

            Map<String, Object> stringObjectMap = productService.copyProductRule(UserUtil.loginId(), camItemIdList);
            List<Long> ruleIdList = (List<Long>) stringObjectMap.get("ruleIdList");
            mktCampaignVO.setMktCamItemIdList(ruleIdList);

            // 获取过滤规则集合
            List<Long> filterRuleIdList = mktStrategyFilterRuleRelMapper.selectByStrategyId(preMktCampaignId);
            mktCampaignVO.setFilterRuleIdList(filterRuleIdList);

            // 获取关单规则集合
            List<Long> closeRuleIdList = mktStrategyCloseRuleRelMapper.selectByStrategyId(preMktCampaignId);
            mktCampaignVO.setCloseRuleIdList(closeRuleIdList);

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



    /**
     * 调整活动（复制活动）
     *
     * @param parentMktCampaignId
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> copyMktCampaign(Long parentMktCampaignId) {
        Map<String, Object> maps = new HashMap<>();
        try {
            // 获取活动基本信息
            MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(parentMktCampaignId);
            // 将新活动数据入库
            mktCampaignDO.setMktCampaignId(null);
            mktCampaignDO.setInitId(mktCampaignDO.getInitId());
            mktCampaignDO.setCreateStaff(UserUtil.loginId());
            mktCampaignDO.setCreateDate(new Date());
            mktCampaignDO.setUpdateStaff(UserUtil.loginId());
            mktCampaignDO.setUpdateDate(new Date());
            mktCampaignDO.setStatusCd(StatusCode.STATUS_CODE_PRE_PUBLISHED.getStatusCode());
            mktCampaignDO.setStatusDate(new Date());
            mktCampaignMapper.insert(mktCampaignDO);
            Long newMktCampaignId = mktCampaignDO.getMktCampaignId();

            // 获取下发城市集合
            List<MktCamCityRelDO> mktCamCityRelDOList = mktCamCityRelMapper.selectByMktCampaignId(parentMktCampaignId);
            for (MktCamCityRelDO mktCamCityRelDO : mktCamCityRelDOList) {
                mktCamCityRelDO.setMktCamCityRelId(null);
                mktCamCityRelDO.setMktCampaignId(newMktCampaignId);
                mktCamCityRelDO.setCreateStaff(UserUtil.loginId());
                mktCamCityRelDO.setCreateDate(new Date());
                mktCamCityRelDO.setUpdateStaff(UserUtil.loginId());
                mktCamCityRelDO.setUpdateDate(new Date());
            }
            if (mktCamCityRelDOList != null && !mktCamCityRelDOList.isEmpty()) {
                mktCamCityRelMapper.insertBatch(mktCamCityRelDOList);
            }


            // 获取活动关联的事件
            List<MktCamEvtRelDO> mktCamEvtRelDOList = mktCamEvtRelMapper.selectByMktCampaignId(parentMktCampaignId);
            if (mktCamEvtRelDOList != null) {
                for (MktCamEvtRelDO mktCamEvtRelDO : mktCamEvtRelDOList) {
                    mktCamEvtRelDO.setMktCampEvtRelId(null);
                    mktCamEvtRelDO.setMktCampaignId(newMktCampaignId);
                    mktCamEvtRelDO.setCreateStaff(UserUtil.loginId());
                    mktCamEvtRelDO.setCreateDate(new Date());
                    mktCamEvtRelDO.setUpdateStaff(UserUtil.loginId());
                    mktCamEvtRelDO.setUpdateDate(new Date());
                }
                if (mktCamEvtRelDOList != null && !mktCamEvtRelDOList.isEmpty()) {
                    mktCamEvtRelMapper.insertBatch(mktCamEvtRelDOList);
                }
            }

            // 推荐条目复制
            Map<String, Object> itemResult = productService.copyItemByCampaign(parentMktCampaignId, newMktCampaignId);
            Map<Long, Long> itemMap = (Map<Long, Long>) itemResult.get("itemMap");

            // 试运算展示列实例化
            mktCamDisplayColumnRelService.copyDisplayLabelByCamId(parentMktCampaignId, newMktCampaignId);

            // 获取过滤规则集合
            List<Long> filterRuleIdList = mktStrategyFilterRuleRelMapper.selectByStrategyId(parentMktCampaignId);
            List<MktStrategyFilterRuleRelDO> mktStrategyFilterRuleRelDOList = new ArrayList<>();
            for (Long filterRuleId:filterRuleIdList) {
                MktStrategyFilterRuleRelDO mktStrategyFilterRuleRelDO = new MktStrategyFilterRuleRelDO();
                mktStrategyFilterRuleRelDO.setStrategyId(newMktCampaignId);
                mktStrategyFilterRuleRelDO.setRuleId(filterRuleId);
                mktStrategyFilterRuleRelDO.setCreateStaff(UserUtil.loginId());
                mktStrategyFilterRuleRelDO.setCreateDate(new Date());
                mktStrategyFilterRuleRelDO.setUpdateStaff(UserUtil.loginId());
                mktStrategyFilterRuleRelDO.setUpdateDate(new Date());
                mktStrategyFilterRuleRelDOList.add(mktStrategyFilterRuleRelDO);
            }
            if (mktStrategyFilterRuleRelDOList != null && !mktStrategyFilterRuleRelDOList.isEmpty()) {
                mktStrategyFilterRuleRelMapper.insertBatch(mktStrategyFilterRuleRelDOList);
            }


            // 获取关单规则集合
            List<Long> closeRuleIdList = mktStrategyCloseRuleRelMapper.selectByStrategyId(parentMktCampaignId);
            List<MktStrategyCloseRuleRelDO> mktStrategyCloseRuleRelDOList = new ArrayList<>();
            for (Long closeRuleId:closeRuleIdList) {
                MktStrategyCloseRuleRelDO mktStrategyCloseRuleRelDO = new MktStrategyCloseRuleRelDO();
                mktStrategyCloseRuleRelDO.setStrategyId(newMktCampaignId);
                mktStrategyCloseRuleRelDO.setRuleId(closeRuleId);
                mktStrategyCloseRuleRelDO.setCreateStaff(UserUtil.loginId());
                mktStrategyCloseRuleRelDO.setCreateDate(new Date());
                mktStrategyCloseRuleRelDO.setUpdateStaff(UserUtil.loginId());
                mktStrategyCloseRuleRelDO.setUpdateDate(new Date());
                mktStrategyCloseRuleRelDOList.add(mktStrategyCloseRuleRelDO);
            }
            if (mktStrategyCloseRuleRelDOList != null && !mktStrategyCloseRuleRelDOList.isEmpty()) {
                mktStrategyCloseRuleRelMapper.insertBatch(mktStrategyCloseRuleRelDOList);
            }
            // 遍历活动下策略的集合
            List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDONewList = new ArrayList<>();
            List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOList = mktCamStrategyConfRelMapper.selectByMktCampaignId(parentMktCampaignId);
            for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOList) {
                Map<String, Object> mktStrategyConfMap = mktStrategyConfService.copyMktStrategyConfForAdjust(mktCamStrategyConfRelDO.getStrategyConfId(), parentMktCampaignId, newMktCampaignId, mktCampaignDO.getLanId(), itemMap);
                Long childMktStrategyConfId = (Long) mktStrategyConfMap.get("childMktStrategyConfId");
                MktCamStrategyConfRelDO mktCamStrRelDONew = new MktCamStrategyConfRelDO();
                mktCamStrRelDONew.setMktCampaignId(newMktCampaignId);
                mktCamStrRelDONew.setStrategyConfId(childMktStrategyConfId);
                mktCamStrRelDONew.setCreateStaff(UserUtil.loginId());
                mktCamStrRelDONew.setCreateDate(new Date());
                mktCamStrRelDONew.setUpdateStaff(UserUtil.loginId());
                mktCamStrRelDONew.setUpdateDate(new Date());
                mktCamStrategyConfRelDONewList.add(mktCamStrRelDONew);
            }
            if (mktCamStrategyConfRelDONewList != null && !mktCamStrategyConfRelDONewList.isEmpty()) {
                mktCamStrategyConfRelMapper.insertBatch(mktCamStrategyConfRelDONewList);
            }

            maps.put("resultCode", CommonConstant.CODE_SUCCESS);
            maps.put("mktCampaignId", newMktCampaignId);
        } catch (Exception e) {
            logger.error("[op:MktCampaignServiceImpl] failed to copyMktCampaign by parentMktCampaignId = {}, Exception = ", parentMktCampaignId, e);
            maps.put("resultCode", CommonConstant.CODE_FAIL);
        }
        return maps;
    }


    /**
     * 定时过期活动
     * @return
     */
    @Override
    public Map<String, Object> dueMktCampaign() {
        Map<String, Object> result = new HashMap<>();
        // 查出所有已经发布的活动
        try {
            MktCampaignDO parma = new MktCampaignDO();
            parma.setStatusCd(StatusCode.STATUS_CODE_PUBLISHED.getStatusCode());
            List<MktCampaignDO> mktCampaignDOList = mktCampaignMapper.qryMktCampaignListByCondition(parma);
            Date now = new Date();
            for (MktCampaignDO mktCampaignDO : mktCampaignDOList) {
                // 在失效时间之后置为过期
                if (mktCampaignDO.getPlanEndTime() != null && now.after(mktCampaignDO.getPlanEndTime())) {
                    mktCampaignDO.setStatusCd(StatusCode.STATUS_CODE_PRE_PAUSE.getStatusCode());
                    mktCampaignDO.setUpdateDate(now);
                    mktCampaignMapper.updateByPrimaryKey(mktCampaignDO);
                }
            }
            result.put("resultCode", CommonConstant.CODE_SUCCESS);
        } catch (Exception e) {
            result.put("resultCode", CommonConstant.CODE_FAIL);
            result.put("resultMsg", e);
        }
        return result;
    }

}