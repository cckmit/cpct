package com.zjtelcom.cpct.service.impl.synchronize.campaign;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCampaignRelMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignRelDO;
import com.zjtelcom.cpct.enums.SynchronizeType;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import com.zjtelcom.cpct.service.synchronize.campaign.SynMktCampaignRelService;
import com.zjtelcom.cpct_prd.dao.campaign.MktCampaignRelPrdMapper;
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
 * @Date: 2018/9/19
 * @Description:营销维挽活动同步
 */
@Service
@Transactional
public class SynMktCampaignRelServiceImpl implements SynMktCampaignRelService {

    @Autowired
    private SynchronizeRecordService synchronizeRecordService;
    @Autowired
    private MktCampaignRelMapper mktCampaignRelMapper;
    @Autowired
    private MktCampaignRelPrdMapper mktCampaignRelPrdMapper;


    //同步表名
    private static final String tableName="mkt_campaign_rel";


    /**
     * 同步单个营销维挽活动
     * @param campaignRelId
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> synchronizeSingleCampaignRel(Long campaignRelId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        MktCampaignRelDO mktCampaignRelDO = mktCampaignRelMapper.selectByPrimaryKey(campaignRelId);
        if(mktCampaignRelDO==null){
            throw new SystemException("对应营销维挽活动信息不存在");
        }
        //同步时查看是新增还是更新
        MktCampaignRelDO mktCampaignRelDO1=mktCampaignRelPrdMapper.selectByPrimaryKey(campaignRelId);
        if(mktCampaignRelDO1==null){
            mktCampaignRelPrdMapper.insert(mktCampaignRelDO);
            synchronizeRecordService.addRecord(roleName,tableName,campaignRelId, SynchronizeType.add.getType());
        }else{
            mktCampaignRelPrdMapper.updateByPrimaryKey(mktCampaignRelDO);
            synchronizeRecordService.addRecord(roleName,tableName,campaignRelId, SynchronizeType.update.getType());
        }

        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);

        return maps;
    }


    /**
     * 批量同步营销维挽活动
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> synchronizeBatchCampaignRel(String roleName) {
        Map<String,Object> maps = new HashMap<>();
        //查出准生产的所有事件目录
        List<MktCampaignRelDO> prdList = mktCampaignRelMapper.selectAll();
        //查出生产环境所有事件目录
        List<MktCampaignRelDO> realList = mktCampaignRelPrdMapper.selectAll();

        List<MktCampaignRelDO> addList=new ArrayList<MktCampaignRelDO>();
        List<MktCampaignRelDO> updateList=new ArrayList<MktCampaignRelDO>();
        List<MktCampaignRelDO> deleteList=new ArrayList<MktCampaignRelDO>();
        for(MktCampaignRelDO c:prdList){
            for (int i = 0; i <realList.size() ; i++) {
                if(c.getMktCampaignRelId()-realList.get(i).getMktCampaignRelId()==0){
                    //需要修改的
                    updateList.add(c);
                    break;
                }else if(i==realList.size()-1){
                    //需要新增的  准生产存在，生产不存在
                    addList.add(c);
                }
            }
        }
        //查出需要删除的事件目录
        for(MktCampaignRelDO c:realList){
            for (int i = 0; i <prdList.size() ; i++) {
                if(c.getMktCampaignRelId()-prdList.get(i).getMktCampaignRelId()==0){
                    break;
                }else if (i==prdList.size()-1){
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }

        //开始新增
        for(MktCampaignRelDO c:addList){
            mktCampaignRelPrdMapper.insert(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getMktCampaignRelId(), SynchronizeType.add.getType());
        }
        //开始修改
        for(MktCampaignRelDO c:updateList){
            mktCampaignRelPrdMapper.updateByPrimaryKey(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getMktCampaignRelId(), SynchronizeType.update.getType());
        }
        //开始删除
        for(MktCampaignRelDO c:deleteList){
            mktCampaignRelPrdMapper.deleteByPrimaryKey(c.getMktCampaignRelId());
            synchronizeRecordService.addRecord(roleName,tableName,c.getMktCampaignRelId(), SynchronizeType.delete.getType());
        }

        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);

        return maps;
    }
}
