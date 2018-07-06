package com.zjtelcom.cpct.controller.channel;

import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.channel.PpmProduct;
import com.zjtelcom.cpct.service.channel.ProductService;
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
@RequestMapping("${adminPath}/ppmProduct")
public class PpmProductController extends BaseController  {

    @Autowired
    private ProductService productService;

    /**
     * 获取销售品列表
     */
    @GetMapping("getProductList")
    @CrossOrigin
    public Map<String,Object> getProductList(Long userId, String productName){
        Map<String ,Object> result = new HashMap<>();
        List<PpmProduct> productList = new ArrayList<>();
        try {
            productList = productService.getProductList(userId,productName);
        }catch (Exception e){
            logger.error("[op:PpmProductController] fail to getProductList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",productList);
        return result;
    }


    /**
     * 添加规则下的销售品
     * @param productIdList
     * @return
     */
    @PostMapping("addProductRule")
    @CrossOrigin
    public Map<String, Object> addProductRule(@RequestBody List<Long> productIdList) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = productService.addProductRule(userId,productIdList);
        }catch (Exception e){
            logger.error("[op:PpmProductController] fail to addProductRule",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addProductRule");
            return result;
        }
        return result;
    }

    /**
     * 添加备注
     * @param ruleId
     * @param remark
     * @return
     */
    @PostMapping("editProductRule")
    @CrossOrigin
    public Map<String, Object> editProductRule(Long ruleId, String remark) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = productService.editProductRule(userId,ruleId,remark);
        }catch (Exception e){
            logger.error("[op:PpmProductController] fail to getProductList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to editProductRule");
            return result;
        }
        return result;
    }

    /**
     * 获取规则下的销售品列表
     * @param ruleIdList
     * @return
     */
    @PostMapping("getProductRuleList")
    @CrossOrigin
    public Map<String, Object> getProductRuleList(@RequestBody List<Long> ruleIdList) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = productService.getProductRuleList(userId,ruleIdList);
        }catch (Exception e){
            logger.error("[op:PpmProductController] fail to getProductRuleList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getProductRuleList");
            return result;
        }
        return result;
    }

    /**
     * 删除规则的销售品
     * @param ruleId
     * @return
     */
    @PostMapping("delProductRule")
    @CrossOrigin
    public Map<String, Object> delProductRule( Long ruleId) {
        Map<String, Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = productService.delProductRule(userId, ruleId);
        } catch (Exception e) {
            logger.error("[op:PpmProductController] fail to delProductRule", e);
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", " fail to delProductRule");
            return result;
        }
        return result;
    }

}
