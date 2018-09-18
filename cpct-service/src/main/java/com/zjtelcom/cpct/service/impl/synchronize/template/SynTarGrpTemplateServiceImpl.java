package com.zjtelcom.cpct.service.impl.synchronize.template;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.grouping.TarGrpTemplateMapper;
import com.zjtelcom.cpct.domain.grouping.TarGrpTemplateDO;
import com.zjtelcom.cpct.enums.SynchronizeType;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import com.zjtelcom.cpct.service.synchronize.template.SynTarGrpTemplateService;
import com.zjtelcom.cpct_prd.dao.template.TarGrpTemplatePrdMapper;
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
public class SynTarGrpTemplateServiceImpl implements SynTarGrpTemplateService{

    @Autowired
    private SynchronizeRecordService synchronizeRecordService;
    @Autowired
    private TarGrpTemplatePrdMapper tarGrpTemplatePrdMapper;
    @Autowired
    private TarGrpTemplateMapper tarGrpTemplateMapper;

    //同步表名
    private static final String tableName="tar_grp_template";


    /**
     * 同步单个分群模板信息
     * @param templateId    分群模板id
     * @param roleName      操作人身份
     * @return
     */
    @Override
    public Map<String, Object> synchronizeSingleTemplate(Long templateId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        //查询源数据库
        TarGrpTemplateDO tarGrpTemplateDO = tarGrpTemplateMapper.selectByPrimaryKey(templateId);
        if(tarGrpTemplateDO==null){
            throw new SystemException("对应分群模板信息不存在");
        }
        //同步时查看是新增还是更新
        TarGrpTemplateDO tarGrpTemplateDO1 = tarGrpTemplatePrdMapper.selectByPrimaryKey(templateId);
        if(tarGrpTemplateDO1==null){
            tarGrpTemplatePrdMapper.insert(tarGrpTemplateDO);
            synchronizeRecordService.addRecord(roleName,tableName,templateId, SynchronizeType.add.getType());
        }else{
            tarGrpTemplatePrdMapper.updateByPrimaryKey(tarGrpTemplateDO);
            synchronizeRecordService.addRecord(roleName,tableName,templateId, SynchronizeType.update.getType());
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 批量同步分群模板信息
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> synchronizeBatchTemplate(String roleName) {
        Map<String,Object> maps = new HashMap<>();
        //先查出准生产的所有事件
        List<TarGrpTemplateDO> prdList = tarGrpTemplateMapper.selectAll();
        //查出生产的所有事件
        List<TarGrpTemplateDO> realList = tarGrpTemplatePrdMapper.selectAll();
        //三个集合分别表示需要 新增的   修改的    删除的
        List<TarGrpTemplateDO> addList=new ArrayList<TarGrpTemplateDO>();
        List<TarGrpTemplateDO> updateList=new ArrayList<TarGrpTemplateDO>();
        List<TarGrpTemplateDO> deleteList=new ArrayList<TarGrpTemplateDO>();
        for(TarGrpTemplateDO c:prdList){
            for (int i = 0; i <realList.size() ; i++) {
                if(c.getTarGrpTemplateId()-realList.get(i).getTarGrpTemplateId()==0){
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
        for(TarGrpTemplateDO c:realList){
            for (int i = 0; i <prdList.size() ; i++) {
                if(c.getTarGrpTemplateId()-prdList.get(i).getTarGrpTemplateId()==0){
                    break;
                }else if (i==prdList.size()-1){
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }
        //开始新增
        for(TarGrpTemplateDO c:addList){
            tarGrpTemplatePrdMapper.insert(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getTarGrpTemplateId(), SynchronizeType.add.getType());
        }
        //开始修改
        for(TarGrpTemplateDO c:updateList){
            tarGrpTemplatePrdMapper.updateByPrimaryKey(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getTarGrpTemplateId(), SynchronizeType.update.getType());
        }
        //开始删除
        for(TarGrpTemplateDO c:deleteList){
            tarGrpTemplatePrdMapper.deleteByPrimaryKey(c.getTarGrpTemplateId());
            synchronizeRecordService.addRecord(roleName,tableName,c.getTarGrpTemplateId(), SynchronizeType.delete.getType());
        }

        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);

        return maps;
    }


}
