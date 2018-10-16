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
import com.zjtelcom.cpct.dubbo.model.ChannelModel;
import com.zjtelcom.cpct.dubbo.model.ContactEvtModel;
import com.zjtelcom.cpct.dubbo.model.FilterRuleInputReq;
import com.zjtelcom.cpct.dubbo.model.UserListModel;
import com.zjtelcom.cpct.dubbo.service.ChannelService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

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
    public Map<String, Object> getChannelDetail(String channelCode) {
        Map<String,Object> result = new HashMap<>();
        ChannelModel vo = new ChannelModel();
        try {
            Channel channel = channelMapper.selectByCode(channelCode);
            if (channel==null){
                result.put("resultCode",CODE_FAIL);
                result.put("resultMessage","渠道不存在");
                return result;
            }
            BeanUtil.copy(channel,vo);
        }catch (Exception e){
            e.printStackTrace();
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMessage",null);
        result.put("data",vo);
        return result;
    }

    @Override
    public Map<String, Object> getEventDetail(String evtCode){
        Map<String, Object> result = new HashMap<>();

        ContactEvt contactEvt = contactEvtMapper.getEventByEventNbr(evtCode);
        if (contactEvt==null){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMessage","事件不存在");
            return result;
        }
        ContactEvtModel evtModel = BeanUtil.create(contactEvt,new ContactEvtModel());
        //查询出事件采集项
        List<ContactEvtItem> contactEvtItems = contactEvtItemMapper.listEventItem(contactEvt.getContactEvtId());
        evtModel.setContactEvtItems(contactEvtItems);
        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("resultMessage", StringUtils.EMPTY);
        result.put("data",evtModel);
        return result;
    }


    @Override
    public Map<String, Object> importRuleUserList(FilterRuleInputReq req) {
        Map<String, Object> result = new HashMap<>();
        FilterRule filterRule = filterRuleMapper.selectByPrimaryKey(req.getFilterRuleId());
        if (filterRule==null){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMessage", "过滤规则不存在");
            return result;
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
        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("resultMessage", "导入成功");
        return result;
    }
}
