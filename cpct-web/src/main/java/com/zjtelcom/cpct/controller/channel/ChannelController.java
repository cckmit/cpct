package com.zjtelcom.cpct.controller.channel;

import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.dto.channel.ContactChannelDetail;
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


    @PostMapping("listAllChildChannelList")
    @CrossOrigin
    public Map<String, Object> listAllChildChannelList() {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = channelService.listAllChildChannelList();
        } catch (Exception e) {
            logger.error("[op:ChannelController] fail to listChannelByIdList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to listChannelByIdList");
            return result;
        }
        return result;
    }


    /**
     *活动页--获取渠道
     * @param param
     * @return
     */
    @PostMapping("listChannelByIdList")
    @CrossOrigin
    public Map<String, Object> listChannelByIdList(@RequestBody HashMap<String,List<Long>> param ) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            List<Long> idList = param.get("idList");
            result = channelService.listChannelByIdList(idList);
        } catch (Exception e) {
            logger.error("[op:ChannelController] fail to listChannelByIdList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to listChannelByIdList");
            return result;
        }
        return result;
    }

    /**
     * 渠道管理渠道树
     * @return
     */
    @PostMapping("listChannelTree")
    @CrossOrigin
    public Map<String, Object> listChannelTree(@RequestBody HashMap<String,String> param) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = channelService.listChannelTree(userId,param.get("channelName"));
        } catch (Exception e) {
            logger.error("[op:ChannelController] fail to listChannelTree",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to listChannelTree");
            return result;
        }
        return result;
    }

    /**
     *渠道-渠道树
     * @return
     */
    @PostMapping("getChannelTreeList")
    @CrossOrigin
    public Map<String, Object> getChannelTreeList(@RequestBody HashMap<String,String> param) {
        Map<String,Object> result = new HashMap<>();
        try {
            result = channelService.getChannelTreeList(param);
        } catch (Exception e) {
            logger.error("[op:ChannelController] fail to getParentList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getParentList");
            return result;
        }
        return result;
    }


    /**
     * 获取子级列表
     * @return
     */
    @PostMapping("getChannelListByParentId")
    @CrossOrigin
    public Map<String, Object> getChannelListByParentId(@RequestBody HashMap<String,Long> param) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = channelService.getChannelListByParentId(userId,param.get("parentId"));
        } catch (Exception e) {
            logger.error("[op:ChannelController] fail to getParentList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getParentList");
            return result;
        }
        return result;
    }
    /**
     * 获取父级列表
     * @return
     */
    @GetMapping("getParentList")
    @CrossOrigin
    public Map<String, Object> getParentList() {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = channelService.getParentList(userId);
        } catch (Exception e) {
            logger.error("[op:ChannelController] fail to getParentList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getParentList");
            return result;
        }
        return result;
    }

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
    @PostMapping("getChannelDetail")
    @CrossOrigin
    public Map<String,Object> getChannelDetail(@RequestBody HashMap<String,Long> param) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            Long channelId = param.get("channelId");
            result = channelService.getChannelDetail(userId,channelId);
        } catch (Exception e) {
            logger.error("[op:ChannelController] fail to deleteChannel",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }


    /**
     * 通过渠道编码批量查询渠道
     */
    @PostMapping("selectBatchByCode")
    @CrossOrigin
    public Map<String,Object> selectBatchByCode(@RequestBody Map<String,Object> param) {
        Map<String,Object> result = new HashMap<>();
        try {
            List<String> contactChlCodeList =  (List<String>) param.get("contactChlCodeList");
            result = channelService.selectBatchByCode(contactChlCodeList);
        } catch (Exception e) {
            logger.error("[op:ChannelController] fail to selectBatchByCode",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg", "通过渠道编码批量查询渠道失败");
            return result;
        }
        return result;
    }


    /**
     * 通过UAM获取服务密码
     */
    @PostMapping("getUamServicePswd")
    @CrossOrigin
    public Map<String,Object> getUamServicePswd(@RequestBody Map<String,Object> param) {
        Map<String,Object> result = new HashMap<>();
        try {
            String accountID = (String)param.get("accountID");
            String areaCode = (String)param.get("areaCode");
            String custID = (String)param.get("custID");

            result = channelService.getUamServicePswd(accountID,areaCode,custID);
        } catch (Exception e) {
            logger.error("[op:ChannelController] fail to getUamServicePswd",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg", "通过uam获取服务密码失败" + e);
            return result;
        }
        return result;
    }

    /**
     * 门店名称模糊查询
     */
    @PostMapping("getChannelByChannelName")
    @CrossOrigin
    public Map<String,Object> getChannelByChannelName(@RequestBody Map<String,Object> param) {
        Map<String,Object> resultMap = new HashMap<>();
        Map<String,Object> dataMap = new HashMap<>();
        try {
            String channelParam = (String)param.get("channelParam");
            Integer pageSize = (Integer)param.get("pageSize");
            Integer page = (Integer)param.get("page");
            logger.info(param.toString());
            dataMap = channelService.getChannelByChannelName(channelParam, pageSize.intValue(),page.intValue());

        } catch (Exception e) {
            logger.error("[op:ChannelController] fail to getChannelByChannelName",e);
            resultMap.put("resultCode",CODE_FAIL);
            resultMap.put("resultMsg", "门店名称模糊查询失败");
            return resultMap;
        }
        resultMap.put("resultCode",CODE_SUCCESS);
        resultMap.put("resultMessage","消息返回成功");
        resultMap.put("data",dataMap);
        return resultMap;
    }

    /**
     * 门店 id获取名称
     */
    @PostMapping("getAllChannelById")
    @CrossOrigin
    public Map<String,Object> getAllChannelById(@RequestBody Map<String,Object> param) {
        Map<String,Object> resultMap = new HashMap<>();
        List<Channel> channelNameList = new ArrayList<>();
        try {
            List<Integer> channelId = (List)param.get("channelId");
            channelNameList = channelService.getAllChannelById(channelId);

        } catch (Exception e) {
            logger.error("[op:ChannelController] fail to getChannelByChannelName",e);
            resultMap.put("resultCode",CODE_FAIL);
            resultMap.put("resultMsg", "门店名称查询失败");
            return resultMap;
        }
        resultMap.put("resultCode",CODE_SUCCESS);
        resultMap.put("resultMessage","消息返回成功");
        resultMap.put("data",channelNameList);
        return resultMap;
    }
}
