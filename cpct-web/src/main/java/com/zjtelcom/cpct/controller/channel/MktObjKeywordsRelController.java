package com.zjtelcom.cpct.controller.channel;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dao.channel.MktObjKeywordsRelMapper;
import com.zjtelcom.cpct.domain.channel.MktObjKeywordsRel;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@RestController
@RequestMapping("${adminPath}/mktObjKeywordsRel")
public class MktObjKeywordsRelController extends BaseController {

    @Autowired
    private MktObjKeywordsRelMapper mktObjKeywordsRelMapper;

    /*
     **创建对象关键字
     */
    @PostMapping("createMktObjKeywordsRel")
    @CrossOrigin
    public Map<String, Object> createMktObjKeywordsRel(@RequestBody Map<String,Object> params) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            MktObjKeywordsRel mktObjKeywords = JSON.parseObject(JSON.toJSONString(params), MktObjKeywordsRel.class);
            mktObjKeywords.setCreateStaff(userId);
            mktObjKeywords.setCreateDate(new Date());
            mktObjKeywords.setUpdateStaff(userId);
            mktObjKeywords.setUpdateDate(new Date());
            mktObjKeywords.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
            mktObjKeywords.setStatusDate(new Date());
            mktObjKeywordsRelMapper.insert(mktObjKeywords);
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","添加成功");
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to createMktObjKeywordsRel",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to createMktObjKeywordsRel");
            return result;
        }
        return result;
    }

    /*
     **编辑对象关键字
     */
    @PostMapping("editMktObjKeywordsRel")
    @CrossOrigin
    public Map<String, Object> editMktObjKeywordsRel(@RequestBody MktObjKeywordsRel mktObjKeywordsRel) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            MktObjKeywordsRel mktObjKeywords = mktObjKeywordsRelMapper.selectByPrimaryKey(mktObjKeywordsRel.getRelId());
            if(mktObjKeywords == null) {
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","事件源接口配置参数表不存在");
                return result;
            }
            BeanUtil.copy(mktObjKeywordsRel, mktObjKeywords);
            mktObjKeywords.setUpdateDate(new Date());
            mktObjKeywords.setUpdateStaff(userId);
            mktObjKeywordsRelMapper.updateByPrimaryKey(mktObjKeywords);
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","修改成功");
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to editMktObjKeywordsRel",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to editMktObjKeywordsRel");
            return result;
        }
        return result;
    }

    /*
     **删除对象关键字
     */
    @PostMapping("delMktObjKeywordsRel")
    @CrossOrigin
    public Map<String, Object> delMktObjKeywordsRel(@RequestBody MktObjKeywordsRel mktObjKeywordsRel) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            MktObjKeywordsRel mktObjKeywords = mktObjKeywordsRelMapper.selectByPrimaryKey(mktObjKeywordsRel.getRelId());
            if(mktObjKeywords == null) {
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","事件源接口配置参数表不存在");
                return result;
            }
            mktObjKeywordsRelMapper.deleteByPrimaryKey(mktObjKeywordsRel.getRelId());
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","删除成功");
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to delMktObjKeywordsRel",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to delMktObjKeywordsRel");
            return result;
        }
        return result;
    }

    /*
     **获取对象关键字详情
     */
    @PostMapping("getMktObjKeywordsRelDetail")
    @CrossOrigin
    public Map<String, Object> getMktObjKeywordsRelDetail(@RequestBody HashMap<String,Long> param) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            Long relId = param.get("relId");
            MktObjKeywordsRel mktObjKeywordsRel = mktObjKeywordsRelMapper.selectByPrimaryKey(relId);
            if(mktObjKeywordsRel == null) {
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","事件源接口配置参数表不存在");
                return result;
            }
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg",mktObjKeywordsRel);
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to getMktObjKeywordsRelDetail",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getMktObjKeywordsRelDetail");
            return result;
        }
        return result;
    }
}
