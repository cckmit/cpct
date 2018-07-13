package com.zjtelcom.cpct.service.impl.event;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCamEvtRelMapper;
import com.zjtelcom.cpct.dao.event.*;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamEvtRelDO;
import com.zjtelcom.cpct.dto.campaign.MktCamEvtRel;
import com.zjtelcom.cpct.dto.event.*;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.request.event.CreateContactEvtJtReq;
import com.zjtelcom.cpct.request.event.CreateContactEvtReq;
import com.zjtelcom.cpct.response.event.ViewContactEvtRsp;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.event.ContactEvtService;
import com.zjtelcom.cpct.service.filter.FilterRuleService;
import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 事件service实现类
 * @Author pengy
 * @Date 2018/6/21 9:46
 */
@Service
@Transactional
public class ContactEvtServiceImpl extends BaseService implements ContactEvtService {

    public static final int EVTCODE_MAX_LENGTH = 9;//事件编码最大长度
    public static final String EVTD_INDEX = "EVTD";//事件编码前缀
    @Autowired
    private ContactEvtMapper contactEvtMapper;
    @Autowired
    private EventItemMapper eventItemMapper;
    @Autowired
    private EventSceneMapper eventSceneMapper;
    @Autowired
    private EvtSceneCamRelMapper evtSceneCamRelMapper;
    @Autowired
    private ContactEvtItemMapper contactEvtItemMapper;
    @Autowired
    private ContactEvtMatchRulMapper contactEvtMatchRulMapper;
    @Autowired
    private FilterRuleMapper filterRuleMapper;
    @Autowired
    private MktCamEvtRelMapper mktCamEvtRelMapper;


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
        map.put("resultCode", CommonConstant.CODE_SUCCESS);
        map.put("resultMsg", StringUtils.EMPTY);
        map.put("contactEvtList", contactEvtList);
        map.put("pageInfo", new Page(new PageInfo(contactEvtList)));
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
        List<ContactEvtItem> contactEvtItems = new ArrayList<>();
        List<ContactEvtMatchRul> contactEvtMatchRuls = new ArrayList<>();
        for (ContactEvtDetail evtDetail : evtDetailList) {
            //插入事件主题信息
            ContactEvt contactEvt = evtDetail;
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
            for (ContactEvtItem contactEvtItem : contactEvtItems) {
                contactEvtItem.setContactEvtId(contactEvt.getContactEvtId());
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
        List<ContactEvtItem> contactEvtItems = new ArrayList<>();
        List<ContactEvtMatchRul> contactEvtMatchRuls = new ArrayList<>();
        for (ContactEventDetail evtDetail : evtDetailList) {
            //插入事件主题信息
            ContactEvt contactEvt = evtDetail;
            contactEvt.setUpdateDate(DateUtil.getCurrentTime());
            contactEvt.setCreateDate(DateUtil.getCurrentTime());
            contactEvt.setStatusDate(DateUtil.getCurrentTime());
            contactEvt.setUpdateStaff(UserUtil.loginId());
            contactEvt.setCreateStaff(UserUtil.loginId());
            contactEvt.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
            contactEvtMapper.createContactEvt(contactEvt);
            //根据返回的id生成事件编码
            contactEvt.setContactEvtCode(generateEvtCode(contactEvt.getContactEvtId()));
            contactEvtMapper.modContactEvtCode(contactEvt.getContactEvtId(), contactEvt.getContactEvtCode());
            //插入事件采集项
            contactEvtItems = evtDetail.getContactEvtItems();
            for (ContactEvtItem contactEvtItem : contactEvtItems) {
                contactEvtItem.setUpdateDate(DateUtil.getCurrentTime());
                contactEvtItem.setUpdateStaff(UserUtil.loginId());
                contactEvtItem.setContactEvtId(contactEvt.getContactEvtId());
                contactEvtItemMapper.modEventItem(contactEvtItem);
            }
            //插入事件匹配规则
            contactEvtMatchRuls = evtDetail.getContactEvtMatchRuls();
            for (ContactEvtMatchRul contactEvtMatchRul : contactEvtMatchRuls) {
                contactEvtMatchRul.setContactEvtId(contactEvt.getContactEvtId());
                contactEvtMatchRul.setCreateDate(DateUtil.getCurrentTime());
                contactEvtMatchRul.setUpdateDate(DateUtil.getCurrentTime());
                contactEvtMatchRul.setStatusDate(DateUtil.getCurrentTime());
                contactEvtMatchRul.setUpdateStaff(UserUtil.loginId());
                contactEvtMatchRul.setCreateStaff(UserUtil.loginId());
                contactEvtMatchRul.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                contactEvtMatchRulMapper.createContactEvtMatchRul(contactEvtMatchRul);
            }

            //插入事件和活动关联
            List<MktCamEvtRel> mktCamEvtRels = evtDetail.getMktCamEvtRels();
            for (MktCamEvtRel mktCamEvtRel : mktCamEvtRels) {
                MktCamEvtRelDO mktCamEvtRelDO = new MktCamEvtRelDO();
                mktCamEvtRel.setEventId(contactEvt.getContactEvtId());
                mktCamEvtRel.setCreateDate(DateUtil.getCurrentTime());
                mktCamEvtRel.setUpdateDate(DateUtil.getCurrentTime());
                mktCamEvtRel.setStatusDate(DateUtil.getCurrentTime());
                mktCamEvtRel.setUpdateStaff(UserUtil.loginId());
                mktCamEvtRel.setCreateStaff(UserUtil.loginId());
                mktCamEvtRel.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                CopyPropertiesUtil.copyBean2Bean(mktCamEvtRelDO,mktCamEvtRel);
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
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
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
    public Map<String, Object> delEvent(Long contactEvtId) {
        Map<String, Object> map = new HashMap<>();
        contactEvtMapper.delEvent(contactEvtId);
        map.put("resultCode", CommonConstant.CODE_SUCCESS);
        map.put("resultMsg", StringUtils.EMPTY);
        return map;
    }


    /**
     * 关闭事件
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> closeEvent(Long contactEvtId, String statusCd) {
        Map<String, Object> map = new HashMap<>();
        contactEvtMapper.updateEventStatusCd(contactEvtId, statusCd);

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
        CopyPropertiesUtil.copyBean2Bean(contactEventDetail, contactEvt);
        //查询出事件采集项
        List<ContactEvtItem> contactEvtItems = contactEvtItemMapper.listEventItem(contactEvt.getContactEvtId());
        contactEventDetail.setContactEvtItems(contactEvtItems);
        //查询出事件匹配规则
        ContactEvtMatchRul contactEvtMatchRul = new ContactEvtMatchRul();
        contactEvtMatchRul.setContactEvtId(contactEvtId);
        List<ContactEvtMatchRul> contactEvtMatchRuls = contactEvtMatchRulMapper.listEventMatchRuls(contactEvtMatchRul);
        List<FilterRule> filterRuleList = new ArrayList<>();
        FilterRule filterRule = new FilterRule();
        for (ContactEvtMatchRul contactEvtMatchRul1 : contactEvtMatchRuls) {
            filterRule.setRuleId(Long.valueOf(contactEvtMatchRul1.getEvtRulExpression()));
            List<FilterRule> filterRules = filterRuleMapper.qryFilterRule(filterRule);
            if (filterRules != null) {
                filterRuleList.add(filterRules.get(0));
            }
        }
        contactEventDetail.setFilterRules(filterRuleList);
        //获取所有活动
        List<MktCamEvtRel> mktCamEvtRels = new ArrayList<>();
        mktCamEvtRels = mktCamEvtRelMapper.qryBycontactEvtId(contactEvt.getContactEvtId());
        contactEventDetail.setMktCamEvtRels(mktCamEvtRels);

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
        List<ContactEvtItem> contactEvtItems = new ArrayList<>();
        List<ContactEvtMatchRul> contactEvtMatchRuls = new ArrayList<>();
        for (ContactEvtDetail evtDetail : evtDetailList) {
            //插入事件主题信息
            ContactEvt contactEvt = evtDetail;
            contactEvt.setUpdateDate(DateUtil.getCurrentTime());
            contactEvt.setUpdateStaff(UserUtil.loginId());
            contactEvtMapper.modContactEvtJt(contactEvt);
            //插入事件采集项
            contactEvtItems = evtDetail.getContactEvtItems();
            for (ContactEvtItem contactEvtItem : contactEvtItems) {
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
        List<ContactEvtItem> contactEvtItems = new ArrayList<>();
        List<ContactEvtMatchRul> contactEvtMatchRuls = new ArrayList<>();
        List<MktCamEvtRel> mktCamEvtRels = new ArrayList<>();
        for (ContactEventDetail evtDetail : evtDetailList) {
            //更新事件主题信息
            ContactEvt contactEvt = evtDetail;
            contactEvt.setUpdateDate(DateUtil.getCurrentTime());
            contactEvt.setUpdateStaff(UserUtil.loginId());
            contactEvtMapper.modContactEvt(contactEvt);
            //更新事件采集项
            contactEvtItems = evtDetail.getContactEvtItems();
            for (ContactEvtItem contactEvtItem : contactEvtItems) {
                contactEvtItem.setUpdateDate(DateUtil.getCurrentTime());
                contactEvtItem.setUpdateStaff(UserUtil.loginId());
                contactEvtItemMapper.modEventItem(contactEvtItem);
            }
            //更新事件匹配规则
            contactEvtMatchRuls = evtDetail.getContactEvtMatchRuls();
            for (ContactEvtMatchRul contactEvtMatchRul : contactEvtMatchRuls) {
                contactEvtMatchRul.setUpdateDate(DateUtil.getCurrentTime());
                contactEvtMatchRul.setUpdateStaff(UserUtil.loginId());
                contactEvtMatchRulMapper.modContactEvtMatchRul(contactEvtMatchRul);
            }
            //更新活动
            mktCamEvtRels = evtDetail.getMktCamEvtRels();
            for(MktCamEvtRel mktCamEvtRel : mktCamEvtRels){
                MktCamEvtRelDO mktCamEvtRelDO = mktCamEvtRelMapper.selectByPrimaryKey(mktCamEvtRel.getMktCampEvtRelId());
                if(mktCamEvtRelDO != null){
                    mktCamEvtRelDO.setUpdateDate(DateUtil.getCurrentTime());
                    mktCamEvtRelDO.setUpdateStaff(UserUtil.loginId());
                    mktCamEvtRelMapper.updateByPrimaryKey(mktCamEvtRelDO);
                }else {
                    mktCamEvtRelDO = new MktCamEvtRelDO();
                    mktCamEvtRel.setEventId(contactEvt.getContactEvtId());
                    mktCamEvtRel.setCreateDate(DateUtil.getCurrentTime());
                    mktCamEvtRel.setUpdateDate(DateUtil.getCurrentTime());
                    mktCamEvtRel.setStatusDate(DateUtil.getCurrentTime());
                    mktCamEvtRel.setUpdateStaff(UserUtil.loginId());
                    mktCamEvtRel.setCreateStaff(UserUtil.loginId());
                    mktCamEvtRel.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                    CopyPropertiesUtil.copyBean2Bean(mktCamEvtRelDO,mktCamEvtRel);
                    mktCamEvtRelMapper.insert(mktCamEvtRelDO);
                }
            }
        }
        map.put("resultCode", CommonConstant.CODE_SUCCESS);
        map.put("resultMsg", StringUtils.EMPTY);
        return map;
    }


}
