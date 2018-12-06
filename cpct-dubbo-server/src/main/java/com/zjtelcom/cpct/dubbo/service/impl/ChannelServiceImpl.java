package com.zjtelcom.cpct.dubbo.service.impl;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.ContactChannelMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtItemMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.user.UserListMapper;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.ContactEvtItem;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dto.user.UserList;
import com.zjtelcom.cpct.dubbo.model.*;
import com.zjtelcom.cpct.dubbo.service.ChannelService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        List<ContactEvtItem> contactEvtItems = contactEvtItemMapper.listEventItem(contactEvt.getContactEvtId());
        ArrayList<ContactEvtItem> detailVOArrayList = new ArrayList(contactEvtItems);
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
}
