package com.zjtelcom.cpct.controller.channel;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dao.channel.IntfCfgParamMapper;
import com.zjtelcom.cpct.domain.channel.IntfCfgParam;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.*;

@RestController
@RequestMapping("${adminPath}/intfCfgParam")
public class IntfCfgParamController extends BaseController {

    @Autowired
    private IntfCfgParamMapper intfCfgParamMapper;

    /*
     **创建事件源接口配置参数表
     */
    @PostMapping("createIntfCfgParam")
    @CrossOrigin
    public Map<String, Object> createIntfCfgParam(@RequestBody Map<String,Object> params) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            IntfCfgParam intfCfgParams = JSON.parseObject(JSON.toJSONString(params), IntfCfgParam.class);
            intfCfgParams.setCreateStaff(userId);
            intfCfgParams.setCreateDate(new Date());
            intfCfgParams.setUpdateStaff(userId);
            intfCfgParams.setUpdateDate(new Date());
            intfCfgParams.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
            intfCfgParams.setStatusDate(new Date());
            intfCfgParamMapper.insert(intfCfgParams);
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","添加成功");
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to createIntfCfgParam",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to createIntfCfgParam");
            return result;
        }
        return result;
    }

    /*
     **编辑事件源接口配置参数表
     */
    @PostMapping("editIntfCfgParam")
    @CrossOrigin
    public Map<String, Object> editIntfCfgParam(@RequestBody IntfCfgParam intfCfgParam) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            IntfCfgParam intfCfgParams = intfCfgParamMapper.selectByPrimaryKey(intfCfgParam.getIntfCfgParamId());
            if(intfCfgParams == null) {
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","事件源接口配置参数表不存在");
                return result;
            }
            BeanUtil.copy(intfCfgParam, intfCfgParams);
            intfCfgParams.setUpdateDate(new Date());
            intfCfgParams.setUpdateStaff(userId);
            intfCfgParamMapper.updateByPrimaryKey(intfCfgParams);
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","修改成功");
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to editIntfCfgParam",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to editIntfCfgParam");
            return result;
        }
        return result;
    }

    /*
     **删除事件源接口配置参数表
     */
    @PostMapping("delIntfCfgParam")
    @CrossOrigin
    public Map<String, Object> delIntfCfgParam(@RequestBody IntfCfgParam intfCfgParam) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            IntfCfgParam intfCfgParams = intfCfgParamMapper.selectByPrimaryKey(intfCfgParam.getIntfCfgParamId());
            if(intfCfgParams == null) {
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","事件源接口配置参数表不存在");
                return result;
            }
            intfCfgParamMapper.deleteByPrimaryKey(intfCfgParam.getIntfCfgParamId());
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","删除成功");
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to delIntfCfgParam",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to delIntfCfgParam");
            return result;
        }
        return result;
    }

    /*
     **获取事件源接口配置参数表详情
     */
    @PostMapping("getIntfCfgParamDetail")
    @CrossOrigin
    public Map<String, Object> getIntfCfgParamDetail(@RequestBody HashMap<String,Long> param) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            Long intfCfgParamId = param.get("intfCfgParamId");
            IntfCfgParam intfCfgParam = intfCfgParamMapper.selectByPrimaryKey(intfCfgParamId);
            if(intfCfgParam == null) {
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","事件源接口配置参数表不存在");
                return result;
            }
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg",intfCfgParam);
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to getIntfCfgParamDetail",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getIntfCfgParamDetail");
            return result;
        }
        return result;
    }
}
