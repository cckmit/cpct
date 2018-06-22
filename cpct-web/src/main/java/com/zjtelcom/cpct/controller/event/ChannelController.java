package com.zjtelcom.cpct.controller.event;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.domain.event.EventSorce;
import com.zjtelcom.cpct.dto.ChannelAddVO;
import com.zjtelcom.cpct.dto.ChannelEditVO;
import com.zjtelcom.cpct.dto.ChannelVO;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@RestController
@RequestMapping("${adminPath}/channel")
public class ChannelController extends BaseController {
    @Autowired
    private ChannelService channelService;


    @PostMapping("addChannel")
    @CrossOrigin
    public RespInfo addChannel(Long userId, ChannelAddVO addVO) {
        RespInfo respInfo = new RespInfo();
        try {
            respInfo = channelService.addChannel(userId,addVO);
        } catch (Exception e) {
            logger.error("[op:ChannelController] fail to addChannel",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.ADD_CHANNEL_FAILURE.getErrorMsg(),ErrorCode.ADD_CHANNEL_FAILURE.getErrorCode());
        }
        return respInfo;
    }

    @PostMapping("editChannel")
    @CrossOrigin
    public RespInfo editChannel(Long userId, ChannelEditVO editVO) {
        RespInfo respInfo = new RespInfo();
        try {
            respInfo = channelService.editChannel(userId,editVO);
        } catch (Exception e) {
            logger.error("[op:ChannelController] fail to editChannel",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.EDIT_CHANNEL_FAILURE.getErrorMsg(),ErrorCode.EDIT_CHANNEL_FAILURE.getErrorCode());
        }
        return respInfo;
    }

    @PostMapping("deleteChannel")
    @CrossOrigin
    public RespInfo deleteChannel(Long userId, Long channelId) {
        RespInfo respInfo = new RespInfo();
        try {
            respInfo = channelService.deleteChannel(userId,channelId);
        } catch (Exception e) {
            logger.error("[op:ChannelController] fail to deleteChannel",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.DELETE_CHANNEL_FAILURE.getErrorMsg(),ErrorCode.DELETE_CHANNEL_FAILURE.getErrorCode());
        }
        return respInfo;
    }

    @GetMapping("getChannelList")
    @CrossOrigin
    public RespInfo getChannelList(Long userId,String channelName,Integer page, Integer pageSize) {
        List<ChannelVO> voList = new ArrayList<>();
        try {
            voList = channelService.getChannelList(userId,channelName,page,pageSize);
        } catch (Exception e) {
            logger.error("[op:ChannelController] fail to deleteChannel",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.GET_CHANNEL_LIST.getErrorMsg(),ErrorCode.GET_CHANNEL_LIST.getErrorCode());
        }
        return RespInfo.build(CODE_SUCCESS,voList);
    }

    @GetMapping("getChannelDetail")
    @CrossOrigin
    public RespInfo getChannelDetail(Long userId, Long channelId) {
        ChannelVO vo = new ChannelVO();
        try {
            vo = channelService.getChannelDetail(userId,channelId);
        } catch (Exception e) {
            logger.error("[op:ChannelController] fail to deleteChannel",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.GET_CHANNEL_DETAIL.getErrorMsg(),ErrorCode.GET_CHANNEL_DETAIL.getErrorCode());
        }
        return RespInfo.build(CODE_SUCCESS,vo);
    }

}
