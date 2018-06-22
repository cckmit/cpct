package com.zjtelcom.cpct.controller.event;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.dto.ChannelAddVO;
import com.zjtelcom.cpct.dto.ChannelEditVO;
import com.zjtelcom.cpct.dto.ChannelVO;
import com.zjtelcom.cpct.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${adminPath}/channel")
public class ChannelController {
    @Autowired
    private ChannelService channelService;


    @PostMapping("addChannel")
    public RespInfo addChannel(Long userId, ChannelAddVO addVO) {
        return channelService.addChannel(userId,addVO);
    }

    @PostMapping("editChannel")
    public RespInfo editChannel(Long userId, ChannelEditVO editVO) {
        return channelService.editChannel(userId,editVO);
    }

    @PostMapping("deleteChannel")
    public RespInfo deleteChannel(Long userId, Long channelId) {
        return channelService.deleteChannel(userId,channelId);
    }

    @GetMapping("getChannelList")
    public List<ChannelVO> getChannelList(Long userId, Integer page, Integer pageSize) {
        return channelService.getChannelList(userId,page,pageSize);
    }

    @GetMapping("getChannelDetail")
    public ChannelVO getChannelDetail(Long userId, Long channelId) {
        return channelService.getChannelDetail(userId,channelId);
    }

}
