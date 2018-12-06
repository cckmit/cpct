package com.zjtelcom.cpct.controller.channel;

import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.channel.VerbalAddVO;
import com.zjtelcom.cpct.dto.channel.VerbalEditVO;
import com.zjtelcom.cpct.service.channel.VerbalService;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;

@RestController
@RequestMapping("${adminPath}/verbal")
public class VerbalController extends BaseController {
    @Autowired
    private VerbalService verbalService;


    /**
     * 编辑痛痒点脚本
     * @param editVO
     * @return
     */
    @PostMapping("editVerbal")
    @CrossOrigin
    public Map<String,Object> editVerbal(@RequestBody VerbalEditVO editVO) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = verbalService.editVerbal(userId,editVO);
        } catch (Exception e) {
            logger.error("[op:VerbalController] fail to editVerbal",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to editVerbal");
            return result;
        }
        return result;
    }

    /**
     * 痛痒点脚本删除
     * @param param
     * @return
     */
    @PostMapping("delVerbal")
    @CrossOrigin
    public Map<String, Object> delVerbal(@RequestBody HashMap<String,Long> param) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            if (param.get("verbalId")==null){
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","请选择脚本");
                return result;
            }
            result = verbalService.delVerbal(userId,param.get("verbalId"));
        } catch (Exception e) {
            logger.error("[op:VerbalController] fail to delVerbal",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to delVerbal");
            return result;
        }
        return result;
    }

    /**
     * 添加痛痒点话术
     */
    @PostMapping("addVerbal")
    @CrossOrigin
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
    @PostMapping("getVerbalListByConfId")
    @CrossOrigin
    public Map<String,Object> getVerbalListByConfId(@RequestBody HashMap<String,Long> param) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            if (param.get("confId")==null){
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","请选择推送渠道");
                return result;
            }
            Long confId = param.get("confId");
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
    @PostMapping("getVerbalDetail")
    @CrossOrigin
    public Map<String,Object> getVerbalDetail(@RequestBody HashMap<String,Long> param) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            if (param.get("verbalId")==null){
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","请选择话术");
                return result;
            }
            result = verbalService.getVerbalDetail(userId,param.get("verbalId"));
        } catch (Exception e) {
            logger.error("[op:VerbalController] fail to getVerbalDetail",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getVerbalDetail");
            return result;
        }
        return result;
    }






}
