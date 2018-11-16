package com.zjtelcom.cpct.service.impl.synchronize.label;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelValueMapper;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.LabelValue;
import com.zjtelcom.cpct.enums.SynchronizeType;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import com.zjtelcom.cpct.service.synchronize.label.SynLabelService;
import com.zjtelcom.cpct_prd.dao.label.InjectionLabelPrdMapper;
import com.zjtelcom.cpct_prd.dao.label.InjectionLabelValuePrdMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/9/14
 * @Description:标签同步（准生产-生产）   injection_label    injection_label_value
 */
@Service
@Transactional
public class SynLabelServiceImpl implements SynLabelService{

    @Autowired
    private InjectionLabelMapper injectionLabelMapper;
    @Autowired
    private InjectionLabelPrdMapper injectionLabelPrdMapper;
    @Autowired
    private SynchronizeRecordService synchronizeRecordService;
    @Autowired
    private InjectionLabelValueMapper injectionLabelValueMapper;
    @Autowired
    private InjectionLabelValuePrdMapper injectionLabelValuePrdMapper;
    //同步表名
    private static final String tableName="injection_label";

    /**
     * 单个标签信息同步
     * @param labelId
     * @param roleName
     * @return
     */
    @Override
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
            injectionLabelValuePrdMapper.deleteByLabelId(labelId);
            if(!labelValues.isEmpty()){
                injectionLabelValuePrdMapper.insertBatch(labelValues);
//                for (LabelValue labelValue:labelValues){
//                    injectionLabelValuePrdMapper.updateByPrimaryKey(labelValue);
//                }
            }
            synchronizeRecordService.addRecord(roleName,tableName,labelId, SynchronizeType.update.getType());
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }



    /**
     * 批量标签信息同步
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> synchronizeBatchLabel(String roleName) {
        Map<String,Object> maps = new HashMap<>();
        List<Label> prdList = injectionLabelMapper.selectAll();
        List<Label> realList = injectionLabelPrdMapper.selectAll();

        List<Label> addList=new ArrayList<Label>();
        List<Label> updateList=new ArrayList<Label>();
        List<Label> deleteList=new ArrayList<Label>();

        for(Label c:prdList){
            for (int i = 0; i <realList.size() ; i++) {
                if(c.getInjectionLabelId()-realList.get(i).getInjectionLabelId()==0){
                    //需要修改的
                    updateList.add(c);
                    break;
                }else if(i==realList.size()-1){
                    //需要新增的  准生产存在，生产不存在
                    addList.add(c);
                }
            }
        }
        for(Label c:realList){
            for (int i = 0; i <prdList.size() ; i++) {
                if(c.getInjectionLabelId()-prdList.get(i).getInjectionLabelId()==0){
                    break;
                }else if (i==prdList.size()-1){
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }

        //开始新增
        for(Label c:addList){
            injectionLabelPrdMapper.insert(c);
            List<LabelValue> labelValues = injectionLabelValueMapper.selectByLabelId(c.getInjectionLabelId());
            if(!labelValues.isEmpty()){
                injectionLabelValuePrdMapper.insertBatch(labelValues);
            }
            synchronizeRecordService.addRecord(roleName,tableName,c.getInjectionLabelId(), SynchronizeType.add.getType());
        }
        //开始修改
        for(Label c:updateList){
            injectionLabelPrdMapper.updateByPrimaryKey(c);
            List<LabelValue> labelValues = injectionLabelValueMapper.selectByLabelId(c.getInjectionLabelId());
            if(!labelValues.isEmpty()){
                for (LabelValue labelValue:labelValues){
                    injectionLabelValuePrdMapper.updateByPrimaryKey(labelValue);
                 }
            }
            synchronizeRecordService.addRecord(roleName,tableName,c.getInjectionLabelId(), SynchronizeType.update.getType());
        }
        //开始删除
        for(Label c:deleteList){
            injectionLabelPrdMapper.deleteByPrimaryKey(c.getInjectionLabelId());
            List<LabelValue> labelValues = injectionLabelValueMapper.selectByLabelId(c.getInjectionLabelId());
            if(!labelValues.isEmpty()){
                for (LabelValue labelValue:labelValues){
                    injectionLabelValuePrdMapper.deleteByPrimaryKey(labelValue.getLabelValueId());
                }
            }
            synchronizeRecordService.addRecord(roleName,tableName,c.getInjectionLabelId(), SynchronizeType.delete.getType());
        }


        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", org.apache.commons.lang.StringUtils.EMPTY);

        return maps;
    }

    @Override
    public Map<String, Object> deleteSingleLabel(Long labelId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        injectionLabelPrdMapper.deleteByPrimaryKey(labelId);
        injectionLabelValuePrdMapper.deleteByLabelId(labelId);
        //相关的标签值
//        List<LabelValue> labelValues = injectionLabelValueMapper.selectByLabelId(labelId);
//        for (LabelValue labelValue:labelValues){
//            injectionLabelValueMapper.deleteByPrimaryKey(labelValue.getLabelValueId());
//        }

        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", org.apache.commons.lang.StringUtils.EMPTY);
        return maps;
    }


   
}
