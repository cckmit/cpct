package com.zjtelcom.cpct.service.impl.channel;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCamItemMapper;
import com.zjtelcom.cpct.dao.channel.MktProductRuleMapper;
import com.zjtelcom.cpct.dao.channel.PpmProductMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamItem;
import com.zjtelcom.cpct.domain.channel.MktProductRule;
import com.zjtelcom.cpct.domain.channel.PpmProduct;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.ProductService;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.MapUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class ProductServiceImpl extends BaseService implements ProductService {

    @Autowired
    private PpmProductMapper productMapper;
    @Autowired
    private MktCamItemMapper camItemMapper;


    @Override
    public Map<String, Object> getProductNameById(Long userId, List<Long> productIdList) {
        Map<String,Object> result = new HashMap<>();
        List<String> nameList = new ArrayList<>();
        for (Long productId : productIdList){
            PpmProduct product = productMapper.selectByPrimaryKey(productId);
            if (product==null){
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","产品不存在");
                return result;
            }
            nameList.add(product.getProductName());
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",nameList);
        return result;
    }

    @Override
    public Map<String,Object> getProductList(Long userId,Map<String,Object> params){
        Map<String,Object> result = new HashMap<>();
        List<PpmProduct> productList = new ArrayList<>();
            Integer page = MapUtil.getIntNum(params.get("page"));
            Integer pageSize = MapUtil.getIntNum(params.get("pageSize"));
            String productName = null;
            if (params.get("productName")!=null){
                productName = params.get("productName").toString();
            }
            PageHelper.startPage(page,pageSize);
            productList = productMapper.findByProductName(productName);
            Page pageInfo = new Page(new PageInfo(productList));
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",productList);
        result.put("page",pageInfo);
        return result;
    }


    @Override
    @Transactional
    public Map<String, Object> addProductRule(Long userId,Long campaignId, List<Long> productIdList) {
        Map<String,Object> result = new HashMap<>();
        List<Long> ruleIdList = new ArrayList<>();
        for (Long productId : productIdList){
            PpmProduct product = productMapper.selectByPrimaryKey(productId);
            if (product==null){
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","产品不存在");
                return result;
            }
            MktCamItem item = new MktCamItem();
            item.setItemId(productId);
            item.setMktCampaignId(campaignId);
            item.setItemType(product.getProductType());
            item.setCreateDate(new Date());
            item.setCreateDate(DateUtil.getCurrentTime());
            item.setUpdateDate(DateUtil.getCurrentTime());
            item.setStatusDate(DateUtil.getCurrentTime());
            item.setUpdateStaff(UserUtil.loginId());
            item.setCreateStaff(UserUtil.loginId());
            item.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
            camItemMapper.insert(item);
            ruleIdList.add(item.getItemId());
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
        List<MktCamItem> ruleList = new ArrayList<>();
        for (Long ruleId : ruleIdList){
            MktCamItem rule = camItemMapper.selectByPrimaryKey(ruleId);
            if (rule==null){
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","推荐条目不存在");
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
        MktCamItem rule = camItemMapper.selectByPrimaryKey(ruleId);
        if (rule==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","推荐条目不存在");
            return result;
        }
        camItemMapper.deleteByPrimaryKey(ruleId);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","删除成功");
        return result;
    }
}
