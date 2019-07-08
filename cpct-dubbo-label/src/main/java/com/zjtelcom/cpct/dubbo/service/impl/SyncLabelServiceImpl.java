package com.zjtelcom.cpct.dubbo.service.impl;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.InjectionLabelCatalogMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelValueMapper;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.LabelCatalog;
import com.zjtelcom.cpct.domain.channel.LabelValue;
import com.zjtelcom.cpct.dto.channel.*;
import com.zjtelcom.cpct.dubbo.model.LabValueModel;
import com.zjtelcom.cpct.dubbo.model.RecordModel;
import com.zjtelcom.cpct.dubbo.service.SyncLabelService;
import com.zjtelcom.cpct.enums.SynchronizeType;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import com.zjtelcom.cpct.util.*;
import com.zjtelcom.cpct_prd.dao.label.InjectionLabelPrdMapper;
import com.zjtelcom.cpct_prd.dao.label.InjectionLabelValuePrdMapper;
import org.apache.commons.lang3.StringUtils;
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

    @Autowired
    private InjectionLabelMapper injectionLabelMapper;
    @Autowired(required = false)
    private InjectionLabelPrdMapper injectionLabelPrdMapper;
    @Autowired
    private SynchronizeRecordService synchronizeRecordService;
    @Autowired
    private InjectionLabelValueMapper injectionLabelValueMapper;
    @Autowired(required = false)
    private InjectionLabelValuePrdMapper injectionLabelValuePrdMapper;
    //同步表名
    private static final String tableName="injection_label";

    /**
     * 单个标签信息同步
     * @param labelId
     * @param roleName
     * @return
     */
    public Map<String, Object> synchronizeSingleLabel(Long labelId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        Label label = injectionLabelMapper.selectByPrimaryKey(labelId);
        if(null==label){
            throw new SystemException("对应标签信息不存在!");
        }
        List<LabelValue> labelValues = injectionLabelValueMapper.selectByLabelId(label.getInjectionLabelId());

        Label label1 = injectionLabelPrdMapper.selectByPrimaryKey(labelId);
        if(null==label1){
            injectionLabelPrdMapper.insert(label);
            if(!labelValues.isEmpty()){
                injectionLabelValuePrdMapper.insertBatch(labelValues);
//                for (LabelValue labelValue:labelValues){
//                    injectionLabelValuePrdMapper.insert(labelValue);
//                }
            }
            synchronizeRecordService.addRecord(roleName,tableName,labelId, SynchronizeType.add.getType());
        }else{
            injectionLabelPrdMapper.updateByPrimaryKey(label);
            diffLabelValue(labelValues,label1);
            synchronizeRecordService.addRecord(roleName,tableName,labelId, SynchronizeType.update.getType());
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 比较标签对应的标签值是否对应
     * !!!准生产代码 每次修改标签都会把他对应的标签值都删除 再新增数据 所以准生产所有的修改我们都对生产环境执行先全删除再新增
     * @param prdList  准生产标签对应的标签值
     * @param label1   生产环境的标签
     */
    public void diffLabelValue(List<LabelValue> prdList,Label label1){
        //1.1首先判断准生产 或生产是否存在某一方数据修改为0的情况
        List<LabelValue> realList = injectionLabelValuePrdMapper.selectByLabelId(label1.getInjectionLabelId());
        if (prdList.isEmpty() || realList.isEmpty()) {
            if (prdList.isEmpty() && !realList.isEmpty()) {
                //清除生产环境数据
                for (int i = 0; i < realList.size(); i++) {
                    injectionLabelValuePrdMapper.deleteByPrimaryKey(realList.get(i).getLabelValueId());
                }
            } else if (!prdList.isEmpty() && realList.isEmpty()) {
                //全量新增准生产的数据到生产环境
                for (int i = 0; i < prdList.size(); i++) {
                    injectionLabelValuePrdMapper.insert(prdList.get(i));
                }
            }
            return;
        }
        //1.2先删除生产环境的对应标签值
        for(LabelValue c:realList){
            injectionLabelValuePrdMapper.deleteByPrimaryKey(c.getLabelValueId());
        }
        //1.3新增标签值到生产环境
        injectionLabelValuePrdMapper.insertBatch(prdList);

    }

    public Map<String, Object> deleteSingleLabel(Long labelId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        injectionLabelPrdMapper.deleteByPrimaryKey(labelId);
        injectionLabelValuePrdMapper.deleteByLabelId(labelId);
        //相关的标签值
//        List<LabelValue> labelValues = injectionLabelValueMapper.selectByLabelId(labelId);
//        for (LabelValue labelValue:labelValues){
//            injectionLabelValueMapper.deleteByPrimaryKey(labelValue.getLabelValueId());
//        }
        synchronizeRecordService.addRecord(roleName,tableName,labelId, SynchronizeType.delete.getType());
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", org.apache.commons.lang.StringUtils.EMPTY);
        return maps;
    }

    /**
     * 标签同步对外接口
     * @param record
     * @return
     */
    @Override
    @Transactional
    public Map<String, Object> syncLabelInfo(Map<String,Object> record) {
        Map<String,Object> result = new HashMap<>();
        System.out.println("*******************入参："+JSON.toJSONString(record));
        Map<String,Object> labModel = (Map<String, Object>) record.get("labModel");
        List<Map<String,Object>> valueList = new ArrayList<>();
        if (record.get("labelValueList")!=null){
            valueList = (List<Map<String,Object>>) record.get("labelValueList");
        }
        RecordModel recordModel = ChannelUtil.mapToEntity(labModel,RecordModel.class);
        recordModel.setLabelValueList(valueList);
        System.out.println("*******************实体转换："+JSON.toJSONString(recordModel));
        try {
            switch (recordModel.getLabState()){
                case "3":
                    result = addLabel(recordModel);
//                    if (result.get("resultCode").equals(CODE_SUCCESS)){
//                        new Thread(){
//                            public void run(){
//                                try {
//                                    synchronizeSingleLabel(record.getLabel().getLabRowId(),"");
//                                }catch (Exception e){
//                                    e.printStackTrace();
//                                }
//                            }
//                        }.start();
//                    }
                    break;
                case "5":
                    result = deleteLabel(recordModel);
//                    if (result.get("resultCode").equals(CODE_SUCCESS)){
//                        new Thread(){
//                            public void run(){
//                                try {
//                                    deleteSingleLabel(record.getLabel().getLabRowId(),"");
//                                }catch (Exception e){
//                                    e.printStackTrace();
//                                }
//                            }
//                        }.start();
//                    }
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


    private boolean  labelDataType(RecordModel labModel,Label label){
        boolean x = true;
        if (labModel.getLabDataType()==null || labModel.getLabDataType().equals("")){
            x = false;
            return x;
        }
        String type = labModel.getLabDataType();
        if (type.toUpperCase().contains("VARCHAR")){
            label.setLabelDataType("1200");
        }else
        if (type.toUpperCase().contains("INTEGER")){
            label.setLabelDataType("1300");
        }else if (type.toUpperCase().contains("INT")){
            label.setLabelDataType("1300");
        }else
        if (type.toUpperCase().contains("NUMERIC")){
            label.setLabelDataType("1300");
        }else
        if (type.toUpperCase().contains("DATE")){
            label.setLabelDataType("1100");
        } else
        if (type.toUpperCase().contains("CHAR")){
            label.setLabelDataType("1200");
        }else {
            x = false;
        }
        return x;
    }

    /**
     * 新增标签
     * @param record
     * @return
     */
    private Map<String,Object> addLabel(RecordModel record) {
        logger.info("**********入参**************："+JSON.toJSONString(record));
        Map<String, Object> result = new HashMap<>();
        RecordModel labModel = record;
        Label labelValodate = labelMapper.selectByTagRowId(record.getLabRowId());
        List<LabValueModel> valueModelList = new ArrayList<>();
        final List<Label> labelList = new ArrayList<>();
        if (record.getLabelValueList() != null && !record.getLabelValueList().isEmpty()) {
            for (Map<String,Object> valueMap : record.getLabelValueList()){
                LabValueModel valueModel = ChannelUtil.mapToEntity(valueMap,LabValueModel.class);
                if (valueModel.getValueName()==null || valueModel.getValueName().equals("")){
                    result.put("resultCode", CODE_FAIL);
                    result.put("resultMsg", "请补充标签枚举值信息。");
                    return result;
                }
                valueModelList.add(valueModel);
            }
        }
        Long labelId = null;
        if (labelValodate != null) {
            //标签中文名重复校验
            List<Label> labelCheck = labelMapper.findByParam(record.getLabName());
            for(int i = 0;i<labelCheck.size();i++) {
                if (!labelCheck.get(i).getTagRowId().equals(record.getLabRowId())) {
                    if (labelCheck.get(i).getInjectionLabelName().equals(record.getLabName())) {
                        result.put("resultCode", CODE_FAIL);
                        result.put("resultMsg", "标签中文名和已有标签重复");
                        return result;
                    }
                }
            }
            //标签英文名重复校验
            Label labelEgCheck = labelMapper.selectByLabelCode(record.getLabEngName());
            if (!labelEgCheck.getTagRowId().equals(record.getLabRowId())) {
                result.put("resultCode", CODE_FAIL);
                result.put("resultMsg", "标签英文名和已有标签重复");
                return result;
            }
            //更新标签
            BeanUtil.copy(labModel,labelValodate);
            labelValodate.setSystemInfoId(1L);
            labelValodate.setTagRowId(labModel.getLabRowId());//标签id
            labelValodate.setInjectionLabelName(labModel.getLabName());//标签名称
            labelValodate.setInjectionLabelCode(labModel.getLabEngName());//英文名称
            //label.setInjectionLabelDesc();
            labelValodate.setLabTagCode(labModel.getLabCode());//标签编码
            labelValodate.setInjectionLabelDesc(labModel.getLabBusiDesc());
            if ( !labelDataType(labModel,labelValodate)){
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","无法识别标签字段类型");
                return result;
            }

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
            labelId = labelValodate.getInjectionLabelId();
            //redis更新标签库
            redisUtils.set("LABEL_LIB_"+labelValodate.getInjectionLabelId(),labelValodate);
            syncLabelValue(valueModelList,labelValodate.getInjectionLabelId());
            labelList.add(labelValodate);
        }else {
            //标签中文名重复校验
            List<Label> labelCheck = labelMapper.findByParam(record.getLabName());
            for(int i = 0;i<labelCheck.size();i++) {
                if(labelCheck.get(i).getInjectionLabelName().equals(record.getLabName())) {
                    result.put("resultCode",CODE_FAIL);
                    result.put("resultMsg","标签中文名和已有标签重复");
                    return result;
                }
            }
            //标签英文名重复校验
            Label labelEgCheck = labelMapper.selectByLabelCode(record.getLabEngName());
            if(labelEgCheck != null) {
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","标签英文名和已有标签重复");
                return result;
            }
            //新增标签
            Label label = BeanUtil.create(labModel, new Label());
            label.setSystemInfoId(1L);
            label.setTagRowId(labModel.getLabRowId());//标签id
            label.setInjectionLabelName(labModel.getLabName());//标签名称
            label.setInjectionLabelCode(labModel.getLabEngName());//英文名称
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
            if (!labelDataType(labModel,label)){
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","无法识别标签字段类型");
                return result;
            }
            label.setLabelType("1000");
            if (record.getLabelValueList()!=null && !record.getLabelValueList().isEmpty()){
                label.setLabelValueType("2000");
            }else {
                label.setLabelValueType("1000");
            }
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
            labelId = label.getInjectionLabelId();
            //redis更新标签库
            redisUtils.set("LABEL_LIB_"+label.getInjectionLabelId(),label);
            syncLabelValue(valueModelList,label.getInjectionLabelId());
            labelList.add(label);
        }
        new Thread(){
            public void run(){
                try {
                    initLabelCatalog(labelList);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","新增成功");
        result.put("labelId",labelId);
        return result;
    }


    /**
     * 删除标签
     * @param record
     * @return
     */
    private Map<String,Object> deleteLabel(RecordModel record){
        Map<String,Object> result = new HashMap<>();
        RecordModel tagModel = record;
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
            value.setValueName(info.getValueName());
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
//        for (Label label : labels) {
//            labelMapper.deleteByPrimaryKey(label.getInjectionLabelId());
//            Label labelModel = BeanUtil.create(label, new Label());
//
//            //label.setInjectionLabelDesc();
//            if(label.getConditionType().equals("4")){
//                labelModel.setOperator("2000,3000,1000,4000,6000,5000,7000,7200");
//                labelModel.setLabelValueType("1000");
//            }else{
//                labelModel.setOperator("7000");
//                labelModel.setLabelValueType("2000");
//            }
//            if (labelModel.getLabObject().equals("客户级")){
//                labelModel.setLabelType("1000");
//            }else if (labelModel.getLabObject().equals("用户级")) {
//                labelModel.setLabelType("2000");
//            }else if (labelModel.getLabObject().equals("销售品级")) {
//                labelModel.setLabelType("3000");
//            }else if (labelModel.getLabObject().equals("区域级")) {
//                labelModel.setLabelType("4000");
//            }
//            labelModel.setLabelDataType("1000");
//            labelModel.setScope(1);
//            labelModel.setIsShared(0);
//            labelModel.setSystemInfoId(0L);
//            labelModel.setCatalogId(label.getLabObjectCode() + label.getLabLevel1() + label.getLabLevel2() + label.getLabLevel3());
//
////            //todo 暂无值类型
////            if (record.getLabelValueList()!=null && !record.getLabelValueList().isEmpty()){
////                labelModel.setLabelValueType("2000");
////            }else {
////                labelModel.setLabelValueType("1000");
////            }
//            //label.setLabelDataType(ChannelUtil.getDataType(tagModel.getSourceTableColumnType()));
//
//            labelModel.setStatusCd(STATUSCD_EFFECTIVE);
//            labelModel.setCreateStaff(UserUtil.loginId());
//            labelModel.setCreateDate(new Date());
//
//            labelMapper.insert(labelModel);
//        }

        //标签目录初始化
//        List<Label> labels = labelMapper.selectAll();
        initLabelCatalog(labels);

        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","新增成功");
        return result;
    }

    @Override
    public void initLabelCatalog() {
        List<Label> labels = injectionLabelMapper.selectByScope(1L,null);
        for (Label label : labels){
            //标签目录插入
            LabelCatalog labelCatalog =new LabelCatalog();
            labelCatalog.setStatusCd(STATUSCD_EFFECTIVE);
            labelCatalog.setCreateStaff(UserUtil.loginId());
            labelCatalog.setCreateDate(new Date());
            if(labelCatalogMapper.findByCodeAndLevel(label.getLabObjectCode(),1L) == null) {
                labelCatalog.setCatalogCode(label.getLabObjectCode());
                labelCatalog.setCatalogName(label.getLabObject());
                labelCatalog.setLevelId(1L);
                labelCatalog.setParentId("0");
                labelCatalog.setRemark("1");
                labelCatalogMapper.insert(labelCatalog);
            }
//            if(labelCatalogMapper.findByCodeAndLevel((label.getLabObjectCode() + label.getLabLevel1()),2L) == null) {
//                labelCatalog.setCatalogCode(label.getLabObjectCode() + label.getLabLevel1());
            if(labelCatalogMapper.findByCodeAndLevel((label.getLabObjectCode() + label.getOriginalLabLevel2Code()),2L) == null) {
                labelCatalog.setCatalogCode(label.getLabObjectCode() + label.getOriginalLabLevel2Code());
                labelCatalog.setCatalogName(label.getOriginalLabLevel2Name());
                labelCatalog.setLevelId(2L);
                labelCatalog.setParentId(label.getLabObjectCode());
                labelCatalog.setRemark("1");
                labelCatalogMapper.insert(labelCatalog);
            }
            if(labelCatalogMapper.findByCodeAndLevel((label.getLabObjectCode() + label.getOriginalLabLevel2Code() + label.getOriginalLabLevel3Code()), 3L) == null) {
                labelCatalog.setCatalogCode(label.getLabObjectCode() + label.getOriginalLabLevel2Code() + label.getOriginalLabLevel3Code());
                labelCatalog.setCatalogName(label.getOriginalLabLevel3Name());
                labelCatalog.setLevelId(3L);
                labelCatalog.setParentId(label.getLabObjectCode() + label.getOriginalLabLevel2Code());
                labelCatalog.setRemark("1");
                labelCatalogMapper.insert(labelCatalog);
            }
        }
    }

    private void initLabelCatalog(List<Label> labelList) {
        List<Label> labels = injectionLabelMapper.selectByScope(1L,null);
        for (Label label : labels){
            //标签目录插入
            LabelCatalog labelCatalog =new LabelCatalog();
            labelCatalog.setStatusCd(STATUSCD_EFFECTIVE);
            labelCatalog.setCreateStaff(UserUtil.loginId());
            labelCatalog.setCreateDate(new Date());
            if(labelCatalogMapper.findByCodeAndLevel(label.getLabObjectCode(),1L) == null) {
                labelCatalog.setCatalogCode(label.getLabObjectCode());
                labelCatalog.setCatalogName(label.getLabObject());
                labelCatalog.setLevelId(1L);
                labelCatalog.setParentId("0");
                labelCatalog.setRemark("0");
                labelCatalogMapper.insert(labelCatalog);
            }
            if(labelCatalogMapper.findByCodeAndLevel((label.getLabObjectCode() + label.getLabLevel1()),2L) == null) {
                labelCatalog.setCatalogCode(label.getLabObjectCode() + label.getLabLevel1());
                labelCatalog.setCatalogName(label.getLabLevel1Name());
                labelCatalog.setLevelId(2L);
                labelCatalog.setParentId(label.getLabObjectCode());
                labelCatalog.setRemark("0");
                labelCatalogMapper.insert(labelCatalog);
            }
            if(labelCatalogMapper.findByCodeAndLevel((label.getLabObjectCode() + label.getLabLevel1() + label.getLabLevel2()), 3L) == null) {
                labelCatalog.setCatalogCode(label.getLabObjectCode() + label.getLabLevel1() + label.getLabLevel2());
                labelCatalog.setCatalogName(label.getLabLevel2Name());
                labelCatalog.setLevelId(3L);
                labelCatalog.setParentId(label.getLabObjectCode() + label.getLabLevel1());
                labelCatalog.setRemark("0");
                labelCatalogMapper.insert(labelCatalog);
            }
            if(labelCatalogMapper.findByCodeAndLevel((label.getLabObjectCode() + label.getLabLevel1() + label.getLabLevel2() + label.getLabLevel3()),4L) == null) {
                labelCatalog.setCatalogCode(label.getLabObjectCode() + label.getLabLevel1() + label.getLabLevel2() + label.getLabLevel3());
                labelCatalog.setCatalogName(label.getLabLevel3Name());
                labelCatalog.setLevelId(4L);
                labelCatalog.setParentId(label.getLabObjectCode() + label.getLabLevel1() + label.getLabLevel2());
                labelCatalog.setRemark("0");
                labelCatalogMapper.insert(labelCatalog);
            }
//            if(labelCatalogMapper.findByCodeAndLevel((label.getLabObjectCode() + label.getLabLevel1() + label.getLabLevel2() + label.getLabLevel3() + label.getLabLevel4()), 5L) == null) {
//                labelCatalog.setCatalogCode(label.getLabObjectCode() + label.getLabLevel1() + label.getLabLevel2() + label.getLabLevel3() + label.getLabLevel4());
//                labelCatalog.setCatalogName(label.getLabLevel4Name());
//                labelCatalog.setLevelId(5L);
//                labelCatalog.setParentId(label.getLabObjectCode() + label.getLabLevel1() + label.getLabLevel2() + label.getLabLevel3());
//                labelCatalogMapper.insert(labelCatalog);
//            }
        }
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
        List<LabelCatalog> allCatalogs = labelCatalogMapper.selectAll("0");
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
