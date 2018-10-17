package com.zjtelcom.cpct.service.impl.channel;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.InjectionLabelCatalogMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelValueMapper;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.LabelCatalog;
import com.zjtelcom.cpct.domain.channel.LabelValue;
import com.zjtelcom.cpct.dto.channel.*;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.LabelCatalogService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class LabelCatalogServiceImpl extends BaseService implements LabelCatalogService {
    @Autowired
    private InjectionLabelMapper labelMapper;
    @Autowired
    private InjectionLabelCatalogMapper labelCatalogMapper;
    @Autowired
    private InjectionLabelValueMapper labelValueMapper;


    @Override
    public Map<String, Object> batchAdd(List<String> nameList, Long parentId, Long level) {
        Map<String,Object> result = new HashMap<>();
        for (String st : nameList){
            LabelCatalog addvo = new LabelCatalog();
            addvo.setLevelId(level);
//            addvo.setParentId(parentId);
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
     * 通过目录节点获取标签列表
     * @param catalogId
     * @return
     */
    @Override
    public Map<String, Object> listLabelByCatalogId(Long catalogId) {
        Map<String,Object> result = new HashMap<>();
        List<Label> labelList = labelMapper.findLabelListByCatalogId(catalogId);
        List<LabelDTO> voList = new ArrayList<>();
        for (Label label : labelList){
            LabelDTO vo = BeanUtil.create(label,new LabelDTO());
            voList.add(vo);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",voList);
        return result;
    }

    /**
     * 标签树
     * @return
     */
    @Override
    public Map<String, Object> listLabelCatalog() {
//        List<Label> resuList = new ArrayList<>();
//        List<Label> labels = labelMapper.selectAllByCondition();
//        List<LabelValue> vas = labelValueMapper.selectAll();
//        List<Long> idlist = new ArrayList<>();
//        for (LabelValue value : vas){
//            idlist.add(value.getInjectionLabelId());
//        }
//        for (Label label : labels){
//            if (!idlist.contains(label.getInjectionLabelId())){
//                label.setConditionType("4");
//                label.setOperator("2000,3000,1000,4000,6000,5000,7000,7200");
//                labelMapper.updateByPrimaryKey(label);
//            }
//        }

        Map<String,Object> result = new HashMap<>();
        List<CatalogTreeParent> resultTree = new ArrayList<>();

        List<LabelCatalog> parentList = labelCatalogMapper.findByParentId(String.valueOf(0));
        List<Label> allLabels = labelMapper.selectAll();
        List<LabelCatalog> allCatalogs = labelCatalogMapper.selectAll();
        List<LabelValue> valueList = labelValueMapper.selectAll();

        for (LabelCatalog parent : parentList) {
            CatalogTreeParent parentTree = new CatalogTreeParent();
            parentTree.setInjectionLabelId(parent.getCatalogId());
            parentTree.setInjectionLabelName(parent.getCatalogName());

            List<LabelCatalogTree> onceTreeList = new ArrayList<>();
            List<LabelCatalog> firstList = labelCatalogMapper.findByParentId(parent.getCatalogCode());
            for (LabelCatalog first : firstList) {
                LabelCatalogTree firstTree = new LabelCatalogTree();
                firstTree.setInjectionLabelId(first.getCatalogId());
                firstTree.setInjectionLabelName(first.getCatalogName());

                List<CatalogTreeTwo> twiceTreeList = new ArrayList<>();
                List<LabelCatalog> twiceList = getCatalogListByParentId(allCatalogs, first.getCatalogCode());
                for (LabelCatalog twice : twiceList) {
                    CatalogTreeTwo twiceTree = new CatalogTreeTwo();
                    twiceTree.setInjectionLabelId(twice.getCatalogId());
                    twiceTree.setInjectionLabelName(twice.getCatalogName());

                    List<CatalogTreeThree> thirdTreeList = new ArrayList<>();
                    List<LabelCatalog> thirdList = getCatalogListByParentId(allCatalogs, twice.getCatalogCode());
                    for (LabelCatalog third : thirdList) {
                        CatalogTreeThree thirdTree = new CatalogTreeThree();
                        thirdTree.setInjectionLabelId(third.getCatalogId());
                        thirdTree.setInjectionLabelName(third.getCatalogName());

                        List<LabelVO> labelVOList = new ArrayList<>();
                        for (Label label : allLabels) {
                            if (label.getCatalogId() == null || !label.getCatalogId().equals(third.getCatalogCode())) {
                                continue;
                            }
                            List<LabelValue> values = new ArrayList<>();
                            for (LabelValue value : valueList) {
                                if (value.getInjectionLabelId() != null && value.getInjectionLabelId().equals(label.getInjectionLabelId())) {
                                    values.add(value);
                                }
                            }
                            LabelVO vo = ChannelUtil.map2LabelVO(label, values);
                            labelVOList.add(vo);
                        }
                        thirdTree.setChildren(labelVOList);
                        thirdTreeList.add(thirdTree);
                    }
                    twiceTree.setChildren(thirdTreeList);
                    twiceTreeList.add(twiceTree);
                }
                firstTree.setChildren(twiceTreeList);
                onceTreeList.add(firstTree);
            }
            parentTree.setChildren(onceTreeList);
            resultTree.add(parentTree);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",resultTree);
        return result;
    }

    private List<LabelCatalog> getCatalogListByParentId(List<LabelCatalog> allList,String catalogCode){
        List<LabelCatalog> resultList = new ArrayList<>();
        for (LabelCatalog catalog : allList){
            if (!catalog.getParentId().equals(catalogCode)){
                continue;
            }
            resultList.add(catalog);
        }
        return resultList;
    }
}
