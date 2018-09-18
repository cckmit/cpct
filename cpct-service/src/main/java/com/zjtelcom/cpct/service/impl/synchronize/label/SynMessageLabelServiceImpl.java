package com.zjtelcom.cpct.service.impl.synchronize.label;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.MessageLabelMapper;
import com.zjtelcom.cpct.domain.channel.MessageLabel;
import com.zjtelcom.cpct.enums.SynchronizeType;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import com.zjtelcom.cpct.service.synchronize.label.SynMessageLabelService;
import com.zjtelcom.cpct_prd.dao.label.MessageLabelPrdMapper;
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
 * @Description:
 */
@Service
@Transactional
public class SynMessageLabelServiceImpl implements SynMessageLabelService{

    @Autowired
    private SynchronizeRecordService synchronizeRecordService;
    @Autowired
    private MessageLabelPrdMapper messageLabelPrdMapper;
    @Autowired
    private MessageLabelMapper messageLabelMapper;

    //同步表名
    private static final String tableName="message_label";

    /**
     * 同步单个试运算展示列配置
     * @param labelId    事件id
     * @param roleName   操作人身份
     * @return
     */
    @Override
    public Map<String, Object> synchronizeSingleMessageLabel(Long labelId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        //查询源数据库
        MessageLabel messageLabel = messageLabelMapper.selectByPrimaryKey(labelId);
        if(messageLabel==null){
            throw new SystemException("对应试运算展示列信息不存在");
        }
        //同步时查看是新增还是更新
        MessageLabel messageLabel1 = messageLabelPrdMapper.selectByPrimaryKey(labelId);
        if(messageLabel1==null){
            messageLabelPrdMapper.insert(messageLabel);
            synchronizeRecordService.addRecord(roleName,tableName,labelId, SynchronizeType.add.getType());
        }else{
            messageLabelPrdMapper.updateByPrimaryKey(messageLabel);
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
        //先查出准生产的所有事件
        List<MessageLabel> prdList = messageLabelMapper.selectAll();
        //查出生产的所有事件
        List<MessageLabel> realList = messageLabelPrdMapper.selectAll();
        //三个集合分别表示需要 新增的   修改的    删除的
        List<MessageLabel> addList=new ArrayList<MessageLabel>();
        List<MessageLabel> updateList=new ArrayList<MessageLabel>();
        List<MessageLabel> deleteList=new ArrayList<MessageLabel>();
        for(MessageLabel c:prdList){
            for (int i = 0; i <realList.size() ; i++) {
                if(c.getMessageLabelId()-realList.get(i).getMessageLabelId()==0){
                    //需要修改的
                    updateList.add(c);
                    break;
                }else if(i==realList.size()-1){
                    //需要新增的  准生产存在，生产不存在
                    addList.add(c);
                }
            }
        }
        //查出需要删除的事件
        for(MessageLabel c:realList){
            for (int i = 0; i <prdList.size() ; i++) {
                if(c.getMessageLabelId()-prdList.get(i).getMessageLabelId()==0){
                    break;
                }else if (i==prdList.size()-1){
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }
        //开始新增
        for(MessageLabel c:addList){
            messageLabelPrdMapper.insert(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getMessageLabelId(), SynchronizeType.add.getType());
        }
        //开始修改
        for(MessageLabel c:updateList){
            messageLabelPrdMapper.updateByPrimaryKey(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getMessageLabelId(), SynchronizeType.update.getType());
        }
        //开始删除
        for(MessageLabel c:deleteList){
            messageLabelPrdMapper.deleteByPrimaryKey(c.getMessageLabelId());
            synchronizeRecordService.addRecord(roleName,tableName,c.getMessageLabelId(), SynchronizeType.delete.getType());
        }

        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);

        return maps;
    }


}
