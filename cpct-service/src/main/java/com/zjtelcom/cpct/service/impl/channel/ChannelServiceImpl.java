package com.zjtelcom.cpct.service.impl.channel;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.dao.channel.ContactChannelMapper;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.dto.ChannelAddVO;
import com.zjtelcom.cpct.dto.ChannelEditVO;
import com.zjtelcom.cpct.dto.ChannelVO;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.ChannelService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class ChannelServiceImpl extends BaseService implements ChannelService {

    @Autowired
    private ContactChannelMapper channelMapper;


    @Override
    public RespInfo addChannel(Long userId, ChannelAddVO addVO) {
        Channel channel = BeanUtil.create(addVO,new Channel());
        channel.setCreateDate(new Date());
        channel.setUpdateDate(new Date());
        channel.setCreateStaff(userId);
        channel.setUpdateStaff(userId);
        channel.setStatusCd("1000");
        channelMapper.insert(channel);
        return RespInfo.build(CODE_SUCCESS,"添加成功");
    }

    @Override
    public RespInfo editChannel(Long userId, ChannelEditVO editVO) {
        Channel channel = channelMapper.selectByPrimaryKey(editVO.getChannelId());
        if (channel==null){
            return RespInfo.build(CODE_FAIL,"渠道信息不存在");
        }
        BeanUtil.copy(editVO,channel);
        channel.setUpdateDate(new Date());
        channel.setUpdateStaff(userId);
        channelMapper.updateByPrimaryKey(channel);
        return RespInfo.build(CODE_SUCCESS,"修改成功");
    }

    @Override
    public RespInfo deleteChannel(Long userId, Long channelId) {
        Channel channel = channelMapper.selectByPrimaryKey(channelId);
        if (channel==null){
            return RespInfo.build(CODE_FAIL,"渠道信息不存在");
        }
        channelMapper.deleteByPrimaryKey(channelId);
        return RespInfo.build(CODE_SUCCESS,"删除成功");
    }

    @Override
    public List<ChannelVO> getChannelList(Long userId,String channelName ,Integer page, Integer pageSize) {
        List<ChannelVO> voList = new ArrayList<>();
        List<Channel> channelList = new ArrayList<>();
        try {
            channelList = channelMapper.selectAll(channelName);
            for (Channel channel : channelList){
                ChannelVO vo = ChannelUtil.map2ChannelVO(channel);
                voList.add(vo);
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[op:ChannelServiceImpl] fail to listChannel ", e);
        }
        return voList;
    }

    @Override
    public ChannelVO getChannelDetail(Long userId, Long channelId) {
        ChannelVO vo = new ChannelVO();
        try {
            Channel channel = channelMapper.selectByPrimaryKey(channelId);
            vo = ChannelUtil.map2ChannelVO(channel);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[op:ChannelServiceImpl] fail to listChannel ", e);
        }
        return vo;
    }
}
