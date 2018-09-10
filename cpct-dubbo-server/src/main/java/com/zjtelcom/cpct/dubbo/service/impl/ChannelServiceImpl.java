package com.zjtelcom.cpct.dubbo.service.impl;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.ContactChannelMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtItemMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.dto.channel.ChannelVO;
import com.zjtelcom.cpct.dto.event.ContactEventDetail;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.ContactEvtItem;
import com.zjtelcom.cpct.dubbo.service.ChannelService;
import com.zjtelcom.cpct.response.event.ViewContactEvtRsp;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public Map<String, Object> getChannelDetail(String channelCode) {
        Map<String,Object> result = new HashMap<>();
        ChannelVO vo = new ChannelVO();
        try {
            Channel channel = channelMapper.selectByCode(channelCode);
            if (channel==null){
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","渠道不存在");
                return result;
            }
            vo = ChannelUtil.map2ChannelVO(channel);
        }catch (Exception e){
            e.printStackTrace();
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",vo);
        return result;
    }

    @Override
    public Map<String, Object> getEventDetail(String evtCode){
        Map<String, Object> map = new HashMap<>();
        ViewContactEvtRsp viewContactEvtRsp = new ViewContactEvtRsp();
        ContactEventDetail contactEventDetail = new ContactEventDetail();
        ContactEvt contactEvt = contactEvtMapper.getEventByEventNbr(evtCode);
        if (contactEvt==null){
            map.put("resultCode", CODE_FAIL);
            map.put("resultMsg","事件不存在");
            return map;
        }
        BeanUtil.copy(contactEvt,contactEventDetail);
        //查询出事件采集项
        List<ContactEvtItem> contactEvtItems = contactEvtItemMapper.listEventItem(contactEvt.getContactEvtId());
        contactEventDetail.setContactEvtItems(contactEvtItems);
        viewContactEvtRsp.setContactEvtDetail(contactEventDetail);
        map.put("resultCode", CommonConstant.CODE_SUCCESS);
        map.put("resultMsg", StringUtils.EMPTY);
        map.put("viewContactEvtRsp", viewContactEvtRsp);
        return map;
    }
}
