package com.zjtelcom.cpct.service.impl.channel;

import com.alibaba.fastjson.JSON;
import com.ctzj.smt.bss.cpc.model.common.CpcResultObject;
import com.ctzj.smt.bss.cpc.model.dto.CouponApplyObjectDto;
import com.ctzj.smt.bss.cpc.model.dto.CouponEffExpDto;
import com.ctzj.smt.bss.cpc.model.dto.MktResCouponDto;
import com.ctzj.smt.bss.cpc.query.service.api.ICouponApplyObjectService;
import com.ctzj.smt.bss.cpc.query.service.api.ICouponEffExpRuleService;
import com.ctzj.smt.bss.cpc.query.service.api.ICpcMktResCouponDubboService;
import com.ctzj.smt.bss.cpc.write.service.api.ICouponApplyObjectWriteService;
import com.ctzj.smt.bss.cpc.write.service.api.ICouponEffExpRuleWriteService;
import com.ctzj.smt.bss.cpc.write.service.api.IMktResCouponWriteService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCamItemMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.channel.MktCamResourceMapper;
import com.zjtelcom.cpct.dao.channel.MktProductAttrMapper;
import com.zjtelcom.cpct.dao.channel.ServiceMapper;
import com.zjtelcom.cpct.dao.product.ProductNewMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamItem;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.dto.channel.OfferDetail;
import com.zjtelcom.cpct.dto.channel.ProductParam;
import com.zjtelcom.cpct.enums.*;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.ProductService;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfRuleService;
import com.zjtelcom.cpct.util.*;
import com.zjtelcom.cpct.dao.offer.MktResourceProdMapper;
import com.zjtelcom.cpct.dao.offer.OfferProdMapper;
import com.zjtelcom.cpct.dao.offer.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.*;

@Service
public class ProductCpcServiceImpl extends BaseService implements ProductService {

    @Autowired
    private OfferProdMapper offerProdMapper;
    @Autowired
    private MktCamItemMapper camItemMapper;
    @Autowired
    private MktStrategyConfRuleService strategyConfRuleService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private MktResourceProdMapper resourceMapper;
    @Autowired
    private ServiceMapper serviceMapper;
    @Autowired
    private MktCampaignMapper campaignMapper;
    @Autowired
    private MktStrategyConfRuleMapper ruleMapper;
    @Autowired
    private ProductMapper productProdMapper;
    @Autowired
    private MktProductAttrMapper mktProductAttrMapper;
    @Autowired
    private ProductNewMapper productNewMapper;

    @Autowired(required = false)
    private ICpcMktResCouponDubboService iCpcMktResCouponDubboService;

    @Autowired(required = false)
    private IMktResCouponWriteService iMktResCouponWriteService;

    @Autowired(required = false)
    private ICouponEffExpRuleWriteService iCouponEffExpRuleWriteService;

    @Autowired(required = false)
    private ICouponEffExpRuleService iCouponEffExpRuleService;

    @Autowired(required = false)
    private ICouponApplyObjectWriteService iCouponApplyObjectWriteService;

    @Autowired(required = false)
    private ICouponApplyObjectService iCouponApplyObjectService;

    @Autowired
    private MktCamResourceMapper mktCamResourceMapper;



    @Override
    public Map<String, Object> copyMktCamResource(Long oldResourceId, Long newResourceId, Long ruleId) {


        return null;
    }

    @Override
    public Map<String, Object> copyMktProductAttr(Long oldProductId, Long newProductId, Long ruleId) {
        Map<String,Object> result = new HashMap<>();
        MktProductAttr  mktProductAttr = new MktProductAttr();
        mktProductAttr.setProductId(oldProductId);
        List<MktProductAttr> productAttrs = mktProductAttrMapper.selectByProduct(mktProductAttr);
        if (!productAttrs.isEmpty()){
            for (MktProductAttr oldAttr : productAttrs) {
                MktProductAttr newAttr = BeanUtil.create(oldAttr, new MktProductAttr());
                newAttr.setMktProductAttrId(null);
                newAttr.setProductId(newProductId);
                newAttr.setRuleId(ruleId);
                mktProductAttrMapper.insert(newAttr);
            }
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",productAttrs);
        return result;
    }

    @Override
    public Map<String, Object> listMktProductAttr(MktProductAttr mktProductAttr) {
        Map<String,Object> result = new HashMap<>();
        List<MktProductAttr> productAttrs = mktProductAttrMapper.selectByProduct(mktProductAttr);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",productAttrs);
        return result;
    }

    @Override
    public Map<String, Object> editMktProductAttr(MktProductAttr mktProductAttr) {
        Map<String,Object> result = new HashMap<>();
        mktProductAttrMapper.updateByPrimaryKey(mktProductAttr);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","更新成功");
        return result;
    }

    @Override
    public Map<String, Object> addMktProductAttr(Map<String, Object> param) {
        Map<String,Object> result = new HashMap<>();
        List<MktProductAttr> productAttrs = (List<MktProductAttr>) param.get("list");
        for (MktProductAttr productAttr : productAttrs) {
            mktProductAttrMapper.insert(productAttr);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",productAttrs);
        return result;
    }

    @Override
    public Map<String, Object> getProductNameById(Long userId, List<Long> productIdList, String itemType) {

        Map<String,Object> result = new HashMap<>();
        List<OfferDetail> nameList = new ArrayList<>();
        //销售品	1000
        //主产品	2000
        //子产品	3000
        if (itemType==null || itemType.equals("1000") || "".equals(itemType)){
            for (Long productId : productIdList){
                Offer product = offerProdMapper.selectByPrimaryKey(Integer.valueOf(productId.toString()));
                if (product==null){
                    continue;
                }
                OfferDetail offerDetail = BeanUtil.create(product,new OfferDetail());
                nameList.add(offerDetail);
            }
        }
        if (itemType==null || itemType.equals("2000") || itemType.equals("3000")){
            for (Long productId : productIdList){
                Product product = productProdMapper.selectByPrimaryKey(Long.valueOf(productId.toString()));
                if (product==null){
                    continue;
                }
                OfferDetail offerDetail = new OfferDetail(Integer.parseInt(product.getProdId().toString()),product.getProdName());
//                OfferDetail offerDetail = BeanUtil.create(product,new OfferDetail());
                nameList.add(offerDetail);
            }
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",nameList);
        return result;
    }

    @Override
    public Map<String,Object> getProductList(Long userId,Map<String,Object> params){
        Map<String,Object> result = new HashMap<>();
        List<Offer> productList = new ArrayList<>();
        Integer page = MapUtil.getIntNum(params.get("page"));
        Integer pageSize = MapUtil.getIntNum(params.get("pageSize"));
        String productName = null;
        if (params.get("productName")!=null){
            productName = params.get("productName").toString();
        }
        PageHelper.startPage(page,pageSize);
        productList = offerProdMapper.findByName(productName);
        Page pageInfo = new Page(new PageInfo(productList));
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",productList);
        result.put("page",pageInfo);
        return result;
    }

    // todo 修改 销售品原流程，主产品 4个类型， 子产品类型3000
    @Override
    public Map<String,Object> getProductListByName( Map<String,Object> params) {
        Map<String,Object> result = new HashMap<>();
        List<Offer> productList = new ArrayList<>();
        Integer page = MapUtil.getIntNum(params.get("page"));
        Integer pageSize = MapUtil.getIntNum(params.get("pageSize"));
        String produtType = MapUtil.getString(params.get("type"));
        String productName = MapUtil.getString(params.get("productName"));
        // 1000查所有；空查有效
        String statusCd = MapUtil.getString(params.get("statusCd") == null? "":params.get("statusCd"));
        List<Long> producetIdList = (List<Long>)params.get("productIdList");
        if ("1000".equals(produtType)){
            //四个编码
            PageHelper.startPage(page,pageSize);
            productList =  offerProdMapper.selectByFourNum();
        }else if ("2000".equals(produtType)){
            PageHelper.startPage(page,pageSize);
            productList = offerProdMapper.findProductByType(productName,statusCd, producetIdList);
        }else {
            PageHelper.startPage(page,pageSize);
            productList = offerProdMapper.findByType(productName, produtType, statusCd, producetIdList,"offer");
        }
        Page pageInfo = new Page(new PageInfo(productList));
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",productList);
        result.put("page",pageInfo);
        return result;
    }

    @Override
    public Map<String, Object> getPackageOfferListByName(Map<String, Object> params) {
        Map<String,Object> result = new HashMap<>();
        List<Offer> productList = new ArrayList<>();
        Integer page = MapUtil.getIntNum(params.get("page"));
        Integer pageSize = MapUtil.getIntNum(params.get("pageSize"));
        String produtType = MapUtil.getString(params.get("type"));
        String productName = MapUtil.getString(params.get("productName"));
        // 1000查所有；空查有效
        String statusCd = MapUtil.getString(params.get("statusCd") == null? "":params.get("statusCd"));
        List<Long> producetIdList = (List<Long>)params.get("productIdList");
        PageHelper.startPage(page,pageSize);
        productList = offerProdMapper.findByType(productName, produtType, statusCd, producetIdList,"package");
        Page pageInfo = new Page(new PageInfo(productList));
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",productList);
        result.put("page",pageInfo);
        return result;
    }

    /**
     * 复制营销活动推荐条目
     * @param userId
     * @param ItemIdList
     * @return
     */
    @Override
    public Map<String, Object> copyProductRule(Long userId, List<Long> ItemIdList) {
        Map<String,Object> result = new HashMap<>();
        List<Long> ruleIdList = new ArrayList<>();
        if(ItemIdList!=null && ItemIdList.size()>0){
            List<MktCamItem> mktCamItems = new ArrayList<>();
            for (Long itemId : ItemIdList) {
                MktCamItem item = (MktCamItem) redisUtils.get("MKT_CAM_ITEM_"+itemId);
                if (item == null) {
                    item = camItemMapper.selectByPrimaryKey(itemId);
                    redisUtils.set("MKT_CAM_ITEM_"+itemId, item);
                }
                MktCamItem newItem = BeanUtil.create(item, new MktCamItem());
                newItem.setMktCamItemId(null);
                newItem.setMktCampaignId(-1L);
                mktCamItems.add(newItem);
            }
            camItemMapper.insertByBatch(mktCamItems);
            for(MktCamItem item : mktCamItems){
                redisUtils.set("MKT_CAM_ITEM_" + item.getMktCamItemId(), item);
                ruleIdList.add(item.getMktCamItemId());
            }
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("ruleIdList",ruleIdList);
        return result;
    }

    /**
     * 活动发布复制推荐条目
     * @param oldCampaignId
     * @param newCampaignId
     * @return
     */
    @Override
    public Map<String, Object> copyItemByCampaignPublish(Long oldCampaignId, Long newCampaignId, String mktCampaignCategory) {
        Map<String,Object> result = new HashMap<>();
        List<Long> idList = new ArrayList<>();
        List<MktCamItem> oldItemList = camItemMapper.selectByCampaignId(oldCampaignId);
        for (MktCamItem item : oldItemList){
            MktCamItem newItem = BeanUtil.create(item,new MktCamItem());
            if(StatusCode.ENFORCEMENT_CAMPAIGN.getStatusCode().equals(mktCampaignCategory) || StatusCode.FRAMEWORK_CAMPAIGN.getStatusCode().equals(mktCampaignCategory)){
                newItem.setStatusCd(StatusCode.STATUS_CODE_FAILURE.getStatusCode());
            } else {
                newItem.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
            }

            newItem.setMktCamItemId(null);
            newItem.setMktCampaignId(newCampaignId);
            camItemMapper.insert(newItem);
            idList.add(newItem.getMktCamItemId());
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",idList);
        return result;
    }

    /**
     * 活动复制推荐条目
     * @param oldCampaignId
     * @param newCampaignId
     * @return
     */
    @Override
    public Map<String, Object> copyItemByCampaign(Long oldCampaignId, Long newCampaignId) {
        Map<String,Object> result = new HashMap<>();
        Map<Long, Long> itemMap = new HashMap<>();
        List<Long> idList = new ArrayList<>();
        List<MktCamItem> oldItemList = camItemMapper.selectByCampaignId(oldCampaignId);
        for (MktCamItem item : oldItemList){
            MktCamItem newItem = BeanUtil.create(item,new MktCamItem());
            newItem.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
            newItem.setMktCamItemId(null);
            newItem.setMktCampaignId(newCampaignId);
            camItemMapper.insert(newItem);
            itemMap.put(item.getMktCamItemId(), newItem.getMktCamItemId());
            idList.add(newItem.getMktCamItemId());
        }
        result.put("itemMap", itemMap);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",idList);
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> addProductRule(ProductParam param) {
        Map<String,Object> result = new HashMap<>();
        List<Long> ruleIdList = new ArrayList<>();
        List<MktCamItem> mktCamItems = new ArrayList<>();

        //销售品
        if (CamItemType.OFFER.getValue().equals(param.getItemType())|| CamItemType.PACKAGE.getValue().equals(param.getItemType())
                || param.getItemType().equals(CamItemType.DEPEND_OFFER.getValue())){
            for (Long productId : param.getIdList()){
                Offer product = offerProdMapper.selectByPrimaryKey(Integer.valueOf(productId.toString()));
                if (product==null){
                    result.put("resultCode",CODE_FAIL);
                    result.put("resultMsg","产品不存在");
                    return result;
                }
                MktCamItem item = new MktCamItem();
                item.setMktCampaignId(param.getCampaignId()==null ? -1L : param.getCampaignId());
                item.setOfferCode(product.getOfferNbr());
                item.setItemType(param.getItemType()==null ? "1000" : param.getItemType());
                item.setCreateDate(new Date());
                item.setCreateDate(DateUtil.getCurrentTime());
                item.setOfferName(product.getOfferName());
                item.setItemId(productId);
                item.setUpdateDate(DateUtil.getCurrentTime());
                item.setStatusDate(DateUtil.getCurrentTime());
                item.setUpdateStaff(UserUtil.loginId());
                item.setCreateStaff(UserUtil.loginId());
                item.setStatusCd(param.getStatusCd()==null? STATUSCD_EFFECTIVE : param.getStatusCd());
                mktCamItems.add(item);
                //redis添加推荐条目数据
                redisUtils.set("MKT_CAM_ITEM_"+item.getMktCamItemId(),item);
            }
            //促销券
        }else if (CamItemType.RESOURCE.getValue().equals(param.getItemType())){
            for (Long resourceId : param.getIdList()){
                MktResource resource = resourceMapper.selectByPrimaryKey(resourceId);
                if (resource==null){
                    result.put("resultCode",CODE_FAIL);
                    result.put("resultMsg","促销券不存在");
                    return result;
                }
                MktCamItem item = new MktCamItem();
                item.setMktCampaignId(param.getCampaignId()==null ? -1L : param.getCampaignId());
                item.setOfferCode(resource.getMktResNbr());
                item.setOfferName(resource.getMktResName());
                itemBuild(param, mktCamItems, resourceId, item);
            }
        }else if (CamItemType.SERVICE.getValue().equals(param.getItemType())){
            for (Long serviceId : param.getIdList()){
                ServiceEntity serviceEntity = serviceMapper.selectByPrimaryKey(serviceId);
                if (serviceEntity==null){
                    result.put("resultCode",CODE_FAIL);
                    result.put("resultMsg","服务不存在");
                    return result;
                }
                MktCamItem item = new MktCamItem();
                item.setMktCampaignId(param.getCampaignId()==null ? -1L : param.getCampaignId());
                item.setOfferCode(serviceEntity.getServiceNbr());
                item.setOfferName(serviceEntity.getServiceName());
                itemBuild(param, mktCamItems, serviceId, item);
            }
        }else if (CamItemType.DEPEND_PRODUCT.getValue().equals(param.getItemType())){
            for (Long productId : param.getIdList()){
                Product product = productProdMapper.selectByPrimaryKey(productId);
                if (product==null){
                    result.put("resultCode",CODE_FAIL);
                    result.put("resultMsg","依赖产品不存在");
                    return result;
                }
                MktCamItem item = new MktCamItem();
                item.setMktCampaignId(param.getCampaignId()==null ? -1L : param.getCampaignId());
                item.setOfferCode(product.getProdNbr());
                item.setOfferName(product.getProdName());
                itemBuild(param, mktCamItems, productId, item);
            }
        }
        if (!mktCamItems.isEmpty()){
            camItemMapper.insertByBatch(mktCamItems);
        }
        for(MktCamItem item : mktCamItems){
            ruleIdList.add(item.getMktCamItemId());
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",ruleIdList);
        return result;
    }

    private void itemBuild(ProductParam param, List<MktCamItem> mktCamItems, Long productId, MktCamItem item) {
        item.setItemId(productId);
        item.setItemType(param.getItemType());
        item.setCreateDate(new Date());
        item.setCreateDate(DateUtil.getCurrentTime());
        item.setUpdateDate(DateUtil.getCurrentTime());
        item.setStatusDate(DateUtil.getCurrentTime());
        item.setUpdateStaff(UserUtil.loginId());
        item.setCreateStaff(UserUtil.loginId());
        item.setStatusCd(param.getStatusCd()==null? STATUSCD_EFFECTIVE : param.getStatusCd());
        mktCamItems.add(item);
        //redis添加推荐条目数据
        redisUtils.set("MKT_CAM_ITEM_"+item.getMktCamItemId(),item);
    }

    @Override
    public Map<String, Object> editProductRule(Long userId, Long ruleId, String remark,Long priority) {
        Map<String,Object> result = new HashMap<>();
        MktCamItem rule = camItemMapper.selectByPrimaryKey(ruleId);
        if (rule==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","推荐条目不存在");
            return result;
        }
        rule.setRemark(remark);
        rule.setPriority(priority);
        camItemMapper.updateByPrimaryKey(rule);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","编辑成功");
        return result;
    }

    @Override
    public Map<String, Object> getProductRuleListByCampaign(ProductParam param) {
        Map<String,Object> result = new HashMap<>();
        List<MktProductRule> ruleList = new ArrayList<>();
        List<MktCamItem> itemList = camItemMapper.selectByCampaignAndType(param.getCampaignId(),param.getItemType(),param.getName());
        for (MktCamItem item : itemList) {
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",itemList);
        return result;
    }

    @Override
    public Map<String, Object> getProductRuleList(Long userId, List<Long> ruleIdList) {
        Map<String,Object> result = new HashMap<>();
        List<MktProductRule> ruleList = new ArrayList<>();
        for (Long ruleId : ruleIdList){
            MktCamItem item = camItemMapper.selectByPrimaryKey(ruleId);
            if (item==null){
                continue;
            }
            //销售品
            if (CamItemType.OFFER.getValue().equals(item.getItemType())|| CamItemType.PACKAGE.getValue().equals(item.getItemType())
                    || item.getItemType().equals(CamItemType.DEPEND_OFFER.getValue())){
                Offer product = offerProdMapper.selectByPrimaryKey(Integer.valueOf(item.getItemId().toString()));
                if (product==null){
                    continue;
                }
                MktProductRule rule = new MktProductRule();
                rule.setId(item.getMktCamItemId());
                rule.setProductId(item.getItemId());
                rule.setProductName(product.getOfferName());
                rule.setProductCode(product.getOfferNbr());
                rule.setProductType(product.getOfferType()==null ? "" : product.getOfferType());
                rule.setRemark(item.getRemark());
                rule.setItemType(item.getItemType()==null ? "" : item.getItemType());
                rule.setPriority(item.getPriority()==null ? 0 : item.getPriority());
                rule.setStatusCd(item.getStatusCd());
                if (item.getPriority()!=null){
                    rule.setPriority(item.getPriority());
                }
                ruleList.add(rule);
            }else if (item.getItemType().equals("3000")){
                //促销券
                MktResource resource = resourceMapper.selectByPrimaryKey(item.getItemId());
                if (resource==null){
                    continue;
                }
                MktProductRule rule = new MktProductRule();
                itemListBuild(ruleList, item, rule, resource.getMktResName(), resource.getMktResNbr());
            } else if (item.getItemType().equals("4000")){
                //促销券
                ServiceEntity serviceEntity = serviceMapper.selectByPrimaryKey(item.getItemId());
                if (serviceEntity==null){
                    continue;
                }
                MktProductRule rule = new MktProductRule();
                itemListBuild(ruleList, item, rule, serviceEntity.getServiceName(), serviceEntity.getServiceNbr());
            }else if (CamItemType.DEPEND_PRODUCT.getValue().equals(item.getItemType())){
                Product product = productProdMapper.selectByPrimaryKey(item.getItemId());
                if (product==null){
                    continue;
                }
                MktProductRule rule = new MktProductRule();
                itemListBuild(ruleList, item, rule, product.getProdName(), product.getProdNbr());
            }
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",ruleList);
        return result;
    }

    private void itemListBuild(List<MktProductRule> ruleList, MktCamItem item, MktProductRule rule, String serviceName, String serviceNbr) {
        rule.setId(item.getMktCamItemId());
        rule.setProductId(item.getItemId());
        rule.setProductName(serviceName);
        rule.setProductCode(serviceNbr);
        rule.setProductType(item.getItemType() == null ? "" : item.getItemType());
        rule.setRemark(item.getRemark());
        rule.setItemType(item.getItemType() == null ? "" : item.getItemType());
        rule.setPriority(item.getPriority() == null ? 0 : item.getPriority());
        rule.setStatusCd(item.getStatusCd());
        ruleList.add(rule);
    }

    @Override
    public Map<String, Object> delProductRule(Long campaignId, Long ruleId) {
        Map<String,Object> result = new HashMap<>();
        MktCamItem item = camItemMapper.selectByPrimaryKey(ruleId);
        if (item==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","推荐条目不存在");
            return result;
        }
        if (campaignId!=null){
            List<MktStrategyConfRuleDO> ruleList = ruleMapper.selectByCampaignId(campaignId);
            for (MktStrategyConfRuleDO rule : ruleList){
                if (rule.getProductId()!=null && !rule.getProductId().equals("")){
                    String[] idList = rule.getProductId().split("/");
                    List<String> ids = Arrays.asList(idList);
                    if (ids.contains(ruleId.toString())){
                        result.put("resultCode",CODE_FAIL);
                        result.put("resultMsg","推荐条目已关联规则："+rule.getMktStrategyConfRuleName()+"  无法删除");
                        return result;
                    }
                }
            }
        }
        camItemMapper.deleteByPrimaryKey(ruleId);
        //更新redis数据
        redisUtils.remove("MKT_CAM_ITEM_"+ruleId);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","删除成功");
        return result;
    }

    private void deleteRedisMktCamItem(Long strategyRuleId, MktCamItem item){
        List<OfferDetail> nameList =  (List<OfferDetail>)redisUtils.get("MKT_CAM_ITEM_"+strategyRuleId);
        if (nameList!=null){
            for (OfferDetail offer : nameList){
                if (Long.valueOf(offer.getOfferId()).equals(item.getItemId())){
                    nameList.remove(offer);
                }
            }
            redisUtils.set("MKT_CAM_ITEM_"+strategyRuleId,nameList);
        }
    }


    @Override
    public Map<String, Object> getProjectListPage(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            //获取分页参数
            Integer page = Integer.parseInt(params.get("page").toString());
            Integer pageSize = Integer.parseInt(params.get("pageSize").toString());
            String prodName = (String) params.get("prodName");
            //分页
            PageHelper.startPage(page, pageSize);
            List<Product> productList = productNewMapper.selectByProdName(prodName);
            for (Product product : productList) {
                product.setManageGradeValue(ManageGradeEnum.getValuedById(product.getManageGrade()));
                product.setProdCompTypeValue(ProdCompTypeEnum.getValuedById(product.getProdCompType()));
            }
            Page pageInfo = new Page(new PageInfo(productList));
            resultMap.put("data", productList);
            resultMap.put("page", pageInfo);
            resultMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            resultMap.put("resultMsg", "查询成功！");
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getProjectList失败= {}", e);
            resultMap.put("resultCode", CommonConstant.CODE_FAIL);
            resultMap.put("resultMsg", "查询失败！");
            return resultMap;
        }
    }

    @Override
    public Map<String, Object> getAttrSpecListPage(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            //获取分页参数
            Long prodId = Long.valueOf(params.get("prodId").toString());
            Integer page = Integer.parseInt(params.get("page").toString());
            Integer pageSize = Integer.parseInt(params.get("pageSize").toString());
            String attrName = (String) params.get("attrName");
            //分页
            PageHelper.startPage(page, pageSize);
            List<Map<String, Object>> mapList = productNewMapper.selectAttrSpec(prodId, attrName);
            for (Map<String, Object> map : mapList) {
                Long prodAttrId = (Long) map.get("prodAttrId");
                List<Map<String, Object>> attrValueList = productNewMapper.selectProdAttrValue(prodAttrId);
                if (attrValueList != null && attrValueList.size() > 0 && attrValueList.get(0) != null) {
                    map.put("prodAttrValue", attrValueList);
                } else {
                    map.put("prodAttrValue", new ArrayList<Map<String, Object>>());
                }
                map.put("checkAttrValue", new ArrayList<Long>());
            }
            Page pageInfo = new Page(new PageInfo(mapList));
            resultMap.put("data", mapList);
            resultMap.put("page", pageInfo);
            resultMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            resultMap.put("resultMsg", "查询成功！");
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getProjectList失败= {}", e);
            resultMap.put("resultCode", CommonConstant.CODE_FAIL);
            resultMap.put("resultMsg", "查询失败！");
            return resultMap;
        }
    }

/*    @Override
    public Map<String, Object> getProjectDetail(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        try {

            Long prodId = (Long) params.get("prodId");

            List<Product> productList = productNewMapper.selectByProdName(prodName);
            for (Product product : productList) {
                product.setManageGradeValue(ManageGradeEnum.getValuedById(product.getManageGrade()));
                product.setProdCompTypeValue(ProdCompTypeEnum.getValuedById(product.getProdCompType()));
            }
            resultMap.put("data", productList);
            resultMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            resultMap.put("resultMsg", "查询成功！");
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getProjectList失败= {}", e);
            resultMap.put("resultCode", CommonConstant.CODE_FAIL);
            resultMap.put("resultMsg", "查询失败！");
            return resultMap;
        }
    }*/




    @Override
    public Map<String, Object> mktCamResourceService(Long mktCampaignId) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            //查询
            List<MktCamResource> mktCamResourceList = mktCamResourceMapper.selectByCampaignId(mktCampaignId, FrameFlgEnum.NO.getValue());
            for (MktCamResource mktCamResource : mktCamResourceList) {
                Long mktResId = mktCamResource.getResourceId();
                MktResCouponDto mktResCouponDto = new MktResCouponDto();
                mktResCouponDto.setShowAmount(Double.valueOf(mktCamResource.getFaceAmount()) + Double.valueOf(mktCamResource.getDifferentPrice()));
                mktResCouponDto.setSalePrice(Double.valueOf(mktCamResource.getFaceAmount()));
                mktResCouponDto.setPriceDifference(Double.valueOf(mktCamResource.getDifferentPrice()));
                mktResCouponDto.setRemark(mktCamResource.getRemark());
                mktResCouponDto.setMktResName(mktCamResource.getCamResourceName());
                //mktResCouponDto.setMktResDesc(mktCamResource.get);
                mktResCouponDto.setMktResTypeId(Long.valueOf(mktCamResource.getResourceType()));
                mktResCouponDto.setMktResExtTypeId(Long.valueOf(mktCamResource.getResourceSubtype()));
                mktResCouponDto.setManageRegionId(mktCamResource.getLanId());
                mktResCouponDto.setEffDate(mktCamResource.getStartTime());
                mktResCouponDto.setExpDate(mktCamResource.getEndTime());
                mktResCouponDto.setQuantity(mktCamResource.getResourceApplyNum().toString());
                mktResCouponDto.setReleaseType(mktCamResource.getReleaseType());
                //mktResCouponDto.setAlgorithm();
                mktResCouponDto.setIssueLan(mktCamResource.getPublishArea());
                mktResCouponDto.setCreateStaff(UserUtil.loginId());
                mktResCouponDto.setUpdateStaff(UserUtil.loginId());
                mktResCouponDto.setSupplierId(mktCamResource.getMktCampaignId());
                if (mktResId != null) {
                    // 有电子券 -> 修改
                    mktResCouponDto.setActType("MOD");
                    logger.info("1--->>> 入参（MOD）：" + JSON.toJSONString(mktResCouponDto));
                    CpcResultObject<String> stringCpcResultObject = iMktResCouponWriteService.modifyMktresCoupon(mktResCouponDto);
                    logger.info("1--->>> 出参（MOD）：" + JSON.toJSONString(mktResCouponDto));

                    CouponEffExpDto couponEffExpDto = new CouponEffExpDto();
                    couponEffExpDto.setEffExpRuleId(mktCamResource.getRuleId());
                    couponEffExpDto.setMktResId(mktCamResource.getResourceId());
                    couponEffExpDto.setRemark(mktCamResource.getRemark());
                    couponEffExpDto.setEffDate(mktCamResource.getStartTime());
                    couponEffExpDto.setExpDate(mktCamResource.getEndTime());
                    couponEffExpDto.setStaffId(mktCamResource.getCreateStaff());
                    couponEffExpDto.setActType("MOD");
                    logger.info("2--->>> 入参（MOD）：" + JSON.toJSONString(couponEffExpDto));
                    CpcResultObject cpcResultObject = iCouponEffExpRuleWriteService.modifyCouponEffExpRlue(couponEffExpDto);
                    logger.info("2--->>> 出参（MOD）：" + JSON.toJSONString(cpcResultObject));

                } else {
                    // 没有电子券 -> 新增
                    mktResCouponDto.setActType("ADD");
                    logger.info("3--->>> 入参（ADD）：" + JSON.toJSONString(mktResCouponDto));
                    CpcResultObject<String> stringCpcResultObject = iMktResCouponWriteService.modifyMktresCoupon(mktResCouponDto);
                    logger.info("3--->>> 出参（ADD）：" + JSON.toJSONString(mktResCouponDto));
                    if ("0".equals(stringCpcResultObject.getResultCode())) {
                        String idStr = stringCpcResultObject.getResultObject();
                        if (idStr != null && !"".equals(idStr)) {
                            mktResId = Long.valueOf(idStr);
                            mktCamResourceMapper.updateResourceId(mktCamResource.getMktCamResourceId(), mktResId);
                            logger.info("回填电子券标识到数据库中...");
                        }
                    }

                    CouponEffExpDto couponEffExpDto = new CouponEffExpDto();
                    couponEffExpDto.setEffExpRuleId(mktCamResource.getRuleId());
                    couponEffExpDto.setMktResId(mktResId);
                    couponEffExpDto.setRemark(mktCamResource.getRemark());
                    couponEffExpDto.setEffDate(mktCamResource.getStartTime());
                    couponEffExpDto.setExpDate(mktCamResource.getEndTime());
                    couponEffExpDto.setStaffId(mktCamResource.getCreateStaff());
                    couponEffExpDto.setActType("ADD");
                    logger.info("4--->>>入参（ADD）：" + JSON.toJSONString(couponEffExpDto));
                    CpcResultObject cpcResultObject = iCouponEffExpRuleWriteService.modifyCouponEffExpRlue(couponEffExpDto);
                    logger.info("4--->>>出参（ADD）：" + JSON.toJSONString(cpcResultObject));
                }

                // 销售品
                if (mktCamResource.getOfferId() != null) {
                    //MktCamItem mktCamItem = camItemMapper.selectByPrimaryKey(Long.valueOf(mktCamResource.getOfferId()));
                    CouponApplyObjectDto couponApplyObjectDto = new CouponApplyObjectDto();
                    couponApplyObjectDto.setMktResId(mktResId);
                    couponApplyObjectDto.setRemark(ProductTypeEnum.OFFER.getName());
                    // 查询销售品
                    logger.info("5--->>> 入参：" + JSON.toJSONString(couponApplyObjectDto));
                    CpcResultObject<List<CouponApplyObjectDto>> listCpcResultObject = iCouponApplyObjectService.qryCouponApplyObjectList(couponApplyObjectDto);
                    logger.info("5--->>> 出参：" + JSON.toJSONString(listCpcResultObject));
                    // 删除销售品
                    if("0".equals(listCpcResultObject.getResultCode())){
                        List<CouponApplyObjectDto> couponApplyObjectDtos = listCpcResultObject.getResultObject();
                        for (CouponApplyObjectDto applyObjectDto : couponApplyObjectDtos) {
                            applyObjectDto.setActType("DEL");
                        }
                        logger.info("6--->>> 入参（DEL）：" + JSON.toJSONString(couponApplyObjectDtos));
                        CpcResultObject<Boolean> booleanCpcResultObject = iCouponApplyObjectWriteService.modifyCouponApplyObject(couponApplyObjectDtos);
                        logger.info("6--->>> 出参（DEL）：" + JSON.toJSONString(booleanCpcResultObject));
                    }
                    // 新增销售品
                    MktCamItem mktCamItem = camItemMapper.selectByPrimaryKey(Long.valueOf(mktCamResource.getOfferId()));
                    List<CouponApplyObjectDto> couponApplyObjectDtos = new ArrayList<>();
                    CouponApplyObjectDto couponApplyObjectDtoNew = new CouponApplyObjectDto();
                    couponApplyObjectDtoNew.setMktResId(mktResId);
                    couponApplyObjectDtoNew.setObjId(mktCamItem.getItemId());
                    couponApplyObjectDtoNew.setObjType(Long.valueOf(mktCamItem.getItemType()));
                    couponApplyObjectDtoNew.setStaffId(UserUtil.loginId());
                    couponApplyObjectDtoNew.setActType("ADD");
                    couponApplyObjectDtoNew.setRemark(ProductTypeEnum.OFFER.getName());
                    couponApplyObjectDtos.add(couponApplyObjectDtoNew);
                    logger.info("7--->>> 入参（ADD）" + JSON.toJSONString(couponApplyObjectDtos));
                    CpcResultObject<Boolean> booleanCpcResultObject = iCouponApplyObjectWriteService.modifyCouponApplyObject(couponApplyObjectDtos);
                    logger.info("7--->>> 出参（ADD）" + JSON.toJSONString(booleanCpcResultObject));
                }

                //依赖销售品条目id(多个以“/”分开)
                if (mktCamResource.getDependOfferId() != null && !"".equals(mktCamResource.getDependOfferId())) {
                    CouponApplyObjectDto couponApplyObjectDto = new CouponApplyObjectDto();
                    couponApplyObjectDto.setMktResId(mktResId);
                    couponApplyObjectDto.setRemark(ProductTypeEnum.DEPEND_OFFER.getName());
                    // 查询依赖销售品
                    logger.info("8--->>> 入参：" + JSON.toJSONString(couponApplyObjectDto));
                    CpcResultObject<List<CouponApplyObjectDto>> listCpcResultObject = iCouponApplyObjectService.qryCouponApplyObjectList(couponApplyObjectDto);
                    logger.info("8--->>> 出参：" + JSON.toJSONString(listCpcResultObject));
                    // 删除依赖销售品
                    if ("0".equals(listCpcResultObject.getResultCode())) {
                        List<CouponApplyObjectDto> couponApplyObjectDtos = listCpcResultObject.getResultObject();
                        for (CouponApplyObjectDto applyObjectDto : couponApplyObjectDtos) {
                            applyObjectDto.setActType("DEL");
                        }
                        logger.info("9---> modifyCouponApplyObject入参（DEL） --->>>" + JSON.toJSONString(couponApplyObjectDtos));
                        CpcResultObject<Boolean> booleanCpcResultObject = iCouponApplyObjectWriteService.modifyCouponApplyObject(couponApplyObjectDtos);
                        logger.info("9---> modifyCouponApplyObject出参（DEL） --->>>" + JSON.toJSONString(booleanCpcResultObject));
                    }
                    String[] dependOfferIds = mktCamResource.getDependOfferId().split("/");
                    List<CouponApplyObjectDto> couponApplyObjectDtos = new ArrayList<>();
                    for (String dependOfferId : dependOfferIds) {
                        // 新增依赖销售品
                        MktCamItem mktCamItem = camItemMapper.selectByPrimaryKey(Long.valueOf(dependOfferId));
                        CouponApplyObjectDto couponApplyObjectDtoNew = new CouponApplyObjectDto();
                        couponApplyObjectDtoNew.setMktResId(mktResId);
                        couponApplyObjectDtoNew.setObjId(mktCamItem.getItemId());
                        couponApplyObjectDtoNew.setObjType(Long.valueOf(mktCamItem.getItemType()));
                        couponApplyObjectDtoNew.setStaffId(UserUtil.loginId());
                        couponApplyObjectDtoNew.setActType("ADD");
                        couponApplyObjectDtoNew.setRemark(ProductTypeEnum.DEPEND_OFFER.getName());
                        couponApplyObjectDtos.add(couponApplyObjectDtoNew);
                    }
                    logger.info("10--->>> 入参（ADD）" + JSON.toJSONString(couponApplyObjectDtos));
                    CpcResultObject<Boolean> booleanCpcResultObject = iCouponApplyObjectWriteService.modifyCouponApplyObject(couponApplyObjectDtos);
                    logger.info("10--->>> 出参（ADD）" + JSON.toJSONString(booleanCpcResultObject));
                }


                //批零差包 销售品条目id(多个以“/”分开)
                if (mktCamResource.getDifferentOfferId() != null && !"".equals(mktCamResource.getDifferentOfferId())) {
                    CouponApplyObjectDto couponApplyObjectDto = new CouponApplyObjectDto();
                    couponApplyObjectDto.setMktResId(mktResId);
                    couponApplyObjectDto.setRemark(ProductTypeEnum.DIFFERENT_OFFER.getName());
                    // 查询批零差包
                    logger.info("11--->>> 入参：" + JSON.toJSONString(couponApplyObjectDto));
                    CpcResultObject<List<CouponApplyObjectDto>> listCpcResultObject = iCouponApplyObjectService.qryCouponApplyObjectList(couponApplyObjectDto);
                    logger.info("11--->>> 出参：" + JSON.toJSONString(listCpcResultObject));
                    // 删除批零差包
                    if ("0".equals(listCpcResultObject.getResultCode())) {
                        List<CouponApplyObjectDto> couponApplyObjectDtos = listCpcResultObject.getResultObject();
                        for (CouponApplyObjectDto applyObjectDto : couponApplyObjectDtos) {
                            applyObjectDto.setActType("DEL");
                        }
                        logger.info("12---> modifyCouponApplyObject入参（DEL） --->>>" + JSON.toJSONString(couponApplyObjectDtos));
                        CpcResultObject<Boolean> booleanCpcResultObject = iCouponApplyObjectWriteService.modifyCouponApplyObject(couponApplyObjectDtos);
                        logger.info("12---> modifyCouponApplyObject出参（DEL） --->>>" + JSON.toJSONString(booleanCpcResultObject));
                    }
                    String[] dependOfferIds = mktCamResource.getDependOfferId().split("/");
                    List<CouponApplyObjectDto> couponApplyObjectDtos = new ArrayList<>();
                    for (String dependOfferId : dependOfferIds) {
                        // 新增批零差包
                        MktCamItem mktCamItem = camItemMapper.selectByPrimaryKey(Long.valueOf(dependOfferId));
                        CouponApplyObjectDto couponApplyObjectDtoNew = new CouponApplyObjectDto();
                        couponApplyObjectDtoNew.setMktResId(mktResId);
                        couponApplyObjectDtoNew.setObjId(mktCamItem.getItemId());
                        couponApplyObjectDtoNew.setObjType(Long.valueOf(mktCamItem.getItemType()));
                        couponApplyObjectDtoNew.setStaffId(UserUtil.loginId());
                        couponApplyObjectDtoNew.setActType("ADD");
                        couponApplyObjectDtoNew.setRemark(ProductTypeEnum.DIFFERENT_OFFER.getName());
                        couponApplyObjectDtos.add(couponApplyObjectDtoNew);
                    }
                    logger.info("13--->>> 入参（ADD）" + JSON.toJSONString(couponApplyObjectDtos));
                    CpcResultObject<Boolean> booleanCpcResultObject = iCouponApplyObjectWriteService.modifyCouponApplyObject(couponApplyObjectDtos);
                    logger.info("13--->>> 出参（ADD）" + JSON.toJSONString(booleanCpcResultObject));
                }
            }

            resultMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            resultMap.put("resultMsg", "查询成功！");
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getProjectList失败= {}", e);
            resultMap.put("resultCode", CommonConstant.CODE_FAIL);
            resultMap.put("resultMsg", "查询失败！");
            return resultMap;
        }
    }


}
