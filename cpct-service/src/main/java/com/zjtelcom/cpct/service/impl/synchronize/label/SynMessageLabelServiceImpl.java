package com.zjtelcom.cpct.service.impl.synchronize.label;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.DisplayColumnLabelMapper;
import com.zjtelcom.cpct.dao.channel.DisplayColumnMapper;
import com.zjtelcom.cpct.domain.channel.DisplayColumn;
import com.zjtelcom.cpct.domain.channel.DisplayColumnLabel;
import com.zjtelcom.cpct.enums.SynchronizeType;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import com.zjtelcom.cpct.service.synchronize.label.SynMessageLabelService;
import com.zjtelcom.cpct_prd.dao.label.DisplayColumnLabelPrdMapper;
import com.zjtelcom.cpct_prd.dao.label.DisplayColumnPrdMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/9/17
 * @Description: 标签列同步  同时需要同步展示列关联标签表
 */
@Service
@Transactional
public class SynMessageLabelServiceImpl implements SynMessageLabelService{

    @Autowired
    private SynchronizeRecordService synchronizeRecordService;
    @Autowired
    private DisplayColumnLabelMapper displayColumnLabelMapper;
    @Autowired
    private DisplayColumnLabelPrdMapper displayColumnLabelPrdMapper;
    @Autowired
    private DisplayColumnMapper displayColumnMapper;
    @Autowired
    private DisplayColumnPrdMapper displayColumnPrdMapper;



    //同步表名
    private static final String tableName="message_label";

    /**
     * 同步单个试运算展示列配置
     * @param labelId    标签id
     * @param roleName   操作人身份
     * @return
     */
    @Override
    public Map<String, Object> synchronizeSingleMessageLabel(Long labelId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        //查询源数据库
        DisplayColumn displayColumn = displayColumnMapper.selectByPrimaryKey(labelId);
        if(displayColumn==null){
            throw new SystemException("对应试运算展示列信息不存在");
        }
        //查出其关联的展示列关联标签表信息
        List<DisplayColumnLabel> listByDisplayId = displayColumnLabelMapper.findListByDisplayId(displayColumn.getDisplayColumnId());

        //同步时查看是新增还是更新
        DisplayColumn displayColumn1 = displayColumnPrdMapper.selectByPrimaryKey(labelId);
        if(displayColumn1==null){
            displayColumnPrdMapper.insert(displayColumn);
            if(!listByDisplayId.isEmpty()){
                   for (DisplayColumnLabel d:listByDisplayId){
                       displayColumnLabelPrdMapper.insert(d);
                   }
            }
            synchronizeRecordService.addRecord(roleName,tableName,labelId, SynchronizeType.add.getType());
        }else{
            displayColumnPrdMapper.updateByPrimaryKey(displayColumn);
            if(!listByDisplayId.isEmpty()){
                List<DisplayColumnLabel>  displayColumnLabels = displayColumnLabelPrdMapper.findListByDisplayId(displayColumn.getDisplayColumnId());
                List<Long> list = new ArrayList<>();
                for(DisplayColumnLabel displayColumnLabel: displayColumnLabels) {
                    list.add(displayColumnLabel.getInjectionLabelId());
                }
                for (DisplayColumnLabel d:listByDisplayId){
                    if(!list.contains(d.getInjectionLabelId())) {
                        displayColumnLabelPrdMapper.insert(d);
                    }
                }
            }
            synchronizeRecordService.addRecord(roleName,tableName,labelId, SynchronizeType.update.getType());
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 批量同步试运算展示列配置
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> synchronizeBatchMessageLabel(String roleName) {
        Map<String,Object> maps = new HashMap<>();
        List<DisplayColumn> prdList = displayColumnMapper.selectAll();
        List<DisplayColumn> realList = displayColumnPrdMapper.selectAll();
        //三个集合分别表示需要 新增的   修改的    删除的
        List<DisplayColumn> addList=new ArrayList<DisplayColumn>();
        List<DisplayColumn> updateList=new ArrayList<DisplayColumn>();
        List<DisplayColumn> deleteList=new ArrayList<DisplayColumn>();
        for(DisplayColumn c:prdList){
            for (int i = 0; i <realList.size() ; i++) {
                if(c.getDisplayColumnId()-realList.get(i).getDisplayColumnId()==0){
                    //需要修改的
                    updateList.add(c);
                    break;
                }else if(i==realList.size()-1){
                    //需要新增的  准生产存在，生产不存在
                    addList.add(c);
                }
            }
        }
        for(DisplayColumn c:realList){
            for (int i = 0; i <prdList.size() ; i++) {
                if(c.getDisplayColumnId()-prdList.get(i).getDisplayColumnId()==0){
                    break;
                }else if (i==prdList.size()-1){
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }
        //开始新增
        for(DisplayColumn c:addList){
            displayColumnPrdMapper.insert(c);
            List<DisplayColumnLabel> listByDisplayId = displayColumnLabelMapper.findListByDisplayId(c.getDisplayColumnId());
            if(!listByDisplayId.isEmpty()){
                for (DisplayColumnLabel d:listByDisplayId){
                    displayColumnLabelPrdMapper.insert(d);
                }
            }
            synchronizeRecordService.addRecord(roleName,tableName,c.getDisplayColumnId(), SynchronizeType.add.getType());
        }
        //开始修改
        for(DisplayColumn c:updateList){
            displayColumnPrdMapper.updateByPrimaryKey(c);
            List<DisplayColumnLabel> listByDisplayId = displayColumnLabelMapper.findListByDisplayId(c.getDisplayColumnId());
            if(!listByDisplayId.isEmpty()){
                for (DisplayColumnLabel d:listByDisplayId){
                    displayColumnLabelPrdMapper.updateByPrimaryKey(d);
                }
            }
            synchronizeRecordService.addRecord(roleName,tableName,c.getDisplayColumnId(), SynchronizeType.update.getType());
        }
        //开始删除
        for(DisplayColumn c:deleteList){
            displayColumnPrdMapper.deleteByPrimaryKey(c.getDisplayColumnId());
            displayColumnLabelPrdMapper.deleteByDisplayId(c.getDisplayColumnId());
            synchronizeRecordService.addRecord(roleName,tableName,c.getDisplayColumnId(), SynchronizeType.delete.getType());
        }

        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);

        return maps;
    }

    @Override
    public Map<String, Object> deleteSingleMessageLabel(Long messageLabelId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        displayColumnPrdMapper.deleteByPrimaryKey(messageLabelId);
        displayColumnLabelPrdMapper.deleteByDisplayId(messageLabelId);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", org.apache.commons.lang.StringUtils.EMPTY);
        return maps;
    }

    @Override
    public Map<String, Object> deleteSingleDisplayLabel(Long displayId, Long labelId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        DisplayColumnLabel displayColumnLabel = displayColumnLabelPrdMapper.findByDisplayIdAndLabelId(displayId, labelId);
        if(displayColumnLabel != null) {
            displayColumnLabelPrdMapper.deleteByPrimaryKey(displayColumnLabel.getDisplayColumnLabelId());
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", org.apache.commons.lang.StringUtils.EMPTY);
        return maps;
    }


}
