package com.zjtelcom.cpct.open.serviceImpl.item;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCamItemMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamItem;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.common.CommonUtil;
import com.zjtelcom.cpct.open.base.common.FormatBeanUtil;
import com.zjtelcom.cpct.open.base.service.BaseService;
import com.zjtelcom.cpct.open.entity.mktCamEvtRel.OpenMktCamEvtRel;
import com.zjtelcom.cpct.open.entity.mktCamItem.OpenMktCamItem;
import com.zjtelcom.cpct.open.service.item.OpenMktCamItemService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: anson
 * @CreateDate: 2018-11-02 09:53:56
 * @version: V 1.0
 * 营销活动推荐条目openapi相关服务
 */
@Service
@Transactional
public class OpenMktCamItemServiceImpl extends BaseService implements OpenMktCamItemService {

    @Autowired
    private MktCamItemMapper mktCamItemMapper;
    @Autowired
    private MktCampaignMapper mktCampaignMapper;
    @Autowired
    private TarGrpMapper tarGrpMapper;
    /**
     * 查询营销活动推荐条目信息
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> queryById(String id) {
        Map<String, Object> resultMap = new HashMap<>();
        Long queryId = CommonUtil.stringToLong(id);
        MktCamItem mktCamItem = mktCamItemMapper.selectByPrimaryKey(queryId);
        if(null == mktCamItem){
            throw new SystemException("对应营销活动推荐条目信息不存在!");
        }
        OpenMktCamItem openMktCamItem = BeanUtil.create(mktCamItem,new OpenMktCamItem());
        //openMktCamItem.setId(Long.valueOf(id));
        //openMktCamItem.setHref("/mktCamItem/" + id);

        MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCamItem.getMktCampaignId());
        if(mktCampaignDO != null) {
            openMktCamItem.setMktActivityNbr(mktCampaignDO.getMktActivityNbr());
        }
        resultMap.put("params",openMktCamItem);
        return resultMap;
    }


    /**
     * 新增营销活动推荐条目
     * @param object
     * @return
     */
    @Override
    public Map<String, Object> addByObject(Object object) {
        Map<String, Object> resultMap = new HashMap<>();
        MktCamItem mktCamItem = (MktCamItem) object;
        mktCamItem.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
        mktCamItem.setStatusDate(DateUtil.getCurrentTime());
        mktCamItem.setCreateDate(DateUtil.getCurrentTime());
        mktCamItem.setUpdateDate(DateUtil.getCurrentTime());
        mktCamItem.setUpdateStaff(UserUtil.loginId());
        mktCamItem.setCreateStaff(UserUtil.loginId());
        mktCamItemMapper.insert(mktCamItem);

        resultMap = queryById(String.valueOf(mktCamItem.getMktCamItemId()));
        return resultMap;
    }


    /**
     * 修改营销活动推荐条目信息
     * @param id
     * @param object
     * @return
     */
    @Override
    public Map<String, Object> updateByParams(String id, Object object) {
        Map<String, Object> resultMap = new HashMap<>();
        Long queryId = CommonUtil.stringToLong(id);
        MktCamItem mktCamItem = mktCamItemMapper.selectByPrimaryKey(queryId);
        if(null == mktCamItem){
            throw new SystemException("对应营销活动推荐条目信息不存在!");
        }

        JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(mktCamItem));
        JSONArray array = (JSONArray) JSONArray.parse((String) object);
        for (int i = 0; i <array.size() ; i++) {
            JSONObject jsonObject = (JSONObject) array.get(i);
            if(((String)jsonObject.get("op")).equals("replace")) {
                String path = ((String) jsonObject.get("path")).substring(1);
                json.put(path, jsonObject.getString("value"));
            }
        }
        MktCamItem mktCamItems = JSONObject.parseObject(json.toJSONString(), MktCamItem.class);
        mktCamItems.setUpdateDate(DateUtil.getCurrentTime());
        mktCamItems.setUpdateStaff(UserUtil.loginId());
        mktCamItemMapper.updateByPrimaryKey(mktCamItems);

        OpenMktCamItem openMktCamItem = BeanUtil.create(mktCamItems,new OpenMktCamItem());
        //openMktCamItem.setId(Long.valueOf(id));
        //openMktCamItem.setHref("/mktCamItem/" + id);
        MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCamItem.getMktCampaignId());
        if(mktCampaignDO != null) {
            openMktCamItem.setMktActivityNbr(mktCampaignDO.getMktActivityNbr());
        }

        resultMap.put("params", openMktCamItem);
        return resultMap;
    }

    /**
     * 删除营销活动推荐条目信息
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> deleteById(String id) {
        Map<String, Object> resultMap = new HashMap<>();
        Long queryId= CommonUtil.stringToLong(id);
        int i = mktCamItemMapper.deleteByPrimaryKey(queryId);
        if(i == 0) {
            throw new SystemException("对应营销活动推荐条目信息不存在!");
        }
        return resultMap;
    }


    /**
     * 查询营销活动推荐条目信息列表
     * @param map
     * @return
     */
    @Override
    public Map<String, Object> queryListByMap(Map<String, Object> map) {
        Map<String, Object> resultMap = new HashMap<>();
        MktCamItem mktCamItem = new MktCamItem();
        List<OpenMktCamItem> openMktCamItemList = new ArrayList<>();
        CommonUtil.setPage(map);
        if(StringUtils.isNotBlank((String) map.get("mktCamItemId"))){
            mktCamItem.setMktCamItemId(Long.valueOf((String)map.get("mktCamItemId")));
        }

        List<MktCamItem> mktCamItemList=new ArrayList<>();
//                List<MktCamItem> mktCamItemList= mktCamItemMapper.selectByPrimaryKey(mktCamItem);
        if(null == mktCamItemList){
            throw new SystemException("对应营销活动推荐条目信息不存在!");
        }

        for(MktCamItem camItem : mktCamItemList) {
            OpenMktCamItem openMktCamItem = BeanUtil.create(camItem,new OpenMktCamItem());
            //openMktCamItem.setId(mktCamItem.getMktCamItemId());
            //openMktCamItem.setHref("/mktCamItem/" + mktCamItem.getMktCamItemId());
            //活动编码
            MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCamItem.getMktCampaignId());
            if(mktCampaignDO != null) {
                openMktCamItem.setMktActivityNbr(mktCampaignDO.getMktActivityNbr());
            }
            openMktCamItemList.add(openMktCamItem);
        }

        Page pageInfo = new Page(new PageInfo(openMktCamItemList));
        resultMap.put("params",openMktCamItemList);
        resultMap.put("size", String.valueOf(pageInfo.getTotal()));
        return resultMap;
    }
}
