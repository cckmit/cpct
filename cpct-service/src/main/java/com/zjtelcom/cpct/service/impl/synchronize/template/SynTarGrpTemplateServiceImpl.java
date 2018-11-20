package com.zjtelcom.cpct.service.impl.synchronize.template;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpMapper;
import com.zjtelcom.cpct.dto.event.ContactEvtItem;
import com.zjtelcom.cpct.dto.grouping.TarGrp;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import com.zjtelcom.cpct.dto.grouping.TarGrpDetail;
import com.zjtelcom.cpct.enums.SynchronizeType;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import com.zjtelcom.cpct.service.synchronize.template.SynTarGrpTemplateService;
import com.zjtelcom.cpct_prd.dao.grouping.TarGrpConditionPrdMapper;
import com.zjtelcom.cpct_prd.dao.grouping.TarGrpPrdMapper;
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
 * @Description: tar_grp   tar_grp_condition  客户分群
 * 记住 客户分群模板已经没有了
 */
@Service
@Transactional
public class SynTarGrpTemplateServiceImpl implements SynTarGrpTemplateService {

    @Autowired
    private SynchronizeRecordService synchronizeRecordService;
    @Autowired
    private TarGrpConditionMapper tarGrpConditionMapper;
    @Autowired
    private TarGrpConditionPrdMapper tarGrpConditionPrdMapper;
    @Autowired
    private TarGrpPrdMapper tarGrpPrdMapper;
    @Autowired
    private TarGrpMapper tarGrpMapper;

    //同步表名
    private static final String tableName = "tar_grp";


    /**
     * 同步单个客户分群信息
     *
     * @param templateId 客户分群id
     * @param roleName   操作人身份
     * @return
     */
    @Override
    public Map<String, Object> synchronizeSingleTarGrp(Long templateId, String roleName) {
        Map<String, Object> maps = new HashMap<>();
        //查询源数据库
        TarGrp tarGrp = tarGrpMapper.selectByPrimaryKey(templateId);
        if (tarGrp == null) {
            throw new SystemException("对应分客户分群信息不存在");
        }

        List<TarGrpCondition> tarGrpConditions = tarGrpConditionMapper.listTarGrpCondition(tarGrp.getTarGrpId());

        //同步时查看是新增还是更新
        TarGrp tarGrp1 = tarGrpPrdMapper.selectByPrimaryKey(templateId);
        if (tarGrp1 == null) {
            tarGrpPrdMapper.insert(tarGrp);
            if (!tarGrpConditions.isEmpty()) {
                tarGrpConditionPrdMapper.insertByBatch(tarGrpConditions);
            }
            synchronizeRecordService.addRecord(roleName, tableName, templateId, SynchronizeType.add.getType());
        } else {
            tarGrpPrdMapper.updateByPrimaryKey(tarGrp);
            diffGrpCondition(tarGrpConditions,tarGrp1);
            synchronizeRecordService.addRecord(roleName, tableName, templateId, SynchronizeType.update.getType());
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 批量同步客户分群信息
     *
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> synchronizeBatchTarGrp(String roleName) {
        Map<String, Object> maps = new HashMap<>();
        List<TarGrpDetail> prdList = tarGrpMapper.selectAll();
        List<TarGrpDetail> realList = tarGrpPrdMapper.selectAll();
        //三个集合分别表示需要 新增的   修改的    删除的
        List<TarGrp> addList = new ArrayList<TarGrp>();
        List<TarGrp> updateList = new ArrayList<TarGrp>();
        List<TarGrp> deleteList = new ArrayList<TarGrp>();
        for (TarGrpDetail c : prdList) {
            for (int i = 0; i < realList.size(); i++) {
                if (c.getTarGrpId() - realList.get(i).getTarGrpId() == 0) {
                    //需要修改的
                    updateList.add(c);
                    break;
                } else if (i == realList.size() - 1) {
                    //需要新增的  准生产存在，生产不存在
                    addList.add(c);
                }
            }
        }
        for (TarGrpDetail c : realList) {
            for (int i = 0; i < prdList.size(); i++) {
                if (c.getTarGrpId() - prdList.get(i).getTarGrpId() == 0) {
                    break;
                } else if (i == prdList.size() - 1) {
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }
        //开始新增
        for (TarGrp c : addList) {
            tarGrpPrdMapper.insert(c);
            List<TarGrpCondition> tarGrpConditions = tarGrpConditionMapper.listTarGrpCondition(c.getTarGrpId());
            diffGrpCondition(tarGrpConditions,c);
            synchronizeRecordService.addRecord(roleName, tableName, c.getTarGrpId(), SynchronizeType.add.getType());
        }
        //开始修改
        for (TarGrp c : updateList) {
            tarGrpPrdMapper.updateByPrimaryKey(c);
            List<TarGrpCondition> tarGrpConditions = tarGrpConditionMapper.listTarGrpCondition(c.getTarGrpId());
            diffGrpCondition(tarGrpConditions,c);
            synchronizeRecordService.addRecord(roleName, tableName, c.getTarGrpId(), SynchronizeType.update.getType());
        }
        //开始删除
        for (TarGrp c : deleteList) {
            tarGrpPrdMapper.deleteByPrimaryKey(c.getTarGrpId());
            tarGrpConditionPrdMapper.deleteByTarGrpTemplateId(c.getTarGrpId());
            synchronizeRecordService.addRecord(roleName, tableName, c.getTarGrpId(), SynchronizeType.delete.getType());
        }

        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);

        return maps;
    }


    @Override
    public Map<String, Object> deleteSingleTarGrp(Long templateId, String roleName) {
        Map<String, Object> maps = new HashMap<>();
        tarGrpPrdMapper.deleteByPrimaryKey(templateId);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", org.apache.commons.lang.StringUtils.EMPTY);
        return maps;
    }


    /**
     * 比较两个环境下分群的匹配条件配置
     *
     * @param prdList
     * @param tarGrp1
     */
    public void diffGrpCondition(List<TarGrpCondition> prdList, TarGrp tarGrp1) {
        List<TarGrpCondition> realList = tarGrpConditionPrdMapper.listTarGrpCondition(tarGrp1.getTarGrpId());
        //首先判断准生产 或生产是否存在某一方数据修改为0的情况
        if (prdList.isEmpty() || realList.isEmpty()) {
            if (prdList.isEmpty() && !realList.isEmpty()) {
                //清除生产环境数据
                for (int i = 0; i < realList.size(); i++) {
                    tarGrpConditionPrdMapper.deleteByPrimaryKey(realList.get(i).getConditionId());
                }
            } else if (!prdList.isEmpty()) {
                //全量新增准生产的数据到生产环境
                for (int i = 0; i < prdList.size(); i++) {
                    tarGrpConditionPrdMapper.insert(prdList.get(i));
                }
            }
            return;
        }
        List<TarGrpCondition> addList = new ArrayList<TarGrpCondition>();
        List<TarGrpCondition> updateList = new ArrayList<TarGrpCondition>();
        List<TarGrpCondition> deleteList = new ArrayList<TarGrpCondition>();

        for (TarGrpCondition c : prdList) {
            for (int i = 0; i < realList.size(); i++) {
                if (c.getConditionId() - realList.get(i).getConditionId() == 0) {
                    //需要修改的
                    updateList.add(c);
                    break;
                } else if (i == realList.size() - 1) {
                    //需要新增的  准生产存在，生产不存在
                    addList.add(c);
                }
            }
        }
        for (TarGrpCondition c : realList) {
            for (int i = 0; i < prdList.size(); i++) {
                if (c.getConditionId() - prdList.get(i).getConditionId() == 0) {
                    break;
                } else if (i == prdList.size() - 1) {
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }
        //开始新增
        for (TarGrpCondition c : addList) {
            tarGrpConditionPrdMapper.insert(c);
        }
        //开始修改
        for (TarGrpCondition c : updateList) {
            tarGrpConditionPrdMapper.updateByPrimaryKey(c);
        }
        //开始删除
        for (TarGrpCondition c : deleteList) {
            tarGrpConditionPrdMapper.deleteByPrimaryKey(c.getConditionId());
        }

    }

}
