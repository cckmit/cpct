package com.zjtelcom.cpct.open.serviceImpl.channel;

import com.zjtelcom.cpct.dao.channel.ContactChannelMapper;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.common.CommonUtil;
import com.zjtelcom.cpct.open.base.service.BaseService;
import com.zjtelcom.cpct.open.service.channel.OpenChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/10/31
 * @Description:触点渠道openapi相关服务
 */
@Service
@Transactional
public class OpenChannelServiceImpl extends BaseService implements OpenChannelService {

    @Autowired
    private ContactChannelMapper contactChannelMapper;

    /**
     * 查询触点渠道信息
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> queryById(String id) {
        Map<String, Object> resultMap = new HashMap<>();
        long queryId= CommonUtil.stringToLong(id);
        Channel channel = contactChannelMapper.selectByPrimaryKey(queryId);
        if(null==channel){
            throw new SystemException("对应触点渠道信息不存在!");
        }
        resultMap.put("params",channel);
        return resultMap;
    }


    /**
     * 新增触点渠道
     * @param object
     * @return
     */
    @Override
    public Map<String, Object> addByObject(Object object) {
        Map<String, Object> resultMap = new HashMap<>();
        Channel channel= (Channel) object;
        contactChannelMapper.updateByPrimaryKey(channel);
        Channel result = contactChannelMapper.selectByPrimaryKey(channel.getContactChlId());
        resultMap.put("params",result);
        return resultMap;
    }


    /**
     * 修改触点渠道信息
     * @param id
     * @param object
     * @return
     */
    @Override
    public Map<String, Object> updateByParams(String id, Object object) {
        Map<String, Object> resultMap = new HashMap<>();
        Long queryId = CommonUtil.stringToLong(id);
        Channel channel = contactChannelMapper.selectByPrimaryKey(queryId);
        if(null==channel){
            throw new SystemException("对应触点渠道信息不存在!");
        }
        contactChannelMapper.updateByPrimaryKey((Channel) object);
        resultMap.put("params",(Channel) object);
        return resultMap;
    }

    /**
     * 删除触点渠道信息
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> deleteById(String id) {
        Map<String, Object> resultMap = new HashMap<>();
        long queryId= CommonUtil.stringToLong(id);
        contactChannelMapper.deleteByPrimaryKey(queryId);
        return resultMap;
    }


    /**
     * 查询触点渠道信息列表
     * @param map
     * @return
     */
    @Override
    public Map<String, Object> queryListByMap(Map<String, Object> map) {
        Map<String, Object> resultMap = new HashMap<>();
        Channel channel=new Channel();
        channel.setContactChlId(CommonUtil.stringToLong((String) map.get("contactChlId")));
        channel.setContactChlCode((String) map.get("contactChlCode"));
        channel.setContactChlName((String) map.get("contactChlName"));
        channel.setChannelType((String) map.get("contactChlType"));
        channel.setContactChlDesc((String) map.get("contactChlDesc"));
        channel.setStatusCd((String) map.get("statusCd"));
        List<Channel> list = contactChannelMapper.queryList(channel);
        CommonUtil.setPage(map);


        resultMap.put("params","");
        return resultMap;
    }
}
