package com.zjtelcom.cpct.controller.channel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.Rule;
import com.zjtelcom.cpct.domain.RuleDetail;
import com.zjtelcom.cpct.domain.channel.MktVerbalCondition;
import com.zjtelcom.cpct.dto.channel.VerbalAddVO;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.channel.VerbalService;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;

@RestController
@RequestMapping("verbal")
public class VerbalController extends BaseController {
    @Autowired
    private VerbalService verbalService;


    /**
     * 添加痛痒点话术
     */
    @PostMapping("addVerbal")
    public Map<String,Object> addVerbal(@RequestBody VerbalAddVO addVO) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = verbalService.addVerbal(userId,addVO);
        } catch (Exception e) {
            logger.error("[op:VerbalController] fail to addVerbal",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addVerbal");
            return result;
        }
        return result;
    }

    /**
     * 根据渠道推送配置id获取痛痒点话术列表
     */
    @GetMapping("getVerbalListByConfId")
    public Map<String,Object> getVerbalListByConfId(Long confId) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = verbalService.getVerbalListByConfId(userId,confId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:VerbalController] fail to getVerbalListByConfId",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getVerbalListByConfId");
            return result;
        }
        return result;

    }

    /**
     * 获取痛痒点话术详情
     */
    @GetMapping("getVerbalDetail")
    public Map<String,Object> getVerbalDetail( Long verbalId) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = verbalService.getVerbalDetail(userId,verbalId);
        } catch (Exception e) {
            logger.error("[op:VerbalController] fail to getVerbalDetail",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getVerbalDetail");
            return result;
        }
        return result;
    }






}
