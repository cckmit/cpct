package com.zjtelcom.cpct.controller.channel;

import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.channel.PpmProduct;
import com.zjtelcom.cpct.dto.channel.ProductParam;
import com.zjtelcom.cpct.service.channel.CatalogService;
import com.zjtelcom.cpct.service.channel.ProductService;
import com.zjtelcom.cpct.util.MapUtil;
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
    @Autowired
    private CatalogService catalogService;




    /**
     *通过目录节点获取销售品
     * @return
     */
    @PostMapping("listOfferByCatalogId")
    @CrossOrigin
    public Map<String, Object> listOfferByCatalogId(@RequestBody HashMap<String,Object> param) {
        Map<String ,Object> result = new HashMap<>();
        try {
            Integer page = Integer.valueOf(param.get("page").toString());
            Integer pageSize = Integer.valueOf(param.get("pageSize").toString());
            String productName = MapUtil.getString(param.get("productName"));
            result = catalogService.listOfferByCatalogId(Long.valueOf(param.get("catalogId").toString()),productName,page,pageSize);
        }catch (Exception e){
            logger.error("[op:PpmProductController] fail to listOfferByCatalogId",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to listOfferByCatalogId");
            return result;
        }
        return result;
    }

    /**
     *获取销售品目录树
     * @return
     */
    @PostMapping("listProductTree")
    @CrossOrigin
    public Map<String, Object> listProductTree() {
        Map<String ,Object> result = new HashMap<>();
        try {
            result = catalogService.listProductTree();
        }catch (Exception e){
            logger.error("[op:PpmProductController] fail to listProductTree",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to listProductTree");
            return result;
        }
        return result;
    }


    /**
     *获取产品名字
     * @return
     */
    @PostMapping("getProductNameById")
    @CrossOrigin
    public Map<String, Object> getProductNameById(@RequestBody ProductParam param) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = productService.getProductNameById(userId,param.getIdList());
        }catch (Exception e){
            logger.error("[op:PpmProductController] fail to getProductNameById",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getProductNameById");
            return result;
        }
        return result;
    }
    /**
     * 获取销售品列表
     */
    @PostMapping("getProductList")
    @CrossOrigin
    public Map<String,Object> getProductList(@RequestBody HashMap<String,Object> params){
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = productService.getProductList(userId,params);
        }catch (Exception e){
            logger.error("[op:PpmProductController] fail to getProductList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }


    /**
     * 添加规则下的销售品
     * @param
     * @return
     */
    @PostMapping("addProductRule")
    @CrossOrigin
    public Map<String, Object> addProductRule(@RequestBody ProductParam param) {
        Map<String ,Object> result = new HashMap<>();
        try {
            result = productService.addProductRule(param.getStrategyRuleId(),param.getIdList());
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
     * @param params
     * @return
     */
    @PostMapping("editProductRule")
    @CrossOrigin
    public Map<String, Object> editProductRule(@RequestBody HashMap<String,Object> params)  {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            String remark = null;
            Long priority = null;
            Long ruleId = Long.valueOf(params.get("ruleId").toString());
            if (params.get("remark")!=null){
                remark = params.get("remark").toString();
            }
            if (params.get("priority")!=null){
                priority = Long.valueOf(params.get("priority").toString());
            }
            result = productService.editProductRule(userId,ruleId,remark,priority);
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
     * @return
     */
    @PostMapping("getProductRuleList")
    @CrossOrigin
    public Map<String, Object> getProductRuleList(@RequestBody ProductParam ruleParam) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = productService.getProductRuleList(userId,ruleParam.getIdList());
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
     * @return
     */
    @PostMapping("delProductRule")
    @CrossOrigin
    public Map<String, Object> delProductRule(@RequestBody HashMap<String,Object> params) {
        Map<String, Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            Long strategyRuleId = null;
            Long ruleId = null;
            List<Long> itemRuleIdList = null;
            if (params.get("strategyRuleId")!=null && !params.get("strategyRuleId").equals("")){
                 strategyRuleId = Long.valueOf(params.get("strategyRuleId").toString());
            }
            if (params.get("ruleId")!=null && !params.get("ruleId").equals("")){
                ruleId = Long.valueOf(params.get("ruleId").toString());
            }
            if (params.get("itemRuleIdList")!=null){
                itemRuleIdList = (List<java.lang.Long>) params.get("itemRuleIdList");
            }
            result = productService.delProductRule(strategyRuleId, ruleId,itemRuleIdList);
        } catch (Exception e) {
            logger.error("[op:PpmProductController] fail to delProductRule", e);
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", " fail to delProductRule");
            return result;
        }
        return result;
    }

}
