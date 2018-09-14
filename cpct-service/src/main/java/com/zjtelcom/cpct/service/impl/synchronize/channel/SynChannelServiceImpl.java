package com.zjtelcom.cpct.service.impl.synchronize.channel;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.ContactChannelMapper;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.enums.SynchronizeType;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import com.zjtelcom.cpct.service.synchronize.channel.SynChannelService;
import com.zjtelcom.cpct_prd.dao.channel.ContactChannelPrdMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/9/14
 * @Description:渠道同步（准生产-生产）
 */
@Service
@Transactional
public class SynChannelServiceImpl implements SynChannelService {

    @Autowired
    private ContactChannelMapper contactChannelMapper;
    @Autowired
    private ContactChannelPrdMapper contactChannelPrdMapper;
    @Autowired
    private SynchronizeRecordService synchronizeRecordService;

    //同步表名
    private static final String tableName="contact_channel";

    /**
     * 单个渠道同步
     * @param channelId
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> synchronizeSingleChannel(Long channelId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        Channel channel = contactChannelMapper.selectByPrimaryKey(channelId);
        if(null==channel){
            throw new SystemException("对应渠道信息不存在!");
        }
        Channel channel1 = contactChannelPrdMapper.selectByPrimaryKey(channelId);
        if(null==channel1){
               contactChannelPrdMapper.insert(channel);
               synchronizeRecordService.addRecord(roleName,tableName,channelId, SynchronizeType.add.getType());
        }else{
               contactChannelPrdMapper.updateByPrimaryKey(channel);
               synchronizeRecordService.addRecord(roleName,tableName,channelId, SynchronizeType.update.getType());
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }


    /**
     * 批量同步渠道信息
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> synchronizeBatchChannel(String roleName) {
        Map<String,Object> maps = new HashMap<>();
        List<Channel> prdList = contactChannelMapper.selectAll("");
        List<Channel> realList = contactChannelPrdMapper.selectAll("");

        List<Channel> addList=new ArrayList<Channel>();
        List<Channel> updateList=new ArrayList<Channel>();
        List<Channel> deleteList=new ArrayList<Channel>();

        for(Channel c:prdList){
            for (int i = 0; i <realList.size() ; i++) {
                if(c.getContactChlId()-realList.get(i).getContactChlId()==0){
                    //需要修改的
                    updateList.add(c);
                    break;
                }else if(i==realList.size()-1){
                    //需要新增的  准生产存在，生产不存在
                    addList.add(c);
                }
            }
        }
        //查出需要删除的事件
        for(Channel c:realList){
            for (int i = 0; i <prdList.size() ; i++) {
                if(c.getContactChlId()-prdList.get(i).getContactChlId()==0){
                    break;
                }else if (i==prdList.size()-1){
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }

        //开始新增
        for(Channel c:addList){
            contactChannelPrdMapper.insert(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getContactChlId(), SynchronizeType.add.getType());
        }
        //开始修改
        for(Channel c:updateList){
            contactChannelPrdMapper.updateByPrimaryKey(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getContactChlId(), SynchronizeType.update.getType());
        }
        //开始删除
        for(Channel c:deleteList){
            contactChannelPrdMapper.deleteByPrimaryKey(c.getContactChlId());
            synchronizeRecordService.addRecord(roleName,tableName,c.getContactChlId(), SynchronizeType.delete.getType());
        }


        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);

        return maps;
    }
}
