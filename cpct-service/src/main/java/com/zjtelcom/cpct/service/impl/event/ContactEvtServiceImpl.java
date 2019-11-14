package com.zjtelcom.cpct.service.impl.event;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCamEvtRelMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.event.*;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamEvtRelDO;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.channel.EventItem;
import com.zjtelcom.cpct.domain.event.EventSorceDO;
import com.zjtelcom.cpct.domain.event.InterfaceCfg;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dto.campaign.MktCamEvtRel;
import com.zjtelcom.cpct.dto.event.*;
import com.zjtelcom.cpct.enums.ParamKeyEnum;
import com.zjtelcom.cpct.request.event.CreateContactEvtJtReq;
import com.zjtelcom.cpct.request.event.CreateContactEvtReq;
import com.zjtelcom.cpct.response.event.ViewContactEvtRsp;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.event.ContactEvtItemService;
import com.zjtelcom.cpct.service.event.ContactEvtService;
import com.zjtelcom.cpct.service.event.EventMatchRulService;
import com.zjtelcom.cpct.service.synchronize.SynContactEvtService;
import com.zjtelcom.cpct.util.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

/**
 * @Description 事件service实现类
 * @Author pengy
 * @Date 2018/6/21 9:46
 */
@Service
@Transactional
public class ContactEvtServiceImpl extends BaseService implements ContactEvtService {

    public static final int EVTCODE_MAX_LENGTH = 9;//事件编码最大长度
    public static final String EVTD_INDEX = "EVT";//事件编码前缀

    @Autowired
    private ContactEvtMapper contactEvtMapper;
    @Autowired
    private ContactEvtItemMapper contactEvtItemMapper;
    @Autowired
    private ContactEvtMatchRulMapper contactEvtMatchRulMapper;
    @Autowired
    private MktCamEvtRelMapper mktCamEvtRelMapper;
    @Autowired
    private SysParamsMapper sysParamsMapper;
    @Autowired
    private ContactEvtItemService evtItemService;
    @Autowired
    private MktCampaignMapper campaignMapper;
    @Autowired
    private ContactEvtTypeMapper evtTypeMapper;
    @Autowired
    private InterfaceCfgMapper interfaceCfgMapper;
    @Autowired
    private EventSorceMapper eventSorceMapper;
    @Autowired
    private EventMatchRulMapper eventMatchRulMapper;
    @Autowired
    private EventMatchRulConditionMapper eventMatchRulConditionMapper;
    @Autowired
    private EventMatchRulService eventMatchRulService;
    @Autowired
    private SynContactEvtService synContactEvtService;

    @Autowired
    private RedisUtils redisUtils;



    @Override
    public Map<String, Object> getEventRelConfig(Map<String, Object> param) {
        Map<String,Object> result = new HashMap<>();
        Long eventId = MapUtil.getLongNum(param.get("eventId"));
        Long campaignId = MapUtil.getLongNum(param.get("campaignId"));
        MktCamEvtRelDO eventRel = mktCamEvtRelMapper.findByCampaignIdAndEvtId(campaignId,eventId);
        if (eventRel==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","未找到有效的关联关系");
            return result;
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",eventRel);
        return result;
    }



    /**
     * 活动事件关系修改优先级及类型
     * @param param
     * @return
     */
    @Override
    public Map<String, Object> editEventRelConfig(Map<String, Object> param) {
        Map<String,Object> result = new HashMap<>();
        Long eventId = MapUtil.getLongNum(param.get("eventId"));
        Long campaignId = MapUtil.getLongNum(param.get("campaignId"));
        Integer levelConfig = MapUtil.getIntNum(param.get("levelConfig"));
        Integer campaignSeq = MapUtil.getIntNum(param.get("campaignSeq"));
        MktCamEvtRelDO eventRel = mktCamEvtRelMapper.findByCampaignIdAndEvtId(campaignId,eventId);
        if (eventRel==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","未找到有效的关联关系");
            return result;
        }
        eventRel.setLevelConfig(levelConfig);
        eventRel.setCampaignSeq(campaignSeq);
        mktCamEvtRelMapper.updateByPrimaryKey(eventRel);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","修改成功");
        return result;
    }

    /**
     * 获取事件类型列表
     * @param userId
     * @return
     */
    @Override
    public Map<String, Object> listMktCampaignType(Long userId) {
        Map<String,Object> result = new HashMap<>();
        List<SysParams> campaignParams = sysParamsMapper.listParamsByKeyForCampaign(ParamKeyEnum.MKT_CAMPAIGN_TYPE.getParamKey());
        result.put(campaignParams.get(0).getParamKey(), campaignParams);
        return result;
    }

    /**
     * 查询事件列表
     *
     * @return
     */
    @Override
    public Map<String, Object> listEvents(ContactEvt contactEvt, Page pageInfo) {
        Map<String, Object> map = new HashMap<>();
        PageHelper.startPage(pageInfo.getPage(), pageInfo.getPageSize());
        List<ContactEvt> contactEvtList = contactEvtMapper.listEvents(contactEvt);
        for (ContactEvt evt : contactEvtList){
            ContactEvtType evtType = evtTypeMapper.selectByPrimaryKey(evt.getContactEvtTypeId());
            if (evtType!=null){
                evt.setContactEvtTypeName(evtType.getContactEvtName());
            }
        }
        map.put("resultCode", CommonConstant.CODE_SUCCESS);
        map.put("resultMsg", StringUtils.EMPTY);
        map.put("contactEvtList", contactEvtList);
        map.put("pageInfo", new Page(new PageInfo(contactEvtList)));
        return map;
    }

    /**
     * 查询事件列表
     *
     * @return
     */
    @Override
    public Map<String, Object> listEventNoPages(ContactEvt contactEvt) {
        Map<String, Object> map = new HashMap<>();
        List<ContactEvt> contactEvtList = contactEvtMapper.listEvents(contactEvt);
        map.put("resultCode", CommonConstant.CODE_SUCCESS);
        map.put("resultMsg", StringUtils.EMPTY);
        map.put("contactEvtList", contactEvtList);
        return map;
    }

    /**
     * 通过渠道编码查询事件列表
     * @param params：key[chlCode,evtName]
     * @return result：key[evtId,evtCode,evtName]
     */
    @Override
    public Map<String, Object> selectContactEvtByChlCode(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        List<ContactEvt> contactEvtByChlCode = contactEvtMapper.getContactEvtByChlCode(params);
        map.put("resultCode", CommonConstant.CODE_SUCCESS);
        map.put("resultMsg", StringUtils.EMPTY);
        map.put("contactEvtList", contactEvtByChlCode);
        return map;
    }

    /**
     * 新增事件(集团调用)
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> createContactEvtJt(CreateContactEvtJtReq createContactEvtJtReq) {
        Map<String, Object> maps = new HashMap<>();
        //获取到所有事件明细
        List<ContactEvtDetail> evtDetailList = createContactEvtJtReq.getContactEvtDetails();
        List<EventItem> contactEvtItems = new ArrayList<>();
        List<ContactEvtMatchRul> contactEvtMatchRuls = new ArrayList<>();
        for (ContactEvtDetail evtDetail : evtDetailList) {
            //插入事件主题信息
            ContactEvt contactEvt = evtDetail;
            contactEvt.setContactEvtCode("");
            contactEvt.setCreateDate(DateUtil.getCurrentTime());
            contactEvt.setUpdateDate(DateUtil.getCurrentTime());
            contactEvt.setStatusDate(DateUtil.getCurrentTime());
            contactEvt.setUpdateStaff(UserUtil.loginId());
            contactEvt.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
            contactEvt.setCreateStaff(UserUtil.loginId());
            contactEvtMapper.createContactEvtJt(contactEvt);
            //根据返回的id生成事件编码
            contactEvt.setContactEvtCode(generateEvtCode(contactEvt.getContactEvtId()));
            contactEvtMapper.modContactEvtCode(contactEvt.getContactEvtId(), contactEvt.getContactEvtCode());
            //插入事件采集项
            contactEvtItems = evtDetail.getContactEvtItems();
            for (EventItem contactEvtItem : contactEvtItems) {
                contactEvtItem.setContactEvtId(contactEvt.getContactEvtId());
                contactEvtItem.setValueDataType("1200");
                contactEvtItem.setEvtTypeId(contactEvt.getContactEvtTypeId());
                contactEvtItem.setCreateDate(DateUtil.getCurrentTime());
                contactEvtItem.setUpdateDate(DateUtil.getCurrentTime());
                contactEvtItem.setStatusDate(DateUtil.getCurrentTime());
                contactEvtItem.setCreateStaff(UserUtil.loginId());
                contactEvtItem.setUpdateStaff(UserUtil.loginId());
                contactEvtItem.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                contactEvtItemMapper.insertContactEvtItem(contactEvtItem);
            }
            //插入事件匹配规则
            contactEvtMatchRuls = evtDetail.getContactEvtMatchRuls();
            for (ContactEvtMatchRul contactEvtMatchRul : contactEvtMatchRuls) {
                contactEvtMatchRul.setCreateDate(DateUtil.getCurrentTime());
                contactEvtMatchRul.setUpdateDate(DateUtil.getCurrentTime());
                contactEvtMatchRul.setStatusDate(DateUtil.getCurrentTime());
                contactEvtMatchRul.setUpdateStaff(UserUtil.loginId());
                contactEvtMatchRul.setCreateStaff(UserUtil.loginId());
                contactEvtMatchRul.setContactEvtId(contactEvt.getContactEvtId());
                contactEvtMatchRul.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                contactEvtMatchRulMapper.createContactEvtMatchRul(contactEvtMatchRul);
            }
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 新增事件
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> createContactEvt(CreateContactEvtReq createContactEvtReq) throws Exception {
        Map<String, Object> maps = new HashMap<>();
        //获取到所有事件明细
        List<ContactEventDetail> evtDetailList = createContactEvtReq.getContactEvtDetails();
        List<EventItem> contactEvtItems = new ArrayList<>();
        List<ContactEvtMatchRul> contactEvtMatchRuls = new ArrayList<>();
        Long eventId = null;
        for (ContactEventDetail evtDetail : evtDetailList) {
            //插入事件主题信息
            final ContactEvt contactEvt = evtDetail;

            eventId = contactEvt.getContactEvtId();
            List<Long> interfaceCfgIdList = contactEvt.getInterfaceCfgId();
//            //todo 待确认必填字段
//            contactEvt.setInterfaceCfgId();
            contactEvt.setExtEventId(1000L);
            contactEvt.setUpdateDate(DateUtil.getCurrentTime());
            contactEvt.setCreateDate(DateUtil.getCurrentTime());
            contactEvt.setStatusDate(DateUtil.getCurrentTime());
            contactEvt.setUpdateStaff(UserUtil.loginId());
            contactEvt.setCreateStaff(UserUtil.loginId());
            contactEvt.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
            contactEvt.setInterfaceCfgId(null);
            contactEvtMapper.createContactEvt(contactEvt);
            //根据返回的id生成事件编码
            contactEvt.setContactEvtCode(generateEvtCode(contactEvt.getContactEvtId()));
            contactEvtMapper.modContactEvtCode(contactEvt.getContactEvtId(), contactEvt.getContactEvtCode());

            // 添加事件关联事件源接口关系
            for (Long interfaceId : interfaceCfgIdList) {
                EventInterfaceRel eventInterfaceRel = new EventInterfaceRel();
                BeanUtil.copy(evtDetail, eventInterfaceRel);
                eventInterfaceRel.setInterfaceId(interfaceId);
                InterfaceCfg interfaceCfg = interfaceCfgMapper.selectByPrimaryKey(interfaceId);
                // 事件源接口调用方—事件接入渠道
                eventInterfaceRel.setChannelCode(interfaceCfg.getCaller());
                int rel = contactEvtMapper.createEvtInterfaceRel(eventInterfaceRel);
            }

            //插入事件采集项
            contactEvtItems = evtDetail.getContactEvtItems();
            for (EventItem contactEvtItem : contactEvtItems) {
                contactEvtItem.setContactEvtId(contactEvt.getContactEvtId());
                maps = evtItemService.createEventItem(contactEvtItem);
                if (!maps.get("resultCode").equals(CODE_SUCCESS)){
                    return maps;
                }
            }

            //插入事件和活动关联

            List<MktCamEvtRel> mktCamEvtRels = evtDetail.getMktCamEvtRels();
            for (MktCamEvtRel mktCamEvtRel : mktCamEvtRels) {
                MktCamEvtRelDO mktCamEvtRelDO = BeanUtil.create(mktCamEvtRel,new MktCamEvtRelDO());
                mktCamEvtRelDO.setEventId(contactEvt.getContactEvtId());
                mktCamEvtRelDO.setCreateDate(DateUtil.getCurrentTime());
                mktCamEvtRelDO.setUpdateDate(DateUtil.getCurrentTime());
                mktCamEvtRelDO.setStatusDate(DateUtil.getCurrentTime());
                mktCamEvtRelDO.setUpdateStaff(UserUtil.loginId());
                mktCamEvtRelDO.setCreateStaff(UserUtil.loginId());
                mktCamEvtRelDO.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                mktCamEvtRelMapper.insert(mktCamEvtRelDO);
            }

//            List<EvtSceneCamRel> evtSceneCamRels = evtDetail.getEvtSceneCamRels();
//            for (EvtSceneCamRel evtSceneCamRel : evtSceneCamRels) {
//                List<EventScene> eventScenes = eventSceneMapper.qryEventSceneByEvtId(contactEvt.getContactEvtId());
//                evtSceneCamRel.setCreateDate(DateUtil.getCurrentTime());
//                evtSceneCamRel.setUpdateDate(DateUtil.getCurrentTime());
//                evtSceneCamRel.setStatusDate(DateUtil.getCurrentTime());
//                evtSceneCamRel.setUpdateStaff(UserUtil.loginId());
//                evtSceneCamRel.setCreateStaff(UserUtil.loginId());
//                evtSceneCamRel.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
//                for (EventScene eventScene : eventScenes) {
//                    evtSceneCamRel.setEventSceneId(eventScene.getEventSceneId());
//                    evtSceneCamRelMapper.insert(evtSceneCamRel);
//                }
//            }

            //插入事件规则
            if(evtDetail.getEventMatchRulDetail() != null) {
                evtDetail.getEventMatchRulDetail().setEventId(contactEvt.getContactEvtId());
                Map<String, Object> eventMatchRul = eventMatchRulService.createEventMatchRul(evtDetail.getEventMatchRulDetail());
                if (!eventMatchRul.get("resultCode").equals(CODE_SUCCESS)) {
                    return eventMatchRul;
                }
            }

            List<Long> interfaceCfgId = contactEvt.getInterfaceCfgId();
            for (Long aLong : interfaceCfgId) {
                //大数据事件同步
                InterfaceCfg interfaceCfg = interfaceCfgMapper.selectByPrimaryKey(aLong);
                if(interfaceCfg != null) {
                    EventSorceDO eventSorceDO = eventSorceMapper.selectByPrimaryKey(interfaceCfg.getEvtSrcId());
                    Map<String, Object> map = new HashMap<>();
                    if (eventSorceDO.getEvtSrcName().equals("大数据")) {
                        map.put("isi", DateUtil.getDetailTime());
                        map.put("eventId", contactEvt.getContactEvtId());
                        map.put("eventCode", contactEvt.getContactEvtCode());
                        map.put("eventName", contactEvt.getContactEvtName());

                        if (contactEvt.getEvtTrigType().equals("1000")) {
                            map.put("eventType", "1");
                        } else {
                            map.put("eventType", "2");
                        }
                        map.put("eventClass", "");
                        map.put("state", contactEvt.getStatusCd());

                    }
                }
            }

//            if (SystemParamsUtil.isSync()){
//                new Thread(){
//                    public void run(){
//                        try {
//                            synContactEvtService.synchronizeSingleEvent(contactEvt.getContactEvtId(),"");
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
//                }.start();
//            }
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("eventId",eventId);
        return maps;
    }

    /**
     * 生成事件编码
     * param contactEvtId 事件id
     * eg input contactEvtId = 1 return EVTD000000001
     */
    private String generateEvtCode(Long contactEvtId) {
        int length = EVTCODE_MAX_LENGTH - String.valueOf(contactEvtId).length();
        String result = EVTD_INDEX;
        for (int i = 0; i < length; i++) {
            //补齐所需的0
            result += "0";
        }
        return result + contactEvtId;
    }

    /**
     * 事件删除
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> delEvent(final Long contactEvtId) {
        Map<String, Object> map = new HashMap<>();
        Long eventId = null;
        ContactEvt evt = contactEvtMapper.getEventById(contactEvtId);
        if (evt==null){
            map.put("resultCode", CODE_FAIL);
            map.put("resultMsg","事件不存在");
            return map;
        }
        List<MktCamEvtRelDO> relDOList = mktCamEvtRelMapper.listActByEventId(contactEvtId);
        if (relDOList.size()>0){
            map.put("resultCode", CODE_FAIL);
            map.put("resultMsg","事件已关联活动无法删除");
            return map;
        }
        //EventMatchRulDetail eventMatchRulDetail = ;
        //删除事件
        contactEvtMapper.delEvent(contactEvtId);
        contactEvtItemMapper.deleteByEventId(contactEvtId);
        EventMatchRulDTO eventMatchRulDTO = eventMatchRulMapper.listEventMatchRul(contactEvtId);
        if(eventMatchRulDTO != null) {
            eventMatchRulMapper.delEventMatchRul(eventMatchRulDTO);
            List<EventMatchRulCondition> eventMatchRulConditionList = eventMatchRulConditionMapper.listEventMatchRulCondition(eventMatchRulDTO.getEvtMatchRulId());
            for(EventMatchRulCondition eventMatchRulCondition : eventMatchRulConditionList){
                eventMatchRulConditionMapper.delEventMatchRulCondition(eventMatchRulCondition);
            }
        }

//        if (SystemParamsUtil.isSync()){
//            new Thread(){
//                public void run(){
//                    try {
//                        synContactEvtService.deleteSingleEvent(contactEvtId,"");
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//            }.start();
//        }
        eventId = contactEvtId;
        map.put("resultCode", CommonConstant.CODE_SUCCESS);
        map.put("resultMsg", StringUtils.EMPTY);
        map.put("eventId",eventId);
        return map;
    }


    /**
     * 关闭事件
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> closeEvent(final Long contactEvtId, String statusCd) {
        Map<String, Object> map = new HashMap<>();
        ContactEvt evt = contactEvtMapper.getEventById(contactEvtId);
        if (evt==null){
            map.put("resultCode", CODE_FAIL);
            map.put("resultMsg","事件不存在");
            return map;
        }
        contactEvtMapper.updateEventStatusCd(contactEvtId, statusCd);
        map.put("resultCode",CODE_SUCCESS);
        map.put("eventId",contactEvtId);
        if (statusCd.equals("1000")){
            map.put("resultMsg","开启成功");
        }else {
            map.put("resultMsg","关闭成功");
        }
        //事件关闭开启 同步状态到生产
//        if (SystemParamsUtil.isSync()){
//            new Thread(){
//                public void run(){
//                    try {
//                        synContactEvtService.synchronizeSingleEvent(contactEvtId,"");
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//            }.start();
//        }
        return map;
    }

    /**
     * 查看事件
     */
    @Override
    public Map<String, Object> editEvent(Long contactEvtId) throws Exception {
        Map<String, Object> map = new HashMap<>();
        ViewContactEvtRsp viewContactEvtRsp = new ViewContactEvtRsp();
        ContactEventDetail contactEventDetail = new ContactEventDetail();
        ContactEvt contactEvt = contactEvtMapper.getEventById(contactEvtId);
        List<EventInterfaceRel> eventInterfaceRels = contactEvtMapper.selectEvtInterfaceRelByEvtId(contactEvtId);
        if (contactEvt==null){
            map.put("resultCode", CODE_FAIL);
            map.put("resultMsg","事件不存在");
            return map;
        }
        BeanUtil.copy(contactEvt,contactEventDetail);
        ContactEvtType evtType = evtTypeMapper.selectByPrimaryKey(contactEvt.getContactEvtTypeId());
        if (evtType!=null){
            contactEventDetail.setEventTypeName(evtType.getContactEvtName());
        }
        List<InterfaceCfg> interfaceCfgs = new ArrayList<>();
        for (EventInterfaceRel eventInterfaceRel : eventInterfaceRels) {
            InterfaceCfg interfaceCfg = interfaceCfgMapper.selectByPrimaryKey(eventInterfaceRel.getInterfaceId());
            if (interfaceCfg != null){
                interfaceCfgs.add(interfaceCfg);
            }
        }
        contactEventDetail.setInterfaceCfgs(interfaceCfgs);

        //查询出事件采集项
        List<EventItem> contactEvtItems = contactEvtItemMapper.listEventItem(contactEvt.getContactEvtId());
        contactEventDetail.setContactEvtItems(contactEvtItems);

        //查询出事件匹配规则
//        ContactEvtMatchRul contactEvtMatchRul = new ContactEvtMatchRul();
//        contactEvtMatchRul.setContactEvtId(contactEvtId);
//        List<ContactEvtMatchRul> contactEvtMatchRuls = contactEvtMatchRulMapper.listEventMatchRuls(contactEvtMatchRul);
//        List<FilterRule> filterRuleList = new ArrayList<>();
//        FilterRule filterRule = new FilterRule();
//        for (ContactEvtMatchRul contactEvtMatchRul1 : contactEvtMatchRuls) {
//            filterRule.setRuleId(Long.valueOf(contactEvtMatchRul1.getEvtRulExpression()));
//            List<FilterRule> filterRules = filterRuleMapper.qryFilterRule(filterRule);
//            if (filterRules != null) {
//                filterRuleList.add(filterRules.get(0));
//            }
//        }
//        contactEventDetail.setFilterRules(filterRuleList);
//        if (contactEvt.getMktCampaignType()!=null){
//            String paramKey = ParamKeyEnum.MKT_CAMPAIGN_TYPE.getParamKey();
//           SysParams systemParam = sysParamsMapper.findParamsByValue(paramKey,contactEvt.getMktCampaignType());
//            if (systemParam!=null){
//                contactEventDetail.setMktCampaignTypeName(systemParam.getParamName());
//            }
//        }
        //获取所有活动
        List<MktCamEvtRel> mktCamEvtRels = new ArrayList<>();

        mktCamEvtRels = mktCamEvtRelMapper.qryBycontactEvtId(contactEvt.getContactEvtId());

        for (MktCamEvtRel rel : mktCamEvtRels){
            MktCampaignDO campaign = campaignMapper.selectByPrimaryKey(rel.getMktCampaignId());
            if (campaign!=null){
                rel.setCampaignName(campaign.getMktCampaignName());
                rel.setMktActivityNbr(campaign.getMktActivityNbr());
            }
        }
        contactEventDetail.setMktCamEvtRels(mktCamEvtRels);

        //获取事件规则
        Map<String, Object> eventMatchRul = eventMatchRulService.listEventMatchRul(contactEvtId);
        EventMatchRulDTO eventMatchRulDTO = (EventMatchRulDTO) eventMatchRul.get("listEventMatchRul");
        EventMatchRulDetail eventMatchRulDetail = new EventMatchRulDetail();
        if(eventMatchRulDTO != null) {
            Map<String, Object> eventMatchRulCondition = eventMatchRulService.listEventMatchRulCondition(eventMatchRulDTO.getEvtMatchRulId());
            List<EventMatchRulConditionVO> eventMatchRulConditionVOS = (List<EventMatchRulConditionVO>) eventMatchRulCondition.get("listEventMatchRulCondition");

            eventMatchRulDetail = BeanUtil.create(eventMatchRulDTO, new EventMatchRulDetail());

            eventMatchRulDetail.setEventMatchRulConditionVOS(eventMatchRulConditionVOS);
        }
        contactEventDetail.setEventMatchRulDetail(eventMatchRulDetail);

        viewContactEvtRsp.setContactEvtDetail(contactEventDetail);
        map.put("resultCode", CommonConstant.CODE_SUCCESS);
        map.put("resultMsg", StringUtils.EMPTY);
        map.put("viewContactEvtRsp", viewContactEvtRsp);
        return map;
    }

    /**
     * 修改事件(集团)
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> modContactEvtJt(CreateContactEvtJtReq createContactEvtJtReq) {
        Map<String, Object> map = new HashMap<>();
        //获取到所有事件明细
        List<ContactEvtDetail> evtDetailList = createContactEvtJtReq.getContactEvtDetails();
        List<EventItem> contactEvtItems = new ArrayList<>();
        List<ContactEvtMatchRul> contactEvtMatchRuls = new ArrayList<>();
        for (ContactEvtDetail evtDetail : evtDetailList) {
            //插入事件主题信息
            ContactEvt contactEvt = evtDetail;
            contactEvt.setUpdateDate(DateUtil.getCurrentTime());
            contactEvt.setUpdateStaff(UserUtil.loginId());
            contactEvtMapper.modContactEvtJt(contactEvt);
            //插入事件采集项
            contactEvtItems = evtDetail.getContactEvtItems();
            for (EventItem contactEvtItem : contactEvtItems) {
                contactEvtItem.setUpdateDate(DateUtil.getCurrentTime());
                contactEvtItem.setUpdateStaff(UserUtil.loginId());
                contactEvtItemMapper.modEventItem(contactEvtItem);
            }
            //插入事件匹配规则
            contactEvtMatchRuls = evtDetail.getContactEvtMatchRuls();
            for (ContactEvtMatchRul contactEvtMatchRul : contactEvtMatchRuls) {
                contactEvtMatchRul.setUpdateDate(DateUtil.getCurrentTime());
                contactEvtMatchRul.setUpdateStaff(UserUtil.loginId());
                contactEvtMatchRulMapper.modContactEvtMatchRul(contactEvtMatchRul);
            }
        }
        map.put("resultCode", CommonConstant.CODE_SUCCESS);
        map.put("resultMsg", StringUtils.EMPTY);
        return map;
    }

    /**
     * 修改事件
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> modContactEvt(CreateContactEvtReq createContactEvtReq) throws Exception {
        Map<String, Object> map = new HashMap<>();
        //获取到所有事件明细
        List<ContactEventDetail> evtDetailList = createContactEvtReq.getContactEvtDetails();
        List<EventItem> contactEvtItems = new ArrayList<>();
        List<MktCamEvtRel> mktCamEvtRels = new ArrayList<>();
        Long eventId = null;
        for (ContactEventDetail evtDetail : evtDetailList) {
            eventId = evtDetail.getContactEvtId();
            List<Long> interfaceCfgIdList = evtDetail.getInterfaceCfgId();
            //更新事件主题信息
            EventEditVO editVO = BeanUtil.create(evtDetail,new EventEditVO());
            final ContactEvt contactEvt = contactEvtMapper.getEventById(evtDetail.getContactEvtId());
            if (contactEvt==null ){
                map.put("resultCode", CODE_FAIL);
                map.put("resultMsg", "事件不存在");
                return map;
            }
            BeanUtil.copy(editVO,contactEvt);
            contactEvt.setUpdateDate(DateUtil.getCurrentTime());
            contactEvt.setUpdateStaff(UserUtil.loginId());
            contactEvtMapper.modContactEvt(contactEvt);
            // 清除原有事件与事件源接口关联关系
            int i = contactEvtMapper.delEvtInterfaceRel(eventId);
            // 添加事件关联事件源接口关系
            for (Long interfaceId : interfaceCfgIdList) {
                EventInterfaceRel eventInterfaceRel = new EventInterfaceRel();
                BeanUtil.copy(evtDetail, eventInterfaceRel);
                eventInterfaceRel.setInterfaceId(interfaceId);
                InterfaceCfg interfaceCfg = interfaceCfgMapper.selectByPrimaryKey(interfaceId);
                // 事件源接口调用方—事件接入渠道
                eventInterfaceRel.setChannelCode(interfaceCfg.getCaller());
                int rel = contactEvtMapper.createEvtInterfaceRel(eventInterfaceRel);
            }

            //更新事件采集项
            List<EventItem> oldItems = contactEvtItemMapper.listEventItem(evtDetail.getContactEvtId());
            contactEvtItems = evtDetail.getContactEvtItems();
            List<Long> contactIdList = new ArrayList<>();
            for (EventItem contactEvtItem : contactEvtItems) {
                contactIdList.add(contactEvtItem.getEvtItemId());
                //判断传值id 是否为0 为0新增 不为0更新
                if (contactEvtItem.getEvtItemId().equals(0L)){
                    //todo 新增采集项
                    contactEvtItem.setContactEvtId(evtDetail.getContactEvtId());
                    map = evtItemService.createEventItem(contactEvtItem);
                    if (!map.get("resultCode").equals(CODE_SUCCESS)){
                        return map;
                    }
                }else {
                    EventItem item = contactEvtItemMapper.selectByPrimaryKey(contactEvtItem.getEvtItemId());
                    ItemEditVO itemEditVO = BeanUtil.create(contactEvtItem,new ItemEditVO());
                    BeanUtil.copy(itemEditVO,item);
                    item.setUpdateDate(DateUtil.getCurrentTime());
                    item.setUpdateStaff(UserUtil.loginId());
                    contactEvtItemMapper.modEventItem(item);
                }
            }
            //遍历旧的采集项 不在的删除
            for (EventItem oldItem : oldItems){
                if (!contactIdList.contains(oldItem.getEvtItemId())){

                    contactEvtItemMapper.deleteByPrimaryKey(oldItem.getEvtItemId());
                }
            }
            //更新活动
            mktCamEvtRels = evtDetail.getMktCamEvtRels();
            List<MktCamEvtRel> oldRelList = mktCamEvtRelMapper.qryBycontactEvtId(evtDetail.getContactEvtId());
            List<Long> relIdList = new ArrayList<>();
            for (MktCamEvtRel mktCamEvtRel : mktCamEvtRels) {
                if (mktCamEvtRel.getMktCampEvtRelId()!=null){
                    relIdList.add(mktCamEvtRel.getMktCampEvtRelId());
                }
                MktCamEvtRelDO mktCamEvtRelDO = mktCamEvtRelMapper.findByCampaignIdAndEvtId(mktCamEvtRel.getMktCampaignId(),evtDetail.getContactEvtId());
                if (mktCamEvtRelDO != null) {
                    mktCamEvtRelDO.setCampaignSeq(mktCamEvtRel.getCampaignSeq());
                    mktCamEvtRelDO.setMktCampaignId(mktCamEvtRel.getMktCampaignId());
                    mktCamEvtRelDO.setLevelConfig(mktCamEvtRel.getLevelConfig());
                    mktCamEvtRelDO.setUpdateDate(DateUtil.getCurrentTime());
                    mktCamEvtRelDO.setUpdateStaff(UserUtil.loginId());
                    mktCamEvtRelMapper.updateByPrimaryKey(mktCamEvtRelDO);
                } else {
                    mktCamEvtRelDO = BeanUtil.create(mktCamEvtRel,new MktCamEvtRelDO());

                    mktCamEvtRelDO.setEventId(contactEvt.getContactEvtId());
                    mktCamEvtRelDO.setCreateDate(DateUtil.getCurrentTime());
                    mktCamEvtRelDO.setUpdateDate(DateUtil.getCurrentTime());
                    mktCamEvtRelDO.setStatusDate(DateUtil.getCurrentTime());
                    mktCamEvtRelDO.setUpdateStaff(UserUtil.loginId());
                    mktCamEvtRelDO.setCreateStaff(UserUtil.loginId());
                    mktCamEvtRelDO.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                    mktCamEvtRelMapper.insert(mktCamEvtRelDO);
                }
            }
            //删除不存在的关联关系
            for (MktCamEvtRel evtRel : oldRelList){
                if (!relIdList.contains(evtRel.getMktCampEvtRelId())){
                    mktCamEvtRelMapper.deleteByPrimaryKey(evtRel.getMktCampEvtRelId());
                }
            }
            //更新事件规则
            if(evtDetail.getEventMatchRulDetail() != null) {
                EventMatchRulDetail eventMatchRulDetail = evtDetail.getEventMatchRulDetail();
                eventMatchRulDetail.setEventId(evtDetail.getContactEvtId());
                List<EventMatchRulCondition> eventMatchRulConditions = eventMatchRulDetail.getEventMatchRulConditions();
//                eventMatchRulDetail.setEventMatchRulConditions(eventMatchRulConditions);
                List<Long> list = new ArrayList<>();
                //遍历旧的事件规则条件
                Map<String, Object> eventMatchRulMap = eventMatchRulService.listEventMatchRul(evtDetail.getContactEvtId());
                EventMatchRulDTO eventMatchRulDTO = (EventMatchRulDTO) eventMatchRulMap.get("listEventMatchRul");
                Map<String, Object> eventMatchRulConditionMap = eventMatchRulService.listEventMatchRulCondition(eventMatchRulDTO.getEvtMatchRulId());
                List<EventMatchRulConditionVO> rulConditionList = (List<EventMatchRulConditionVO>) eventMatchRulConditionMap.get("listEventMatchRulCondition");

                //事件规则更新
                Map<String, Object> eventMatchRul = eventMatchRulService.modEventMatchRul(eventMatchRulDetail);
                if (!eventMatchRul.get("resultCode").equals(CODE_SUCCESS)) {
                    return eventMatchRul;
                }

                //更新的事件规则条件的id
                for (EventMatchRulCondition eventMatchRulCondition : eventMatchRulConditions) {
                    list.add(eventMatchRulCondition.getConditionId());
                }

                //删除多余的事件规则条件
                for (EventMatchRulConditionVO eventMatchRulConditionVO : rulConditionList) {
                    if (!list.contains(eventMatchRulConditionVO.getConditionId())) {
                        EventMatchRulCondition eventMatchRulCondition = BeanUtil.create(eventMatchRulConditionVO, new EventMatchRulCondition());
                        eventMatchRulService.delEventMatchRulCondition(eventMatchRulCondition.getConditionId());
                    }
                }
            }
            eventId = evtDetail.getContactEvtId();
//            if (SystemParamsUtil.isSync()){
//                new Thread(){
//                    public void run(){
//                        try {
//                            synContactEvtService.synchronizeSingleEvent(contactEvt.getContactEvtId(),"");
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
//                }.start();
//            }

        }
        map.put("resultCode", CommonConstant.CODE_SUCCESS);
        map.put("resultMsg", StringUtils.EMPTY);
        map.put("eventId",eventId);
        return map;
    }

    /**
     * 事件详情
     */
    @Override
    public Map<String, Object> evtDetails(ContactEvt contactEvt) {
        Map<String,Object> maps = new HashMap<>();
        contactEvt = contactEvtMapper.getEventById(contactEvt.getContactEvtId());
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("ContactEvt", contactEvt);
        return maps;
    }


    @Override
    public Map<String, Object> evtDetailsByIdList(List<Integer> idList) {
        Map<String,Object> maps = new HashMap<>();
        List<ContactEvt> evts = new ArrayList<>();
        for (Integer id: idList){
            ContactEvt contactEvt = contactEvtMapper.getEventById(Long.valueOf(id.toString()));
            if (contactEvt!=null){
                evts.add(contactEvt);
            }
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", evts);
        return maps;
    }

    /**
     * 批量查询事件列表
     * @param contactChlCodeList
     * @return
     */
    @Override
    public Map<String,Object> selectBatchByCode(List<String> contactChlCodeList){
        Map<String,Object> maps = new HashMap<>();
        List<ContactEvt> contactEvtList = contactEvtMapper.selectBatchByCode(contactChlCodeList);
        maps.put("contactEvtList", contactEvtList);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", "批量查询事件列表成功");
        return maps;
    }
}
