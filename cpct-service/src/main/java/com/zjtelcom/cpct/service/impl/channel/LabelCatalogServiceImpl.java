package com.zjtelcom.cpct.service.impl.channel;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.InjectionLabelCatalogMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.LabelCatalog;
import com.zjtelcom.cpct.dto.channel.LabelCatalogTree;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.LabelCatalogService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class LabelCatalogServiceImpl extends BaseService implements LabelCatalogService {
    @Autowired
    private InjectionLabelMapper labelMapper;
    @Autowired
    private InjectionLabelCatalogMapper labelCatalogMapper;


    @Override
    public Map<String, Object> batchAdd(List<String> nameList, Long parentId, Long level) {
        Map<String,Object> result = new HashMap<>();
        for (String st : nameList){
            LabelCatalog addvo = new LabelCatalog();
            addvo.setLevelId(level);
            addvo.setParentId(parentId);
            addvo.setCatalogName(st);
            addLabelCatalog(addvo);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");
        return result;
    }

    /**
     * 添加目录节点
     * @param addVO
     * @return
     */
    @Override
    public Map<String, Object> addLabelCatalog(LabelCatalog addVO) {
        Map<String,Object> result = new HashMap<>();
        LabelCatalog catalog = BeanUtil.create(addVO,new LabelCatalog());
        catalog.setCreateDate(DateUtil.getCurrentTime());
        catalog.setUpdateDate(DateUtil.getCurrentTime());
        catalog.setStatusDate(DateUtil.getCurrentTime());
        catalog.setUpdateStaff(UserUtil.loginId());
        catalog.setCreateStaff(UserUtil.loginId());
        catalog.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
        labelCatalogMapper.insert(catalog);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");
        return result;
    }

    /**
     * 标签树
     * @return
     */
    @Override
    public Map<String, Object> listLabelCatalog() {
        Map<String,Object> result = new HashMap<>();
        List<LabelCatalogTree> resultTree = new ArrayList<>();

        List<LabelCatalog> firstList = labelCatalogMapper.findByLevelId(1L);
        List<Label> allLabels = labelMapper.selectAll();
        List<LabelCatalog> allCatalogs = labelCatalogMapper.selectAll();

        for (LabelCatalog first : firstList){
            LabelCatalogTree firstTree = new LabelCatalogTree();
            firstTree.setId(first.getCatalogId());
            firstTree.setName(first.getCatalogName());
            List<LabelCatalogTree> twiceTreeList = new ArrayList<>();
            List<LabelCatalog> twiceList = getCatalogListByParentId(allCatalogs,first.getCatalogId());
            for (LabelCatalog twice : twiceList){
                LabelCatalogTree twiceTree = new LabelCatalogTree();
                twiceTree.setId(twice.getCatalogId());
                twiceTree.setName(twice.getCatalogName());

                List<LabelCatalogTree> thirdTreeList = new ArrayList<>();
                List<LabelCatalog> thirdList = getCatalogListByParentId(allCatalogs,twice.getCatalogId());
                for (LabelCatalog third : thirdList){
                    LabelCatalogTree thirdTree = new LabelCatalogTree();
                    thirdTree.setId(third.getCatalogId());
                    thirdTree.setName(third.getCatalogName());

                    List<LabelCatalogTree> forceTreeList = new ArrayList<>();
                    for (Label label : allLabels) {
                        if (label.getCatalogId()==null || !label.getCatalogId().equals(third.getCatalogId())){
                            continue;
                        }
                        LabelCatalogTree forceTree = new LabelCatalogTree();
                        forceTree.setId(label.getInjectionLabelId());
                        forceTree.setName(label.getInjectionLabelName());
                        forceTreeList.add(forceTree);
                    }
                    thirdTree.setChildren(forceTreeList);
                    thirdTreeList.add(thirdTree);
                }
                twiceTree.setChildren(thirdTreeList);
                twiceTreeList.add(twiceTree);
            }
            firstTree.setChildren(twiceTreeList);
            resultTree.add(firstTree);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",resultTree);
        return result;
    }



    private List<LabelCatalog> getCatalogListByParentId(List<LabelCatalog> allList,Long catalogId){
        List<LabelCatalog> resultList = new ArrayList<>();
        for (LabelCatalog catalog : allList){
            if (!catalog.getParentId().equals(catalogId)){
                continue;
            }
            resultList.add(catalog);
        }
        return resultList;
    }
}