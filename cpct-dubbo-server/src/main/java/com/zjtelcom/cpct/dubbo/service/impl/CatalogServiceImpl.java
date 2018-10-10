package com.zjtelcom.cpct.dubbo.service.impl;

import com.zjtelcom.cpct.dao.channel.InjectionLabelCatalogMapper;
import com.zjtelcom.cpct.dubbo.model.LabelCatalog;
import com.zjtelcom.cpct.dubbo.model.LabelCatalogModel;
import com.zjtelcom.cpct.dubbo.service.CatalogService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class CatalogServiceImpl implements CatalogService {
    @Autowired(required = false)
    private InjectionLabelCatalogMapper labelCatalogMapper;


    @Override
    public Map<String, Object> syncLabelCatalogList(LabelCatalogModel req) {
        Map<String,Object> result = new HashMap<>();
        for (LabelCatalog catalog : req.getCatalogList()){
            com.zjtelcom.cpct.domain.channel.LabelCatalog entity = BeanUtil.create(catalog,new com.zjtelcom.cpct.domain.channel.LabelCatalog());
            labelCatalogMapper.insert(entity);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","导入成功");
        return result;
    }

    @Override
    public Map<String, Object> syncLabelCatalog(LabelCatalog req) {
        Map<String,Object> result = new HashMap<>();
        com.zjtelcom.cpct.domain.channel.LabelCatalog catalog = new com.zjtelcom.cpct.domain.channel.LabelCatalog();
        switch (req.getOperateType()){
            case "new":
                catalog = BeanUtil.create(req,new com.zjtelcom.cpct.domain.channel.LabelCatalog());
                catalog.setCatalogId(null);
                catalog.setCreateDate(DateUtil.getCurrentTime());
                catalog.setUpdateDate(DateUtil.getCurrentTime());
                catalog.setStatusDate(DateUtil.getCurrentTime());
                catalog.setUpdateStaff(UserUtil.loginId());
                catalog.setStatusCd("1000");
                catalog.setCreateStaff(UserUtil.loginId());
                labelCatalogMapper.insert(catalog);
                break;
            case "update":
                catalog = labelCatalogMapper.selectByPrimaryKey(req.getCatalogId());
                if (catalog==null){
                    result.put("resultCode",CODE_FAIL);
                    result.put("resultMsg","更新失败，目录不存在");
                    return result;
                }
                BeanUtil.copy(req,catalog);
                labelCatalogMapper.updateByPrimaryKey(catalog);
                break;
            case "delete":
                catalog = labelCatalogMapper.selectByPrimaryKey(req.getCatalogId());
                if (catalog==null){
                    result.put("resultCode",CODE_FAIL);
                    result.put("resultMsg","目录不存在");
                    return result;
                }
                labelCatalogMapper.deleteByPrimaryKey(catalog.getCatalogId());
                break;
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","成功");
        return result;
    }
}
