package com.zjtelcom.cpct.service.impl.event;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.event.ContactEvtItemMapper;
import com.zjtelcom.cpct.dto.event.ContactEvtItem;
import com.zjtelcom.cpct.request.event.ContactEvtReq;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.event.ContactEvtItemService;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * @Description 事件采集项实现类
 * @Author pengy
 * @Date 2018/7/1 22:41
 */
@Service
public class ContactEvtItemServiceImpl extends BaseService implements ContactEvtItemService {

    @Autowired
    private ContactEvtItemMapper contactEvtItemMapper;

    /**
     * 获取事件采集项列表(主参)
     */
    @Override
    public Map<String, Object> listEventItem(ContactEvtReq ContactEvtReq) {
        Map<String, Object> maps = new HashMap<>();
        List<ContactEvtItem> contactEvtItems = new ArrayList<>();
//        Page pageInfo = ContactEvtReq.getPage();
//        PageHelper.startPage(pageInfo.getPage(), pageInfo.getPageSize());
        contactEvtItems = contactEvtItemMapper.listEventItem(ContactEvtReq.getContactEvt().getContactEvtId(),"0");
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("contactEvtItems", contactEvtItems);
//        maps.put("pageInfo", new Page(new PageInfo(contactEvtItems)));
        return maps;
    }

    /**
     * 删除事件采集项
     */
    @Override
    public Map<String, Object> delEventItem(ContactEvtItem contactEvtItem) {
        Map<String, Object> maps = new HashMap<>();
        contactEvtItemMapper.deleteByPrimaryKey(contactEvtItem.getEvtItemId());
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 查看事件采集项
     */
    @Override
    public Map<String, Object> viewEventItem(ContactEvtItem contactEvtItem) {
        Map<String, Object> maps = new HashMap<>();
        ContactEvtItem contactEvtItemR = contactEvtItemMapper.viewEventItem(contactEvtItem.getEvtItemId());
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("contactEvtItem", contactEvtItemR);
        return maps;
    }

    /**
     * 新增事件采集项
     */
    @Override
    public Map<String, Object> createEventItem(ContactEvtItem contactEvtItem) {
        Map<String, Object> maps = new HashMap<>();
        contactEvtItem.setCreateDate(DateUtil.getCurrentTime());
        contactEvtItem.setUpdateDate(DateUtil.getCurrentTime());
        contactEvtItem.setStatusDate(DateUtil.getCurrentTime());
        contactEvtItem.setUpdateStaff(UserUtil.loginId());
        contactEvtItem.setCreateStaff(UserUtil.loginId());
        contactEvtItem.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
        contactEvtItemMapper.insertContactEvtItem(contactEvtItem);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 修改事件采集项
     */
    @Override
    public Map<String, Object> modEventItem(ContactEvtItem contactEvtItem) {
        Map<String, Object> maps = new HashMap<>();
        contactEvtItem.setUpdateDate(DateUtil.getCurrentTime());
        contactEvtItem.setUpdateStaff(UserUtil.loginId());
        contactEvtItemMapper.modEventItem(contactEvtItem);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

}
