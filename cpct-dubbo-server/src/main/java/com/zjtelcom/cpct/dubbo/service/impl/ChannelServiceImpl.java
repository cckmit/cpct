package com.zjtelcom.cpct.dubbo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zjpii.biz.service.uam.SyncService;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.ContactChannelMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtItemMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.user.UserListMapper;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.domain.channel.EventItem;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.ContactEvtItem;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dto.user.UserList;
import com.zjtelcom.cpct.dubbo.model.*;
import com.zjtelcom.cpct.dubbo.service.ChannelService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.MD5Util;
import com.zjtelcom.cpct.util.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;

@Service
public class ChannelServiceImpl implements ChannelService {
    @Autowired
    private ContactChannelMapper channelMapper;
    @Autowired
    private ContactEvtItemMapper contactEvtItemMapper;
    @Autowired
    private ContactEvtMapper contactEvtMapper;
    @Autowired
    private UserListMapper userListMapper;
    @Autowired
    private FilterRuleMapper filterRuleMapper;
    @Autowired(required = false)
    private SyncService syncService;
    private static final Logger log = LoggerFactory.getLogger(EventApiServiceImpl.class);
    @Override
    public RetChannel getChannelDetail(String channelCode) {
        RetChannel ret = new RetChannel();
        ChannelModel vo = new ChannelModel();
        try {
            Channel channel = channelMapper.selectByCode(channelCode);
            if (channel==null){
                ret.setResultMsg("渠道不存在");
                ret.setResultCode(CODE_FAIL);
                return ret;
            }
            BeanUtil.copy(channel,vo);
        }catch (Exception e){
            e.printStackTrace();
        }
        ret.setResultCode("0");
        ret.setData(vo);
        ret.setResultMsg(null);
        return ret;
    }

    @Override
    public RetEvent getEventDetail(String evtCode){
        RetEvent ret = new RetEvent();
        ContactEvt contactEvt = contactEvtMapper.getEventByEventNbr(evtCode);
        if (contactEvt==null){
            ret.setResultMsg("事件不存在");
            ret.setResultCode(CODE_FAIL);
            return ret;
        }
        ContactEvtModel evtModel = BeanUtil.create(contactEvt,new ContactEvtModel());
        //查询出事件采集项
        List<EventItem> contactEvtItems = contactEvtItemMapper.listEventItem(contactEvt.getContactEvtId());
        ArrayList<ContactEvtItem> detailVOArrayList = new ArrayList<>();
        for (EventItem eventItem : contactEvtItems){
            ContactEvtItem evtItem = BeanUtil.create(eventItem,new ContactEvtItem());
            detailVOArrayList.add(evtItem);
        }
        evtModel.setContactEvtItems(detailVOArrayList);
        ret.setResultCode("0");
        ret.setData(evtModel);
        ret.setResultMsg(null);
        return ret;
    }


    @Override
    public Ret importRuleUserList(FilterRuleInputReq req) {
        Ret ret = new Ret();
        FilterRule filterRule = filterRuleMapper.selectByPrimaryKey(req.getFilterRuleId());
        if (filterRule==null){
            ret.setResultMsg("过滤规则不存在");
            ret.setResultCode(CODE_FAIL);
            return ret;
        }
        for (UserListModel user : req.getUserList()){
            int exists =  userListMapper.checkRule(user.getUserPhone(),user.getRuleId(),null);
            if (exists>0){
                continue;
            }
            UserList userList = BeanUtil.create(user,new UserList());
            userList.setFilterType(filterRule.getFilterType());
            userList.setCreateDate(new Date());
            userList.setUpdateDate(new Date());
            userList.setStatusDate(new Date());
            userList.setRemark("123");
            userList.setLanId(1L);
            userList.setUpdateStaff(UserUtil.loginId());
            userList.setCreateStaff(UserUtil.loginId());
            userList.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
            userListMapper.insert(userList);
        }
        ret.setResultCode("0");
        ret.setResultMsg("导入成功");
        return ret;
    }

    @Override
    public  Map<String,Object> getUamServicePswd( String accountID, String areaCode, String custID) {
        //header
        Map<String, Object> headMap = new HashMap();
        String miyao = "rAx5T2pIvvw5W9vQ";
        String tokn = miyao + "CLZX" + DateUtil.date2String(new Date());
        tokn = MD5Util.encryByMD5(tokn).toUpperCase();
        headMap.put("channel", "CLZX");
        headMap.put("channel_token", tokn);
        headMap.put("bis_module", "策略中心");
        headMap.put("bis_detail", "策略中心获取服务密码");
        headMap.put("version", "v1.0");

        //body
        Date date = new Date();
        String accountType = "2000004";
        String strDateFormat = "yyyyMMddHHmmss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(strDateFormat);
        String strDateFormat2 = "yyyyMMdd";
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(strDateFormat2);
        int nums = (int) ((Math.random() * 9 + 1) * Math.pow(10,  10));//10位随机数
        String serviceCode = "CAP08000";
        String  version = "20200728ZJUAM120";
        String actionCode = "0";
        String transactionID = simpleDateFormat2.format(date) + nums;
        String srcSysID = "12110";
        String digitalSign = "12110";
        String dstSysID = "12110";
        String reqTime = simpleDateFormat.format(date);
        String sendSMS = "00";
        String receSMService = "";

        Map<String,String> bodyMap = new HashMap<>();
        bodyMap.put("accountID",accountID);
        bodyMap.put("accountType",accountType);
        bodyMap.put("serviceCode",serviceCode);
        bodyMap.put("version",version);
        bodyMap.put("actionCode",actionCode);
        bodyMap.put("transactionID",transactionID);
        bodyMap.put("srcSysID",srcSysID);
        bodyMap.put("digitalSign",digitalSign);
        bodyMap.put("dstSysID",dstSysID);
        bodyMap.put("reqTime",reqTime);
        bodyMap.put("sendSMS",sendSMS);
        bodyMap.put("receSMService",receSMService);
        bodyMap.put("areaCode",areaCode);
        bodyMap.put("custID",custID);

        Map<String,Object> extMap = new HashMap<>();
        Map<String,Object> result = syncService.queryPassword(headMap,bodyMap,extMap);
        log.info("统一平台服务密码获取结果：" + result);
        Map<String,String> msghead =(Map) result.get("msghead");

        if("1".equals(msghead.get("result_code"))){
            JSONObject content = JSON.parseObject((String)result.get("msgbody"));
            String password  = content.getJSONObject("DATA").getString("password");
            result.put("password",password);
        }else {
            result.put("password","服务密码获取失败");
            log.info("统一平台服务密码获取失败：" + (String)result.get("result_msg"));
        }
        return result;
    }
}
