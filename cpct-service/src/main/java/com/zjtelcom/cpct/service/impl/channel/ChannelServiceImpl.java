package com.zjtelcom.cpct.service.impl.channel;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.dao.channel.ContactChannelMapper;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.dto.channel.ContactChannelDetail;
import com.zjtelcom.cpct.dto.channel.ChannelEditVO;
import com.zjtelcom.cpct.dto.channel.ChannelVO;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.ChannelService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class ChannelServiceImpl extends BaseService implements ChannelService {

    @Autowired
    private ContactChannelMapper channelMapper;


    @Override
    public Map<String,Object> createContactChannel(Long userId, ContactChannelDetail addVO) {
        Map<String,Object> result = new HashMap<>();
        Channel channel = BeanUtil.create(addVO,new Channel());
        channel.setCreateDate(new Date());
        channel.setUpdateDate(new Date());
        channel.setCreateStaff(userId);
        channel.setUpdateStaff(userId);
        channel.setStatusCd("1000");
        channelMapper.insert(channel);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultData","添加成功");
        return result;
    }

    @Override
    public  Map<String,Object> modContactChannel(Long userId, ContactChannelDetail editVO) {
        Map<String,Object> result = new HashMap<>();
        Channel channel = channelMapper.selectByPrimaryKey(editVO.getChannelId());
        if (channel==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","渠道不存在");
            return result;
        }
        BeanUtil.copy(editVO,channel);
        channel.setUpdateDate(new Date());
        channel.setUpdateStaff(userId);
        channelMapper.updateByPrimaryKey(channel);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultData","添加成功");
        return result;
    }

    @Override
    public  Map<String,Object> delContactChannel(Long userId, ContactChannelDetail channelDetail) {
        Map<String,Object> result = new HashMap<>();
        Channel channel = channelMapper.selectByPrimaryKey(channelDetail.getChannelId());
        if (channel==null){
            result.put("resultCode",CODE_FAIL);

            result.put("resultMsg","渠道不存在");
            return result;
        }
        channelMapper.deleteByPrimaryKey(channelDetail.getChannelId());
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultData","添加成功");
        return result;
    }

    @Override
    public  Map<String,Object> getChannelList(Long userId,String channelName ,Integer page, Integer pageSize) {
        Map<String,Object> result = new HashMap<>();
        List<ChannelVO> voList = new ArrayList<>();
        PageHelper.startPage(page,pageSize);
        List<Channel> channelList = channelMapper.selectAll(channelName);
        Page pageInfo = new Page(new PageInfo(channelList));
        for (Channel channel : channelList){
            ChannelVO vo = ChannelUtil.map2ChannelVO(channel);
            voList.add(vo);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultData",voList);
        result.put("pageInfo",pageInfo);
        return result;
    }

    @Override
    public Map<String, Object> getChannelListByType(Long userId, String channelType) {
        Map<String,Object> result = new HashMap<>();
        List<ChannelVO> voList = new ArrayList<>();
        List<Channel> channelList = channelMapper.selectByType(channelType);
        for (Channel channel : channelList){
            ChannelVO vo = ChannelUtil.map2ChannelVO(channel);
            voList.add(vo);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultData",voList);
        return result;
    }

    @Override
    public  Map<String,Object> getChannelDetail(Long userId, Long channelId) {
        Map<String,Object> result = new HashMap<>();
        ChannelVO vo = new ChannelVO();
        try {
            Channel channel = channelMapper.selectByPrimaryKey(channelId);
            vo = ChannelUtil.map2ChannelVO(channel);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[op:ChannelServiceImpl] fail to listChannel ", e);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultData",vo);
        return result;
    }
}
