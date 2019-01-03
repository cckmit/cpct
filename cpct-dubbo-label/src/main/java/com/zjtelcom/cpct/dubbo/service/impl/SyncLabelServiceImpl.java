package com.zjtelcom.cpct.dubbo.service.impl;

import com.zjtelcom.cpct.dao.channel.InjectionLabelCatalogMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelValueMapper;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.LabelCatalog;
import com.zjtelcom.cpct.domain.channel.LabelValue;
import com.zjtelcom.cpct.dto.channel.*;
import com.zjtelcom.cpct.dubbo.model.LabModel;
import com.zjtelcom.cpct.dubbo.model.LabValueModel;
import com.zjtelcom.cpct.dubbo.model.RecordModel;
import com.zjtelcom.cpct.dubbo.service.SyncLabelService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import com.zjtelcom.cpct.util.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.*;


/**
 * Description:
 * author: hyf
 * date: 2018/07/17 11:11
 * version: V1.0
 */
@Service
public class SyncLabelServiceImpl  implements SyncLabelService {
    public static final Logger logger = LoggerFactory.getLogger(SyncLabelServiceImpl.class);
    @Autowired(required = false)
    private InjectionLabelMapper labelMapper;
    @Autowired(required = false)
    private InjectionLabelValueMapper labelValueMapper;
    @Autowired(required = false)
    private InjectionLabelCatalogMapper labelCatalogMapper;
    @Autowired(required = false)
    private RedisUtils redisUtils;

    /**
     * 标签同步对外接口
     * @param record
     * @return
     */
    @Override
    @Transactional
    public Map<String, Object> syncLabelInfo(RecordModel record) {
        Map<String,Object> result = new HashMap<>();
        try {
            switch (record.getLabel().getLabState()){
                case "3":
                    result = addLabel(record);
                    break;
                case "5":
                    result = deleteLabel(record);
                    break;
            }
        }catch (Exception e){
            logger.error("[op:SyncLabelServiceImpl] fail to syncLabelInfo",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to syncLabelInfo");
            return result;
        }
        return result;
    }


    /**
     * 新增标签
     * @param record
     * @return
     */
    private Map<String,Object> addLabel(RecordModel record) {
        Map<String, Object> result = new HashMap<>();
        LabModel labModel = record.getLabel();
        Label labelValodate = labelMapper.selectByTagRowId(record.getLabel().getLabRowId());
        List<LabValueModel> valueModelList = new ArrayList<>();
        if (record.getLabelValueList() != null && !record.getLabelValueList().isEmpty()) {
            valueModelList = record.getLabelValueList();
        }
        if (labelValodate != null) {
            BeanUtil.copy(labModel,labelValodate);
            labelValodate.setSystemInfoId(1L);
            labelValodate.setTagRowId(labModel.getLabRowId());//标签id
            labelValodate.setInjectionLabelName(labModel.getLabName());//标签名称
            labelValodate.setInjectionLabelCode(labModel.getLabEngName());//英文名称
            //label.setInjectionLabelDesc();
            labelValodate.setLabTagCode(labModel.getLabCode());//标签编码
            labelValodate.setInjectionLabelDesc(labModel.getLabBusiDesc());


            labelValodate.setConditionType(labModel.getLabType());
            if("4".equals(labelValodate.getConditionType())){
                labelValodate.setOperator("2000,3000,1000,4000,6000,5000,7000,7200");
            }else if("2".equals(labelValodate.getConditionType())){
                labelValodate.setOperator("7000");
            }
            labelValodate.setCatalogId(labModel.getLabObjectCode() + labModel.getLabLevel1() + labModel.getLabLevel2() + labModel.getLabLevel3());

            if (record.getLabelValueList()!=null && !record.getLabelValueList().isEmpty()){
                labelValodate.setLabelValueType("2000");
            }else {
                labelValodate.setLabelValueType("1000");
            }
            //label.setLabelDataType(ChannelUtil.getDataType(tagModel.getSourceTableColumnType()));
            if (labelValodate.getLabObject().equals("客户级")){
                labelValodate.setLabelType("1000");
            }else if (labelValodate.getLabObject().equals("用户级")) {
                labelValodate.setLabelType("2000");
            }else if (labelValodate.getLabObject().equals("销售品级")) {
                labelValodate.setLabelType("3000");
            }else if (labelValodate.getLabObject().equals("区域级")) {
                labelValodate.setLabelType("4000");
            }
            labelMapper.updateByPrimaryKey(labelValodate);
            //redis更新标签库
            redisUtils.set("LABEL_LIB_"+labelValodate.getInjectionLabelId(),labelValodate);
            syncLabelValue(valueModelList,labelValodate.getInjectionLabelId());
        }else {

            Label label = BeanUtil.create(labModel, new Label());
            label.setSystemInfoId(1L);
            label.setTagRowId(labModel.getLabRowId());//标签id
            label.setInjectionLabelName(labModel.getLabName());//标签名称
            label.setInjectionLabelCode(labModel.getLabEngName());//英文名称
            //label.setInjectionLabelDesc();
            label.setLabTagCode(labModel.getLabCode());//标签编码
            label.setInjectionLabelDesc(labModel.getLabBusiDesc());
            label.setConditionType(labModel.getLabType());
            if("4".equals(label.getConditionType())){
                label.setOperator("2000,3000,1000,4000,6000,5000,7000,7200");
            }else if("2".equals(label.getConditionType())){
                label.setOperator("7000");
            }
            label.setScope(1);
            label.setIsShared(0);
            label.setCatalogId(labModel.getLabObjectCode() + labModel.getLabLevel1() + labModel.getLabLevel2() + labModel.getLabLevel3());

            //todo 暂无标签类型
            label.setLabelType("1000");
            //todo 暂无值类型
            if (record.getLabelValueList()!=null && !record.getLabelValueList().isEmpty()){
                label.setLabelValueType("2000");
            }else {
                label.setLabelValueType("1000");
            }
            //label.setLabelDataType(ChannelUtil.getDataType(tagModel.getSourceTableColumnType()));

            label.setStatusCd(STATUSCD_EFFECTIVE);
            label.setCreateStaff(UserUtil.loginId());
            label.setCreateDate(new Date());
            if (label.getLabObject().equals("客户级")){
                label.setLabelType("1000");
            }else if (label.getLabObject().equals("用户级")) {
                label.setLabelType("2000");
            }else if (label.getLabObject().equals("销售品级")) {
                label.setLabelType("3000");
            }else if (label.getLabObject().equals("区域级")) {
                label.setLabelType("4000");
            }
            labelMapper.insert(label);
            //redis更新标签库
            redisUtils.set("LABEL_LIB_"+label.getInjectionLabelId(),label);
            syncLabelValue(valueModelList,label.getInjectionLabelId());
        }


//        //标签目录插入
//        LabelCatalog labelCatalog =new LabelCatalog();
//        labelCatalog.setStatusCd(STATUSCD_EFFECTIVE);
//        labelCatalog.setCreateStaff(UserUtil.loginId());
//        labelCatalog.setCreateDate(new Date());
//        if(labelCatalogMapper.findByCodeAndLevel(record.getLabel().getLabObjectCode(), Long.valueOf(1)) == null) {
//            labelCatalog.setCatalogCode(record.getLabel().getLabObjectCode());
//            labelCatalog.setCatalogName(record.getLabel().getLabObject());
//            labelCatalog.setLevelId(Long.valueOf(1));
//            labelCatalog.setParentId("0");
//            labelCatalogMapper.insert(labelCatalog);
//        }
//        if(labelCatalogMapper.findByCodeAndLevel((record.getLabel().getLabObjectCode() + record.getLabel().getLabLevel1()), Long.valueOf(2)) == null) {
//            labelCatalog.setCatalogCode(record.getLabel().getLabObjectCode() + record.getLabel().getLabLevel1());
//            labelCatalog.setCatalogName(record.getLabel().getLabLevel1Name());
//            labelCatalog.setLevelId(Long.valueOf(2));
//            labelCatalog.setParentId(record.getLabel().getLabObjectCode());
//            labelCatalogMapper.insert(labelCatalog);
//        }
//        if(labelCatalogMapper.findByCodeAndLevel((record.getLabel().getLabObjectCode() + record.getLabel().getLabLevel1() + record.getLabel().getLabLevel2()), Long.valueOf(3)) == null) {
//            labelCatalog.setCatalogCode(record.getLabel().getLabObjectCode() + record.getLabel().getLabLevel1() + record.getLabel().getLabLevel2());
//            labelCatalog.setCatalogName(record.getLabel().getLabLevel2Name());
//            labelCatalog.setLevelId(Long.valueOf(3));
//            labelCatalog.setParentId(record.getLabel().getLabObjectCode() + record.getLabel().getLabLevel1());
//            labelCatalogMapper.insert(labelCatalog);
//        }
//        if(labelCatalogMapper.findByCodeAndLevel((record.getLabel().getLabObjectCode() + record.getLabel().getLabLevel1() + record.getLabel().getLabLevel2() + record.getLabel().getLabLevel3()), Long.valueOf(4)) == null) {
//            labelCatalog.setCatalogCode(record.getLabel().getLabObjectCode() + record.getLabel().getLabLevel1() + record.getLabel().getLabLevel2() + record.getLabel().getLabLevel3());
//            labelCatalog.setCatalogName(record.getLabel().getLabLevel3Name());
//            labelCatalog.setLevelId(Long.valueOf(4));
//            labelCatalog.setParentId(record.getLabel().getLabObjectCode() + record.getLabel().getLabLevel1() + record.getLabel().getLabLevel2());
//            labelCatalogMapper.insert(labelCatalog);
//        }
//        if(labelCatalogMapper.findByCodeAndLevel((record.getLabel().getLabObjectCode() + record.getLabel().getLabLevel1() + record.getLabel().getLabLevel2() + record.getLabel().getLabLevel3() + record.getLabel().getLabLevel4()), Long.valueOf(5)) == null) {
//            labelCatalog.setCatalogCode(record.getLabel().getLabObjectCode() + record.getLabel().getLabLevel1() + record.getLabel().getLabLevel2() + record.getLabel().getLabLevel3() + record.getLabel().getLabLevel4());
//            labelCatalog.setCatalogName(record.getLabel().getLabLevel4Name());
//            labelCatalog.setLevelId(Long.valueOf(5));
//            labelCatalog.setParentId(record.getLabel().getLabObjectCode() + record.getLabel().getLabLevel1() + record.getLabel().getLabLevel2() + record.getLabel().getLabLevel3());
//            labelCatalogMapper.insert(labelCatalog);
//        }

        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","新增成功");
        return result;
    }

    /**
     * 更新标签
     * @param record
     * @return
     */
    private Map<String,Object> updateLabel(RecordModel record){
        Map<String,Object> result = new HashMap<>();
        //todo 先删除再添加
        LabModel tagModel = record.getLabel();
        Label label = labelMapper.selectByTagRowId(tagModel.getLabRowId());
//        if (label==null){
//            result.put("resultCode",CODE_FAIL);
//            result.put("resultMsg","未找到对应标签");
//            return result;
//        }
        labelValueMapper.deleteByLabelId(label.getInjectionLabelId());
        labelMapper.deleteByPrimaryKey(label.getInjectionLabelId());
        addLabel(record);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","更新成功");
        return result;
    }

    /**
     * 删除标签
     * @param record
     * @return
     */
    private Map<String,Object> deleteLabel(RecordModel record){
        Map<String,Object> result = new HashMap<>();
        LabModel tagModel = record.getLabel();
        Label label = labelMapper.selectByTagRowId(tagModel.getLabRowId());
        if (label==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","未找到对应标签");
            return result;
        }
//        List<LabelValue> valueList = labelValueMapper.selectByLabelId(label.getInjectionLabelId());
        labelValueMapper.deleteByLabelId(label.getInjectionLabelId());
        labelMapper.deleteByPrimaryKey(label.getInjectionLabelId());
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","删除成功");
        return result;
    }


    private Map<String,Object> syncLabelValue(List<LabValueModel> valueModelList, Long labelId) {
        Map<String,Object> result = new HashMap<>();
        List<LabelValue> valueList = new ArrayList<>();
        List<String> stringList = new ArrayList<>();
        labelValueMapper.deleteByLabelId(labelId);
        for (LabValueModel info : valueModelList){
            LabelValue value = BeanUtil.create(info,new LabelValue());
            value.setInjectionLabelId(labelId);
            value.setValueDesc(info.getValueName());
            value.setValueName(info.getLabValue());
            value.setLabelValue(info.getLabValue());
            value.setCreateDate(new Date());
            value.setStatusCd("1000");
            value.setUpdateDate(new Date());
            valueList.add(value);
            stringList.add(value.getLabelValue());
            labelValueMapper.insert(value);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","标签值同步成功");
        result.put("valueString",ChannelUtil.StringList2String(stringList));
        return result;
    }

    /**
     * 初始化
     * @return
     */
    @Override
    public Map<String,Object> initialization() {
        Map<String, Object> result = new HashMap<>();

        //标签数据初始化
        List<Label> labels = labelMapper.selectAll();
        for (Label label : labels) {
            labelMapper.deleteByPrimaryKey(label.getInjectionLabelId());
            Label labelModel = BeanUtil.create(label, new Label());

            //label.setInjectionLabelDesc();
            if(label.getConditionType().equals("4")){
                labelModel.setOperator("2000,3000,1000,4000,6000,5000,7000,7200");
                labelModel.setLabelValueType("1000");
            }else{
                labelModel.setOperator("7000");
                labelModel.setLabelValueType("2000");
            }
            if (labelModel.getLabObject().equals("客户级")){
                labelModel.setLabelType("1000");
            }else if (labelModel.getLabObject().equals("用户级")) {
                labelModel.setLabelType("2000");
            }else if (labelModel.getLabObject().equals("销售品级")) {
                labelModel.setLabelType("3000");
            }else if (labelModel.getLabObject().equals("区域级")) {
                labelModel.setLabelType("4000");
            }
            labelModel.setLabelDataType("1000");
            labelModel.setScope(1);
            labelModel.setIsShared(0);
            labelModel.setSystemInfoId(0L);
            labelModel.setCatalogId(label.getLabObjectCode() + label.getLabLevel1() + label.getLabLevel2() + label.getLabLevel3());

//            //todo 暂无值类型
//            if (record.getLabelValueList()!=null && !record.getLabelValueList().isEmpty()){
//                labelModel.setLabelValueType("2000");
//            }else {
//                labelModel.setLabelValueType("1000");
//            }
            //label.setLabelDataType(ChannelUtil.getDataType(tagModel.getSourceTableColumnType()));

            labelModel.setStatusCd(STATUSCD_EFFECTIVE);
            labelModel.setCreateStaff(UserUtil.loginId());
            labelModel.setCreateDate(new Date());

            labelMapper.insert(labelModel);
        }

        //标签目录初始化
//        List<Label> labels = labelMapper.selectAll();
        for (Label label : labels){
            //标签目录插入
            LabelCatalog labelCatalog =new LabelCatalog();
            labelCatalog.setStatusCd(STATUSCD_EFFECTIVE);
            labelCatalog.setCreateStaff(UserUtil.loginId());
            labelCatalog.setCreateDate(new Date());
            if(labelCatalogMapper.findByCodeAndLevel(label.getLabObjectCode(), Long.valueOf(1)) == null) {
                labelCatalog.setCatalogCode(label.getLabObjectCode());
                labelCatalog.setCatalogName(label.getLabObject());
                labelCatalog.setLevelId(Long.valueOf(1));
                labelCatalog.setParentId("0");
                labelCatalogMapper.insert(labelCatalog);
            }
            if(labelCatalogMapper.findByCodeAndLevel((label.getLabObjectCode() + label.getLabLevel1()), Long.valueOf(2)) == null) {
                labelCatalog.setCatalogCode(label.getLabObjectCode() + label.getLabLevel1());
                labelCatalog.setCatalogName(label.getLabLevel1Name());
                labelCatalog.setLevelId(Long.valueOf(2));
                labelCatalog.setParentId(label.getLabObjectCode());
                labelCatalogMapper.insert(labelCatalog);
            }
            if(labelCatalogMapper.findByCodeAndLevel((label.getLabObjectCode() + label.getLabLevel1() + label.getLabLevel2()), Long.valueOf(3)) == null) {
                labelCatalog.setCatalogCode(label.getLabObjectCode() + label.getLabLevel1() + label.getLabLevel2());
                labelCatalog.setCatalogName(label.getLabLevel2Name());
                labelCatalog.setLevelId(Long.valueOf(3));
                labelCatalog.setParentId(label.getLabObjectCode() + label.getLabLevel1());
                labelCatalogMapper.insert(labelCatalog);
            }
            if(labelCatalogMapper.findByCodeAndLevel((label.getLabObjectCode() + label.getLabLevel1() + label.getLabLevel2() + label.getLabLevel3()), Long.valueOf(4)) == null) {
                labelCatalog.setCatalogCode(label.getLabObjectCode() + label.getLabLevel1() + label.getLabLevel2() + label.getLabLevel3());
                labelCatalog.setCatalogName(label.getLabLevel3Name());
                labelCatalog.setLevelId(Long.valueOf(4));
                labelCatalog.setParentId(label.getLabObjectCode() + label.getLabLevel1() + label.getLabLevel2());
                labelCatalogMapper.insert(labelCatalog);
            }
            if(labelCatalogMapper.findByCodeAndLevel((label.getLabObjectCode() + label.getLabLevel1() + label.getLabLevel2() + label.getLabLevel3() + label.getLabLevel4()), Long.valueOf(5)) == null) {
                labelCatalog.setCatalogCode(label.getLabObjectCode() + label.getLabLevel1() + label.getLabLevel2() + label.getLabLevel3() + label.getLabLevel4());
                labelCatalog.setCatalogName(label.getLabLevel4Name());
                labelCatalog.setLevelId(Long.valueOf(5));
                labelCatalog.setParentId(label.getLabObjectCode() + label.getLabLevel1() + label.getLabLevel2() + label.getLabLevel3());
                labelCatalogMapper.insert(labelCatalog);
            }

        }

        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","新增成功");
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
