package com.zjtelcom.cpct.service.impl.channel;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.dao.channel.ContactChannelMapper;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.dto.channel.*;
import com.zjtelcom.cpct.enums.ChannelType;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.ChannelService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.DateUtil;
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
    public Map<String, Object> getChannelTreeList(Long userId) {
        Map<String,Object> result = new HashMap<>();
        List<ChannelDetail> parentDetailList = new ArrayList<>();
        List<Channel> parentList = channelMapper.findParentList();
        for (Channel parent : parentList){
            ChannelDetail parentDetail = new ChannelDetail();
            List<Channel> childList = channelMapper.findChildListByParentId(parent.getContactChlId());
            List<ChannelDetail> childDetailList = new ArrayList<>();
            for (Channel child : childList){
                ChannelDetail childDetail = new ChannelDetail();
                childDetail.setChannelId(child.getContactChlId());
                childDetail.setChannelName(child.getContactChlName());
                childDetailList.add(childDetail);
            }
            parentDetail.setChannelId(parent.getParentId());
            parentDetail.setChannelName(parent.getContactChlName());
            parentDetail.setChildrenList(childDetailList);
            parentDetailList.add(parentDetail);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",parentDetailList);
        return result;



    }

    @Override
    public Map<String, Object> getChannelListByParentId(Long userId, Long parentId) {
        Map<String,Object> result = new HashMap<>();
        List<ChannelVO> voList = new ArrayList<>();
        List<Channel> childList = channelMapper.findChildListByParentId(parentId);
        for (Channel channel : childList){
            ChannelVO vo = ChannelUtil.map2ChannelVO(channel);
            voList.add(vo);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",voList);
        return result;
    }

    @Override
    public Map<String, Object> getParentList(Long userId) {
        Map<String,Object> result = new HashMap<>();
        List<ChannelVO> voList = new ArrayList<>();
        List<Channel> parentList = channelMapper.findParentList();
        for (Channel channel : parentList){
            ChannelVO vo = ChannelUtil.map2ChannelVO(channel);
            voList.add(vo);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",voList);
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
        result.put("resultMsg",voList);
        result.put("pageInfo",pageInfo);
        return result;
    }

    @Override
    public Map<String, Object> getChannelTreeForActivity(Long userId) {
        Map<String,Object> result = new HashMap<>();
        List<ChanDetailVO2> reList = new ArrayList<>();
//        List<ChannelDetail> resultList = new ArrayList<>();
//        List<ChannelDetail> initChannelList = new ArrayList<>();//主动
//        List<ChannelDetail> passiveChannelList = new ArrayList<>();//被动

        List<ChannelDetailVO> initVOList = new ArrayList<>();
        List<ChannelDetailVO> passVOList = new ArrayList<>();

        List<Channel> parentList = channelMapper.findParentList();
        for (Channel parent : parentList){
            int initCount = 0;
            int passCount = 0;
            List<Channel> childList = channelMapper.findChildListByParentId(parent.getContactChlId());
            List<ChannelDetail> initChildList = new ArrayList<>();
            List<ChannelDetail> passChildList = new ArrayList<>();
            for (Channel child : childList){
                if (child.getChannelType().equals(ChannelType.INITIATIVE.getValue().toString())){
                    ChannelDetail detail = getDetail(child);
                    detail.setChildrenList(null);
                    initChildList.add(detail);
                    initCount += 1;
                }else {
                    ChannelDetail detail = getDetail(child);
                    detail.setChildrenList(null);
                    passChildList.add(detail);
                    passCount += 1;
                }
            }
//            //所有主动渠道的渠道信息
//            if (initCount >0 ){
//                ChannelDetail detail = getDetail(parent);
//                detail.setChildrenList(initChildList);
//                initChannelList.add(detail);
//            }
//            if (passCount > 0){
//                ChannelDetail detail = getDetail(parent);
//                detail.setChildrenList(passChildList);
//                passiveChannelList.add(detail);
//            }


            //----------------
            //所有主动渠道的渠道信息
            if (initCount > 0){
                ChannelDetailVO detailVO = getDetailvo(parent);
                detailVO.setChildrenList(initChildList);
                initVOList.add(detailVO);
            }
            if (passCount > 0){
                ChannelDetailVO detailVO = getDetailvo(parent);
                detailVO.setChildrenList(passChildList);
                passVOList.add(detailVO);
            }
        }
//        ChannelDetail initDetail = new ChannelDetail();
//        initDetail.setChannelId(-1L);
//        initDetail.setChannelName("主动渠道");
//        initDetail.setChildrenList(initChannelList);
//        resultList.add(initDetail);
//        ChannelDetail passDetail = new ChannelDetail();
//        passDetail.setChannelId(-2L);
//        passDetail.setChannelName("被动渠道");
//        passDetail.setChildrenList(passiveChannelList);
//        resultList.add(passDetail);
//-------------------------------------
        ChanDetailVO2 initvo = new ChanDetailVO2();
        initvo.setChannelName("主动渠道");
        initvo.setChildrenList(initVOList);
        reList.add(initvo);
        ChanDetailVO2 passvo = new ChanDetailVO2();
        passvo.setChannelName("被动渠道");
        passvo.setChildrenList(passVOList);
        reList.add(passvo);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",reList);
        return result;

    }

    private ChannelDetail getDetail(Channel channel ){
        ChannelDetail detail = new ChannelDetail();
        detail.setChannelId(channel.getContactChlId());
        detail.setChannelName(channel.getContactChlName());
        return detail;
    }

    private ChannelDetailVO getDetailvo(Channel channel ){
        ChannelDetailVO detail = new ChannelDetailVO();
        detail.setChannelName(channel.getContactChlName());
        return detail;
    }

    /**
     * 添加父级渠道
     * @param userId
     * @param parentAddVO
     * @return
     */
    @Override
    public Map<String, Object> createParentChannel(Long userId, ContactChannelDetail parentAddVO) {
        Map<String,Object> result = new HashMap<>();
        Channel channel = BeanUtil.create(parentAddVO,new Channel());
        channel.setParentId(0L);
        channel.setChannelType(null);//主动被动
        channel.setCreateDate(new Date());
        channel.setUpdateDate(new Date());
        channel.setCreateStaff(userId);
        channel.setUpdateStaff(userId);
        channel.setStatusCd("1000");
        channelMapper.insert(channel);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");
        return result;
    }

    /**
     * 添加子渠道
     * @param userId
     * @param addVO
     * @return
     */
    @Override
    public Map<String,Object> createContactChannel(Long userId, ContactChannelDetail addVO) {
        Map<String,Object> result = new HashMap<>();
        if (addVO.getParentId()==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","请选择父级渠道添加");
            return result;
        }
        Channel parent = channelMapper.selectByPrimaryKey(addVO.getParentId());
        if (parent==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","父级渠道不存在");
            return result;
        }
        Channel channel = BeanUtil.create(addVO,new Channel());
        channel.setCreateDate(new Date());
        channel.setUpdateDate(new Date());
        channel.setCreateStaff(userId);
        channel.setUpdateStaff(userId);
        channel.setStatusCd("1000");
        channelMapper.insert(channel);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");
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
        result.put("resultMsg","添加成功");
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
        result.put("resultMsg","添加成功");
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
        result.put("resultMsg",voList);
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
        result.put("resultMsg",vo);
        return result;
    }
}
