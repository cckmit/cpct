package com.zjtelcom.cpct.service.impl.synchronize.filter;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.enums.SynchronizeType;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import com.zjtelcom.cpct.service.synchronize.filter.SynFilterRuleService;
import com.zjtelcom.cpct_prd.dao.filter.FilterRulePrdMapper;
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
public class SynFilterRuleServiceImpl implements SynFilterRuleService {

    @Autowired
    private SynchronizeRecordService synchronizeRecordService;
    @Autowired
    private FilterRulePrdMapper filterRulePrdMapper;
    @Autowired
    private FilterRuleMapper filterRuleMapper;

    //同步表名
    private static final String tableName="filter_rule";


    /**
     * 同步单个过滤规则
     * @param ruleId     过滤规则id
     * @param roleName   操作人身份
     * @return
     */
    @Override
    public Map<String, Object> synchronizeSingleFilterRule(Long ruleId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        //查询源数据库
        FilterRule filterRule = filterRuleMapper.selectByPrimaryKey(ruleId);
        if(filterRule==null){
            throw new SystemException("对应事件不存在");
        }
        //同步时查看是新增还是更新
        FilterRule filterRule1 = filterRulePrdMapper.selectByPrimaryKey(ruleId);
        if(filterRule1==null){
            filterRulePrdMapper.insert(filterRule);
            synchronizeRecordService.addRecord(roleName,tableName,ruleId, SynchronizeType.add.getType());
        }else{
            filterRulePrdMapper.updateByPrimaryKey(filterRule);
            synchronizeRecordService.addRecord(roleName,tableName,ruleId, SynchronizeType.update.getType());
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 批量同步过滤规则
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> synchronizeBatchFilterRule(String roleName) {
        Map<String,Object> maps = new HashMap<>();
        List<FilterRule> prdList = filterRuleMapper.selectAll();
        List<FilterRule> realList = filterRulePrdMapper.selectAll();
        //三个集合分别表示需要 新增的   修改的    删除的
        List<FilterRule> addList=new ArrayList<FilterRule>();
        List<FilterRule> updateList=new ArrayList<FilterRule>();
        List<FilterRule> deleteList=new ArrayList<FilterRule>();
        for(FilterRule c:prdList){
            for (int i = 0; i <realList.size() ; i++) {
                if(c.getRuleId()-realList.get(i).getRuleId()==0){
                    //需要修改的
                    updateList.add(c);
                    break;
                }else if(i==realList.size()-1){
                    //需要新增的  准生产存在，生产不存在
                    addList.add(c);
                }
            }
        }
        for(FilterRule c:realList){
            for (int i = 0; i <prdList.size() ; i++) {
                if(c.getRuleId()-prdList.get(i).getRuleId()==0){
                    break;
                }else if (i==prdList.size()-1){
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }
        //开始新增
        for(FilterRule c:addList){
            filterRulePrdMapper.insert(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getRuleId(), SynchronizeType.add.getType());
        }
        //开始修改
        for(FilterRule c:updateList){
            filterRulePrdMapper.updateByPrimaryKey(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getRuleId(), SynchronizeType.update.getType());
        }
        //开始删除
        for(FilterRule c:deleteList){
            filterRulePrdMapper.deleteByPrimaryKey(c.getRuleId());
            synchronizeRecordService.addRecord(roleName,tableName,c.getRuleId(), SynchronizeType.delete.getType());
        }

        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);

        return maps;
    }

    @Override
    public Map<String, Object> deleteSingleFilterRule(Long ruleId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        filterRulePrdMapper.deleteByPrimaryKey(ruleId);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", org.apache.commons.lang.StringUtils.EMPTY);
        return maps;
    }


}
