package com.zjtelcom.cpct.controller.campaign;

import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.campaign.MktAlgorithms;
import com.zjtelcom.cpct.service.campaign.MktAlgorithmsService;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;

@RestController
@RequestMapping("${adminPath}/mktAlgorithms")
public class MktAlgorithmsController extends BaseController {

    @Autowired
    private MktAlgorithmsService mktAlgorithmsService;

    /*
     **算法定义列表
     */
    @PostMapping("getMktAlgorithmsList")
    @CrossOrigin
    public Map<String, Object> getMktAlgorithmsList(@RequestBody HashMap<String,Object> params) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = mktAlgorithmsService.listMktAlgorithms(userId, params);
        }catch (Exception e){
            logger.error("[op:ServiceTypeController] fail to getServiceTypeList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getServiceTypeList");
            return result;
        }
        return result;
    }

    /*
     *创建算法定义
     */
    @PostMapping("createMktAlgorithms")
    @CrossOrigin
    public Map<String, Object> createMktAlgorithms(@RequestBody MktAlgorithms mktAlgorithms) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = mktAlgorithmsService.saveMktAlgorithms(userId, mktAlgorithms);
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to createService",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to createService");
            return result;
        }
        return result;
    }

    /*
     **编辑算法定义
     */
    @PostMapping("editMktAlgorithms")
    @CrossOrigin
    public Map<String, Object> editMktAlgorithms(@RequestBody MktAlgorithms mktAlgorithms) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = mktAlgorithmsService.updateMktAlgorithms(userId, mktAlgorithms);
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to editService",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to editService");
            return result;
        }
        return result;
    }

    /*
     **删除算法定义
     */
    @PostMapping("delMktAlgorithms")
    @CrossOrigin
    public Map<String, Object> delMktAlgorithms(@RequestBody MktAlgorithms mktAlgorithms) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = mktAlgorithmsService.deleteMktAlgorithms(userId, mktAlgorithms);
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to delService",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to delService");
            return result;
        }
        return result;
    }

    /*
     **算法定义详情
     */
    @PostMapping("getMktAlgorithmsDetail")
    @CrossOrigin
    public Map<String, Object> getMktAlgorithmsDetail(@RequestBody HashMap<String,Long> param) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            Long algoId = param.get("algoId");
            result = mktAlgorithmsService.getMktAlgorithms(userId, algoId);
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to getServiceDetail",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to delService");
            return result;
        }
        return result;
    }
}
