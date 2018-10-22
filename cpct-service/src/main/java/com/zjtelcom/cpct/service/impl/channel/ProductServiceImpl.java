package com.zjtelcom.cpct.service.impl.channel;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCamItemMapper;
import com.zjtelcom.cpct.dao.channel.MktProductRuleMapper;
import com.zjtelcom.cpct.dao.channel.OfferMapper;
import com.zjtelcom.cpct.dao.channel.PpmProductMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamItem;
import com.zjtelcom.cpct.domain.channel.MktProductRule;
import com.zjtelcom.cpct.domain.channel.Offer;
import com.zjtelcom.cpct.domain.channel.PpmProduct;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConf;
import com.zjtelcom.cpct.dto.channel.OfferDetail;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.ProductService;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfRuleService;
import com.zjtelcom.cpct.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class ProductServiceImpl extends BaseService implements ProductService {

    @Autowired
    private OfferMapper productMapper;
    @Autowired
    private MktCamItemMapper camItemMapper;
    @Autowired
    private MktStrategyConfRuleService strategyConfRuleService;
    @Autowired
    private RedisUtils redisUtils;


    @Override
    public Map<String, Object> getProductNameById(Long userId, List<Long> productIdList) {
        Map<String,Object> result = new HashMap<>();
        List<OfferDetail> nameList = new ArrayList<>();
        for (Long productId : productIdList){
            Offer product = productMapper.selectByPrimaryKey(Integer.valueOf(productId.toString()));
            if (product==null){
                continue;
            }
            OfferDetail offerDetail = BeanUtil.create(product,new OfferDetail());
            nameList.add(offerDetail);
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
            productList = productMapper.findByName(productName);
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
            for (Long itemId : ItemIdList) {
                MktCamItem item = camItemMapper.selectByPrimaryKey(itemId);
                if (item == null) {
                    continue;
                }
                MktCamItem newItem = BeanUtil.create(item, new MktCamItem());
                newItem.setMktCamItemId(null);
                camItemMapper.insert(newItem);
                ruleIdList.add(newItem.getMktCamItemId());
            }
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("ruleIdList",ruleIdList);
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> addProductRule(Long strategyRuleId, List<Long> productIdList) {
        Map<String,Object> result = new HashMap<>();
        List<Long> ruleIdList = new ArrayList<>();
        for (Long productId : productIdList){
            Offer product = productMapper.selectByPrimaryKey(Integer.valueOf(productId.toString()));
            if (product==null){
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","产品不存在");
                return result;
            }
            MktCamItem item = new MktCamItem();
            item.setOfferCode(product.getOfferNbr());
            item.setOfferName(product.getOfferName());
            item.setItemId(productId);
            item.setItemType(product.getOfferType());
            item.setCreateDate(new Date());
            item.setCreateDate(DateUtil.getCurrentTime());
            item.setUpdateDate(DateUtil.getCurrentTime());
            item.setStatusDate(DateUtil.getCurrentTime());
            item.setUpdateStaff(UserUtil.loginId());
            item.setCreateStaff(UserUtil.loginId());
            item.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
            camItemMapper.insert(item);
            ruleIdList.add(item.getMktCamItemId());
        }
        if (strategyRuleId!=null){
            strategyConfRuleService.updateProductIds(ruleIdList,strategyRuleId);
        }
        //redis添加推荐条目数据
        Map<String,Object> productResult = getProductNameById(1L,productIdList);
        if (productResult.get("resultCode").equals(CODE_SUCCESS)){
            redisUtils.set("MKT_CAM_ITEM_"+strategyRuleId,productResult.get("nameList"));
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",ruleIdList);
        return result;
    }

    @Override
    public Map<String, Object> editProductRule(Long userId, Long ruleId, String remark) {
        Map<String,Object> result = new HashMap<>();
        MktCamItem rule = camItemMapper.selectByPrimaryKey(ruleId);
        if (rule==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","推荐条目不存在");
            return result;
        }
        rule.setRemark(remark);
        camItemMapper.updateByPrimaryKey(rule);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","编辑成功");
        return result;
    }

    @Override
    public Map<String, Object> getProductRuleList(Long userId, List<Long> ruleIdList) {
        Map<String,Object> result = new HashMap<>();
        List<MktProductRule> ruleList = new ArrayList<>();
        for (Long ruleId : ruleIdList){
            MktCamItem item = camItemMapper.selectByPrimaryKey(ruleId);
            if (item==null){
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","推荐条目不存在");
                return result;
            }
            Offer product = productMapper.selectByPrimaryKey(Integer.valueOf(item.getItemId().toString()));
            if (product==null){
                continue;
            }
            MktProductRule rule = new MktProductRule();
            rule.setId(item.getMktCamItemId());
            rule.setProductId(item.getItemId());
            rule.setProductName(product.getOfferName());
            rule.setRemark(item.getRemark());
            ruleList.add(rule);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",ruleList);
        return result;
    }

    @Override
    public Map<String, Object> delProductRule(Long strategyRuleId, Long ruleId,List<Long> itemRuleIdList) {
        Map<String,Object> result = new HashMap<>();
        if (strategyRuleId!=null){
            strategyConfRuleService.updateProductIds(itemRuleIdList,strategyRuleId);
            MktCamItem rule = camItemMapper.selectByPrimaryKey(ruleId);
            if (rule==null){
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","推荐条目不存在");
                return result;
            }
            camItemMapper.deleteByPrimaryKey(ruleId);
            //更新redis数据
            deleteRedisMktCamItem(strategyRuleId,rule);
        }
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
}
