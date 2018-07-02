package com.zjtelcom.cpct.controller.channel;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.channel.ChannelAddVO;
import com.zjtelcom.cpct.dto.channel.ChannelEditVO;
import com.zjtelcom.cpct.dto.channel.ChannelVO;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.channel.ChannelService;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@RestController
@RequestMapping("${adminPath}/channel")
public class ChannelController extends BaseController {
    @Autowired
    private ChannelService channelService;


    /**
     * 添加渠道
     */
    @PostMapping("addChannel")
    @CrossOrigin
    public Map<String,Object> addChannel(ChannelAddVO addVO) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = channelService.addChannel(userId,addVO);
        } catch (Exception e) {
            logger.error("[op:ChannelController] fail to addChannel",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }
    /**
     * 编辑渠道
     */
    @PostMapping("editChannel")
    @CrossOrigin
    public Map<String,Object> editChannel( ChannelEditVO editVO) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = channelService.editChannel(userId,editVO);
        } catch (Exception e) {
            logger.error("[op:ChannelController] fail to editChannel",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

    /**
     * 删除渠道
     */
    @PostMapping("deleteChannel")
    @CrossOrigin
    public Map<String,Object> deleteChannel(Long channelId) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = channelService.deleteChannel(userId,channelId);
        } catch (Exception e) {
            logger.error("[op:ChannelController] fail to deleteChannel",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }


    /**
     * 获取渠道列表（渠道名称查询）
     */
    @GetMapping("getChannelList")
    @CrossOrigin
    public Map<String,Object> getChannelList(String channelName,Integer page, Integer pageSize) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = channelService.getChannelList(userId,channelName,page,pageSize);
        } catch (Exception e) {
            logger.error("[op:ChannelController] fail to deleteChannel",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

    /**
     * 获取渠道详情
     */
    @GetMapping("getChannelDetail")
    @CrossOrigin
    public Map<String,Object> getChannelDetail(Long channelId) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = channelService.getChannelDetail(userId,channelId);
        } catch (Exception e) {
            logger.error("[op:ChannelController] fail to deleteChannel",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

}
