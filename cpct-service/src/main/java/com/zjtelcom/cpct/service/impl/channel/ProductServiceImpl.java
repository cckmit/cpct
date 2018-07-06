package com.zjtelcom.cpct.service.impl.channel;

import com.zjtelcom.cpct.dao.channel.MktProductRuleMapper;
import com.zjtelcom.cpct.dao.channel.PpmProductMapper;
import com.zjtelcom.cpct.domain.channel.MktProductRule;
import com.zjtelcom.cpct.domain.channel.PpmProduct;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class ProductServiceImpl extends BaseService implements ProductService {

    @Autowired
    private PpmProductMapper productMapper;
    @Autowired
    private MktProductRuleMapper ruleMapper;


    @Override
    public List<PpmProduct> getProductList(Long userId,String productName){
        List<PpmProduct> productList = new ArrayList<>();
        try {
            productList = productMapper.findByProductName(productName);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[op:ProductServiceImpl] fail to getProductList ", e);
        }
        return productList;
    }


    @Override
    @Transactional
    public Map<String, Object> addProductRule(Long userId, List<Long> productIdList) {
        Map<String,Object> result = new HashMap<>();
        List<Long> ruleIdList = new ArrayList<>();
        for (Long productId : productIdList){
            PpmProduct product = productMapper.selectByPrimaryKey(productId);
            if (product==null){
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","产品不存在");
                return result;
            }
            MktProductRule rule = new MktProductRule();
            rule.setProductId(productId);
            rule.setProductName(product.getProductName());
            ruleMapper.insert(rule);
            ruleIdList.add(rule.getId());
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",ruleIdList);
        return result;
    }

    @Override
    public Map<String, Object> editProductRule(Long userId, Long ruleId, String remark) {
        Map<String,Object> result = new HashMap<>();
        MktProductRule rule = ruleMapper.selectByPrimaryKey(ruleId);
        if (rule==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","关联销售品记录不存在");
            return result;
        }
        rule.setRemark(remark);
        ruleMapper.updateByPrimaryKey(rule);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","编辑成功");
        return result;
    }

    @Override
    public Map<String, Object> getProductRuleList(Long userId, List<Long> ruleIdList) {
        Map<String,Object> result = new HashMap<>();
        List<MktProductRule> ruleList = new ArrayList<>();
        for (Long ruleId : ruleIdList){
            MktProductRule rule = ruleMapper.selectByPrimaryKey(ruleId);
            if (rule==null){
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","关联销售品记录不存在");
                return result;
            }
            ruleList.add(rule);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",ruleList);
        return result;
    }

    @Override
    public Map<String, Object> delProductRule(Long userId, Long ruleId) {
        Map<String,Object> result = new HashMap<>();
        MktProductRule rule = ruleMapper.selectByPrimaryKey(ruleId);
        if (rule==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","关联销售品记录不存在");
            return result;
        }
        ruleMapper.deleteByPrimaryKey(ruleId);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","删除成功");
        return result;
    }
}
