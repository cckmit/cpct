package com.zjtelcom.cpct.controller.channel;

import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.channel.ContactChannelDetail;
import com.zjtelcom.cpct.dto.channel.ChannelEditVO;
import com.zjtelcom.cpct.service.channel.ChannelService;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;

@RestController
@RequestMapping("${adminPath}/channel")
public class ChannelController extends BaseController {
    @Autowired
    private ChannelService channelService;


    /**
     * 添加父级渠道
     * @param parentAddVO
     * @return
     */
    @PostMapping("createParentChannel")
    @CrossOrigin
    public Map<String, Object> createParentChannel(@RequestBody ContactChannelDetail parentAddVO) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = channelService.createParentChannel(userId,parentAddVO);
        } catch (Exception e) {
            logger.error("[op:ChannelController] fail to createParentChannel",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to createParentChannel");
            return result;
        }
        return result;

    }

    /**
     * 获取渠道树
     * @return
     */
    @GetMapping("getChannelTreeForActivity")
    @CrossOrigin
    public Map<String, Object> getChannelTreeForActivity() {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = channelService.getChannelTreeForActivity(userId);
        } catch (Exception e) {
            logger.error("[op:ChannelController] fail to getChannelTreeForActivity",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getChannelTreeForActivity");
            return result;
        }
        return result;

    }

    /**
     * 添加渠道
     */
    @PostMapping("addChannel")
    @CrossOrigin
    public Map<String,Object> createContactChannel(@RequestBody ContactChannelDetail addVO) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = channelService.createContactChannel(userId,addVO);
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
    public Map<String,Object> modContactChannel(@RequestBody ContactChannelDetail editVO) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = channelService.modContactChannel(userId,editVO);
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
    public Map<String,Object> delContactChannel(@RequestBody ContactChannelDetail channelDetail) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = channelService.delContactChannel(userId,channelDetail);
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
     * 获取渠道列表（渠道类型查询）
     */
    @GetMapping("getChannelListByType")
    @CrossOrigin
    public Map<String, Object> getChannelListByType( String channelType) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        if (channelType==null || channelType.equals("")){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","请输入渠道类型");
            return result;
        }
        try {
            result = channelService.getChannelListByType(userId,channelType);
        } catch (Exception e) {
            logger.error("[op:ChannelController] fail to getChannelListByType",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getChannelListByType");
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
