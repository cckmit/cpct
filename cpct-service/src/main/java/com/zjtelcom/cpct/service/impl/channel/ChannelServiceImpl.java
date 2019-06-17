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
import com.zjtelcom.cpct.service.synchronize.channel.SynChannelService;
import com.zjtelcom.cpct.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

/*import com.zjtelcom.cpct.service.impl.api.ClTest;*/

@Service
public class ChannelServiceImpl extends BaseService implements ChannelService {

    @Autowired
    private ContactChannelMapper channelMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private SynChannelService synChannelService;



/*
    @Autowired
    private ClTestRepository testRepository;*/


    @Override
    public Map<String, Object> listAllChildChannelList() {
        Map<String,Object> result = new HashMap<>();
        List<Channel> channelList = channelMapper.findChildList();
        List<String> resultList = new ArrayList<>();
        List<Channel> resultChannelList = new ArrayList<>();
        for (Channel channel : channelList){
            if (resultList.contains(channel.getContactChlCode())){
                continue;
            }
            resultList.add(channel.getContactChlCode());
            resultChannelList.add(channel);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",resultChannelList);
        return result;
    }


    @Override
    public Map<String, Object> listChannelByIdList(List<Long> idList) {
        Map<String,Object> result = new HashMap<>();
        List<Channel> channelList = new ArrayList<>();
        for (Long id : idList){
            Channel channel = channelMapper.selectByPrimaryKey(id);
            if (channel==null){
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","渠道不存在");
                return result;
            }
            channelList.add(channel);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",channelList);
        return result;

    }

    @Override
    public Map<String, Object> listChannelTree(Long userId,String channelName) {

        Map<String,Object> result = new HashMap<>();
        Channel channel = channelMapper.selectChannel4AllChannel(-1L);
        List<ChannelDetail> chList = new ArrayList<>();
        List<ChannelDetail> parentDetailList = new ArrayList<>();
        List<Channel> parentList = channelMapper.findParentList();
        listParent(parentDetailList, parentList,channelName);
        ChannelDetail allChannel = new ChannelDetail();
        allChannel.setChannelName(channel.getContactChlName());
        allChannel.setChannelId(channel.getContactChlId());
        allChannel.setChildren(parentDetailList);
        chList.add(allChannel);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",chList);
        return result;
    }

    private void listParent(List<ChannelDetail> parentDetailList, List<Channel> parentList,String channelName) {
        for (Channel parent : parentList){
            ChannelDetail parentDetail = new ChannelDetail();
            List<Channel> childList = channelMapper.findChildListByParentId(parent.getContactChlId());
            List<ChannelDetail> childDetailList = new ArrayList<>();
            for (Channel child : childList){
                if (channelName!=null && !channelName.equals("") && !child.getContactChlName().contains(channelName)){
                    continue;
                }
                ChannelDetail childDetail = new ChannelDetail();
                childDetail.setChannelId(child.getContactChlId());
                childDetail.setChannelName(child.getContactChlName());
                childDetail.setChannelCode(child.getContactChlCode());
                if (child.getRemark()!=null && !child.getRemark().equals("")){
                    childDetail.setRemark(child.getRemark());
                }
                childDetailList.add(childDetail);
            }
            if (channelName!=null && !channelName.equals("")){
                if (childDetailList.isEmpty() && !parent.getContactChlName().contains(channelName)){
                    continue;
                }
            }
            parentDetail.setChannelId(parent.getContactChlId());
            parentDetail.setChannelName(parent.getContactChlName());
            parentDetail.setChannelCode(parent.getContactChlCode());
            parentDetail.setChildren(childDetailList);
            parentDetailList.add(parentDetail);
        }
    }

    @Override
    public Map<String, Object> getChannelTreeList(Long userId) {
        Map<String,Object> result = new HashMap<>();
        List<ChannelDetail> parentDetailList = new ArrayList<>();
        List<Channel> parentList = channelMapper.findParentList();
        listParent(parentDetailList, parentList,null);
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
                    detail.setChildren(null);
                    initChildList.add(detail);
                    initCount += 1;
                }else {
                    ChannelDetail detail = getDetail(child);
                    detail.setChildren(null);
                    passChildList.add(detail);
                    passCount += 1;
                }
            }
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
        Channel ch = channelMapper.selectByCode(parentAddVO.getContactChlCode());
        if (ch!=null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","渠道编码已存在");
            return result;
        }
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
     * 添加渠道
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
        if (parent.getParentId()!=0){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","该根目录节点下不允许新增子节点");
            return result;
        }
        String channelCode = "CHL"+DateUtil.date2String(new Date())+ChannelUtil.getRandomStr(4);
        Channel exist = channelMapper.selectByCode(channelCode);
        if (exist!=null){
             channelCode = "CHL"+DateUtil.date2String(new Date())+ChannelUtil.getRandomStr(4);
        }
        final Channel channel = BeanUtil.create(addVO,new Channel());
        Channel ch = channelMapper.selectChannel4AllChannel(-1L);
        if (addVO.getParentId().equals(ch.getContactChlId())){
            channel.setParentId(0L);
        }
        channel.setContactChlCode(channelCode);
        channel.setCreateDate(new Date());
        channel.setUpdateDate(new Date());
        channel.setCreateStaff(userId);
        channel.setUpdateStaff(userId);
        channel.setStatusCd("1000");
        channel.setChannelType("0");
        channel.setContactChlType("100000");
        channelMapper.insert(channel);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");

        if (SystemParamsUtil.isSync()){
            new Thread(){
                public void run(){
                    try {
                        synChannelService.synchronizeSingleChannel(channel.getContactChlId(),"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        return result;
    }

    @Override
    public  Map<String,Object> modContactChannel(Long userId, ContactChannelDetail editVO) {
        Map<String,Object> result = new HashMap<>();
        final Channel channel = channelMapper.selectByPrimaryKey(editVO.getChannelId());
        if (channel==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","渠道不存在");
            return result;
        }
        if (!channel.getContactChlName().equals(editVO.getContactChlName())){
            channel.setContactChlName(editVO.getContactChlName());
        }
        if (!channel.getContactChlCode().equals(editVO.getContactChlCode())){
            channel.setContactChlCode(editVO.getContactChlCode());
        }
        channel.setUpdateDate(new Date());
        channel.setUpdateStaff(userId);
        channelMapper.updateByPrimaryKey(channel);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","编辑成功");

        if (SystemParamsUtil.isSync()){
            new Thread(){
                public void run(){
                    try {
                        synChannelService.synchronizeSingleChannel(channel.getContactChlId(),"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        return result;
    }

    @Override
    public  Map<String,Object> delContactChannel(Long userId, ContactChannelDetail channelDetail) {
        Map<String,Object> result = new HashMap<>();
        final Channel channel = channelMapper.selectByPrimaryKey(channelDetail.getChannelId());
        if (channel==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","渠道不存在");
            return result;
        }
        if (channel.getContactChlName().equals("所有渠道")){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","根渠道无法删除！");
            return result;
        }
        channelMapper.deleteByPrimaryKey(channelDetail.getChannelId());
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","删除成功");

        if (SystemParamsUtil.isSync()){
            new Thread(){
                public void run(){
                    try {
                        synChannelService.deleteSingleChannel(channel.getContactChlId(),"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

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
            if (channel==null){
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","渠道不存在");
                return result;
            }
            vo = ChannelUtil.map2ChannelVO(channel);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[op:ChannelServiceImpl] fail to listChannel ", e);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",vo);
        return result;
    }

    @Override
    public Object addAcount() {
/*            ClTest clTest = new ClTest();
            clTest.setId(Long.valueOf(ChannelUtil.getRandomStr(5)));
            testRepository.save(clTest);
            logger.info("333333");*/

        return "success";
    }



}
