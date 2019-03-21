package com.zjtelcom.cpct.controller.channel;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dao.channel.MktKeywordsMapper;
import com.zjtelcom.cpct.domain.channel.MktKeywords;
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
@RequestMapping("${adminPath}/mktKeywords")
public class MktKeywordsController extends BaseController {

    @Autowired
    private MktKeywordsMapper mktKeywordsMapper;

    /*
     **创建关键字
     */
    @PostMapping("createMktKeywords")
    @CrossOrigin
    public Map<String, Object> createMktKeywords(@RequestBody Map<String,Object> params) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            MktKeywords mktKeyword = JSON.parseObject(JSON.toJSONString(params), MktKeywords.class);
            mktKeyword.setCreateStaff(userId);
            mktKeyword.setCreateDate(new Date());
            mktKeyword.setUpdateStaff(userId);
            mktKeyword.setUpdateDate(new Date());
            mktKeyword.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
            mktKeyword.setStatusDate(new Date());
            mktKeywordsMapper.insert(mktKeyword);
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","添加成功");
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to createMktKeywords",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to createMktKeywords");
            return result;
        }
        return result;
    }

    /*
     **编辑关键字
     */
    @PostMapping("editMktKeywords")
    @CrossOrigin
    public Map<String, Object> editMktKeywords(@RequestBody MktKeywords mktKeywords) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            MktKeywords mktKeyword = mktKeywordsMapper.selectByPrimaryKey(mktKeywords.getKeywordId());
            if(mktKeyword == null) {
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","事件源接口配置参数表不存在");
                return result;
            }
            BeanUtil.copy(mktKeywords, mktKeyword);
            mktKeyword.setUpdateDate(new Date());
            mktKeyword.setUpdateStaff(userId);
            mktKeywordsMapper.updateByPrimaryKey(mktKeyword);
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","修改成功");
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to editMktKeywords",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to editMktKeywords");
            return result;
        }
        return result;
    }

    /*
     **删除关键字
     */
    @PostMapping("delMktKeywords")
    @CrossOrigin
    public Map<String, Object> delMktKeywords(@RequestBody MktKeywords mktKeywords) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            MktKeywords mktKeyword = mktKeywordsMapper.selectByPrimaryKey(mktKeywords.getKeywordId());
            if(mktKeyword == null) {
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","事件源接口配置参数表不存在");
                return result;
            }
            mktKeywordsMapper.deleteByPrimaryKey(mktKeywords.getKeywordId());
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","删除成功");
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to delMktKeywords",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to delMktKeywords");
            return result;
        }
        return result;
    }

    /*
     **获取关键字详情
     */
    @PostMapping("getMktKeywordsDetail")
    @CrossOrigin
    public Map<String, Object> getMktKeywordsDetail(@RequestBody HashMap<String,Long> param) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            Long keywordId = param.get("keywordId");
            MktKeywords mktKeywords = mktKeywordsMapper.selectByPrimaryKey(keywordId);
            if(mktKeywords == null) {
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","事件源接口配置参数表不存在");
                return result;
            }
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg",mktKeywords);
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to getMktKeywordsDetail",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getMktKeywordsDetail");
            return result;
        }
        return result;
    }

}
