package com.zjtelcom.cpct.controller.channel;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.channel.MktCamResource;
import com.zjtelcom.cpct.domain.channel.MktProductAttr;
import com.zjtelcom.cpct.dto.channel.ProductParam;
import com.zjtelcom.cpct.service.channel.CamElectronService;
import com.zjtelcom.cpct.service.channel.CatalogService;
import com.zjtelcom.cpct.service.channel.ProductService;
import com.zjtelcom.cpct.util.MapUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;

@RestController
@RequestMapping("${adminPath}/ppmProduct")
public class PpmProductController extends BaseController  {

    @Autowired
    private ProductService productService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private CamElectronService camElectronService;

    /**
     *活动发布调用营服接口
     * parram: list
     * @return
     */
    @PostMapping("publish4Mktcamresource")
    @CrossOrigin
    public Map<String, Object> publish4Mktcamresource(@RequestBody MktCamResource camResource) {
        Map<String ,Object> result = new HashMap<>();
        try {
            result = camElectronService.publish4Mktcamresource(camResource);
        }catch (Exception e){
            logger.error("[op:PpmProductController] fail to publish4Mktcamresource",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to publish4Mktcamresource");
            return result;
        }
        return result;
    }





    /**
     *批量新增依赖产品属性
     * parram: list
     * @return
     */
    @PostMapping("addMktProductAttr")
    @CrossOrigin
    public Map<String, Object> addMktProductAttr(@RequestBody Map<String,Object> param) {
        Map<String ,Object> result = new HashMap<>();
        try {
            result = productService.addMktProductAttr(param);
        }catch (Exception e){
            logger.error("[op:PpmProductController] fail to addMktProductAttr",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addMktProductAttr");
            return result;
        }
        return result;
    }

    /**
     *编辑产品属性值
     * @return
     */
    @PostMapping("editMktProductAttr")
    @CrossOrigin
    public Map<String, Object> editMktProductAttr(@RequestBody MktProductAttr mktProductAttr) {
        Map<String ,Object> result = new HashMap<>();
        try {
            result = productService.editMktProductAttr(mktProductAttr);
        }catch (Exception e){
            logger.error("[op:PpmProductController] fail to addMktProductAttr",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addMktProductAttr");
            return result;
        }
        return result;
    }
    /**
     *删除产品属性
     * @return
     */
    @PostMapping("deleteMktProductAttr")
    @CrossOrigin
    public Map<String, Object> deleteMktProductAttr(@RequestBody Map<String,Object> param) {
        Map<String ,Object> result = new HashMap<>();
        try {
            result = productService.deleteMktProductAttr(MapUtil.getLongNum(param.get("mktProductAttrId")));
        }catch (Exception e){
            logger.error("[op:PpmProductController] fail to deleteMktProductAttr",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to deleteMktProductAttr");
            return result;
        }
        return result;
    }
    /**
     *获取产品属性列表
     * @return
     */
    @PostMapping("listMktProductAttr")
    @CrossOrigin
    public Map<String, Object> listMktProductAttr(@RequestBody MktProductAttr mktProductAttr) {
        Map<String ,Object> result = new HashMap<>();
        try {
            result = productService.listMktProductAttr(mktProductAttr);
        }catch (Exception e){
            logger.error("[op:PpmProductController] fail to listMktProductAttr",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to listMktProductAttr");
            return result;
        }
        return result;
    }
    /**
     *删除整个产品
     * @return
     */
    @PostMapping("deleteMktProductItem")
    @CrossOrigin
    public Map<String, Object> deleteMktProductItem(@RequestBody MktProductAttr mktProductAttr) {
        Map<String ,Object> result = new HashMap<>();
        try {
            result = productService.deleteMktProductItem(mktProductAttr);
        }catch (Exception e){
            logger.error("[op:PpmProductController] fail to deleteMktProductItem",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to deleteMktProductItem");
            return result;
        }
        return result;
    }


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
//        Long userId = 1l;
        try {
            result = productService.getProductNameById(userId,param.getIdList(),param.getItemType());
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
     * 销售品模糊查询
     */
    @PostMapping("getProductListByName")
    @CrossOrigin
    public Map<String,Object> getProductListByName(@RequestBody HashMap<String,Object> params) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = productService.getProductListByName(params);
        }catch (Exception e){
            logger.error("[op:PpmProductController] fail to getProductListByName",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getProductListByName");
            return result;
        }
        return result;
    }

    /**
     * 销售品模糊查询
     */
    @PostMapping("getPackageOfferListByName")
    @CrossOrigin
    public Map<String,Object> getPackageOfferListByName(@RequestBody HashMap<String,Object> params) {
        Map<String ,Object> result = new HashMap<>();
        try {
            result = productService.getProductListByName(params);
        }catch (Exception e){
            logger.error("[op:PpmProductController] fail to getPackageOfferListByName",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getPackageOfferListByName");
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
            result = productService.addProductRule(param);
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
     * 关联活动的推荐条目列表
     * @return
     */
    @PostMapping("getProductRuleListByCampaign")
    @CrossOrigin
    public Map<String, Object> getProductRuleListByCampaign(@RequestBody ProductParam ruleParam) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = productService.getProductRuleListByCampaign(ruleParam);
        }catch (Exception e){
            logger.error("[op:PpmProductController] fail to getProductRuleListByCampaign",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getProductRuleListByCampaign");
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
            Long campaignId = null;
            Long ruleId = null;
            List<Long> itemRuleIdList = null;
            if (params.get("campaignId")!=null && !params.get("campaignId").equals("")){
                campaignId = Long.valueOf(params.get("campaignId").toString());
            }
            if (params.get("ruleId")!=null && !params.get("ruleId").equals("")){
                ruleId = Long.valueOf(params.get("ruleId").toString());
            }
            result = productService.delProductRule(campaignId, ruleId);
        } catch (Exception e) {
            logger.error("[op:PpmProductController] fail to delProductRule", e);
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", " fail to delProductRule");
            return result;
        }
        return result;
    }


    @RequestMapping("/getProjectListPage")
    @CrossOrigin
    public String getProjectListPage(@RequestBody Map<String, Object> params) {
        Map<String, Object> resultMap = productService.getProjectListPage(params);
        return JSON.toJSONString(resultMap);
    }

    @RequestMapping("/getAttrSpecListPage")
    @CrossOrigin
    public String getAttrSpecListPage(@RequestBody Map<String, Object> params) {
        Map<String, Object> resultMap = productService.getAttrSpecListPage(params);
        return JSON.toJSONString(resultMap);
    }


    @RequestMapping("/mktCamResourceService")
    @CrossOrigin
    public String mktCamResourceService(@RequestBody Map<String, Object> params) {
        Long mktCampaignId = (Long) params.get("mktCampaignId");
        Map<String, Object> resultMap = productService.mktCamResourceService(mktCampaignId);
        return JSON.toJSONString(resultMap);
    }

}
