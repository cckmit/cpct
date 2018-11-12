package com.zjtelcom.cpct.service.impl.synchronize.label;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.InjectionLabelGrpMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelGrpMbrMapper;
import com.zjtelcom.cpct.domain.channel.LabelGrp;
import com.zjtelcom.cpct.domain.channel.LabelGrpMbr;
import com.zjtelcom.cpct.domain.channel.MessageLabel;
import com.zjtelcom.cpct.enums.SynchronizeType;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import com.zjtelcom.cpct.service.synchronize.label.SynLabelGrpService;
import com.zjtelcom.cpct_prd.dao.label.InjectionLabelGrpMbrPrdMapper;
import com.zjtelcom.cpct_prd.dao.label.InjectionLabelGrpPrdMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/9/28
 * @Description:标签组同步  injection_label_grp关联injection_label_grp_mbr 值
 */
@Service
public class SynLabelGrpServiceImpl implements SynLabelGrpService {

    @Autowired
    private InjectionLabelGrpMapper injectionLabelGrpMapper;
    @Autowired
    private InjectionLabelGrpPrdMapper injectionLabelGrpPrdMapper;
    @Autowired
    private SynchronizeRecordService synchronizeRecordService;
    @Autowired
    private InjectionLabelGrpMbrMapper injectionLabelGrpMbrMapper;
    @Autowired
    private InjectionLabelGrpMbrPrdMapper injectionLabelGrpMbrPrdMapper;

    //同步表名
    private static final String tableName="injection_label_grp";


    @Override
    public Map<String, Object> synchronizeSingleLabel(Long labelGrpId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        LabelGrp labelGrp = injectionLabelGrpMapper.selectByPrimaryKey(labelGrpId);
        if(labelGrp==null){
            throw new SystemException("对应标签组信息不存在!");
        }
        List<LabelGrpMbr> listByGrpId = injectionLabelGrpMbrMapper.findListByGrpId(labelGrp.getGrpId());
        LabelGrp labelGrp1 = injectionLabelGrpPrdMapper.selectByPrimaryKey(labelGrpId);
        if(null==labelGrp1){
            injectionLabelGrpPrdMapper.insert(labelGrp);
            //增加对应的关联
            if(!listByGrpId.isEmpty()){
                for (LabelGrpMbr grpMbr:listByGrpId){
                    injectionLabelGrpMbrPrdMapper.insert(grpMbr);
                }
            }
            synchronizeRecordService.addRecord(roleName,tableName,labelGrpId, SynchronizeType.add.getType());
        }else{
            injectionLabelGrpPrdMapper.updateByPrimaryKey(labelGrp);
            if(!listByGrpId.isEmpty()){
                for (LabelGrpMbr grpMbr:listByGrpId){
                    injectionLabelGrpMbrPrdMapper.updateByPrimaryKey(grpMbr);
                }
            }
            synchronizeRecordService.addRecord(roleName,tableName,labelGrpId, SynchronizeType.update.getType());
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", org.apache.commons.lang.StringUtils.EMPTY);
        return maps;
    }


    /**
     * 全量同步标签组
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> synchronizeBatchLabel(String roleName) {
        Map<String,Object> maps = new HashMap<>();
        List<LabelGrp> prdList = injectionLabelGrpMapper.selectAll();
        List<LabelGrp> realList = injectionLabelGrpPrdMapper.selectAll();
        //三个集合分别表示需要 新增的   修改的    删除的
        List<LabelGrp> addList=new ArrayList<LabelGrp>();
        List<LabelGrp> updateList=new ArrayList<LabelGrp>();
        List<LabelGrp> deleteList=new ArrayList<LabelGrp>();
        for(LabelGrp c:prdList){
            for (int i = 0; i <realList.size() ; i++) {
                if(c.getGrpId()-realList.get(i).getGrpId()==0){
                    //需要修改的
                    updateList.add(c);
                    break;
                }else if(i==realList.size()-1){
                    //需要新增的  准生产存在，生产不存在
                    addList.add(c);
                }
            }
        }
        for(LabelGrp c:realList){
            for (int i = 0; i <prdList.size() ; i++) {
                if(c.getGrpId()-prdList.get(i).getGrpId()==0){
                    break;
                }else if (i==prdList.size()-1){
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }
        //开始新增
        for(LabelGrp c:addList){
            injectionLabelGrpPrdMapper.insert(c);
            List<LabelGrpMbr> listByGrpId = injectionLabelGrpMbrMapper.findListByGrpId(c.getGrpId());
            if(!listByGrpId.isEmpty()){
                for (LabelGrpMbr grpMbr:listByGrpId){
                    injectionLabelGrpMbrPrdMapper.insert(grpMbr);
                }
            }
            synchronizeRecordService.addRecord(roleName,tableName,c.getGrpId(), SynchronizeType.add.getType());
        }
        //开始修改
        for(LabelGrp c:updateList){
            injectionLabelGrpPrdMapper.updateByPrimaryKey(c);
            List<LabelGrpMbr> listByGrpId = injectionLabelGrpMbrMapper.findListByGrpId(c.getGrpId());
            if(!listByGrpId.isEmpty()){
                for (LabelGrpMbr grpMbr:listByGrpId){
                    injectionLabelGrpMbrPrdMapper.updateByPrimaryKey(grpMbr);
                }
            }
            synchronizeRecordService.addRecord(roleName,tableName,c.getGrpId(), SynchronizeType.update.getType());
        }
        //开始删除
        for(LabelGrp c:deleteList){
            injectionLabelGrpPrdMapper.deleteByPrimaryKey(c.getGrpId());
            List<LabelGrpMbr> listByGrpId = injectionLabelGrpMbrMapper.findListByGrpId(c.getGrpId());
            if(!listByGrpId.isEmpty()){
                for (LabelGrpMbr grpMbr:listByGrpId){
                    injectionLabelGrpMbrPrdMapper.deleteByPrimaryKey(grpMbr.getGrpMbrId());
                }
            }
            synchronizeRecordService.addRecord(roleName,tableName,c.getGrpId(), SynchronizeType.delete.getType());
        }

        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);

        return maps;
    }

    @Override
    public Map<String, Object> deleteSingleLabel(Long labelGrpId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        injectionLabelGrpPrdMapper.deleteByPrimaryKey(labelGrpId);
        List<LabelGrpMbr> listByGrpId = injectionLabelGrpMbrPrdMapper.findListByGrpId(labelGrpId);
        for (LabelGrpMbr labelGrpMbr:listByGrpId){
            injectionLabelGrpMbrPrdMapper.deleteByPrimaryKey(labelGrpMbr.getGrpMbrId());
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", org.apache.commons.lang.StringUtils.EMPTY);
        return maps;
    }
}
