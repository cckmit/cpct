package com.zjtelcom.cpct.open.serviceImpl.camEvtRel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCamEvtRelMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamEvtRelDO;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.ContactEvtType;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.common.CommonUtil;
import com.zjtelcom.cpct.open.base.common.FormatBeanUtil;
import com.zjtelcom.cpct.open.base.service.BaseService;
import com.zjtelcom.cpct.open.entity.mktCamEvtRel.OpenEventRef;
import com.zjtelcom.cpct.open.entity.mktCamEvtRel.OpenMktCamEvtRel;
import com.zjtelcom.cpct.open.service.camEvtRel.OpenMktCamEvtRelService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.crypto.Data;
import java.util.*;

/**
 * @author: anson
 * @CreateDate: 2018-11-03 17:06:17
 * @version: V 1.0
 * 营销活动关联事件openapi相关服务
 */
@Service
@Transactional
public class OpenMktCamEvtRelServiceImpl extends BaseService implements OpenMktCamEvtRelService {

    @Autowired
    private MktCamEvtRelMapper mktCamEvtRelMapper;
    @Autowired
    private ContactEvtMapper contactEvtMapper;

    /**
     * 查询营销活动关联事件信息
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> queryById(String id) {
        Map<String, Object> resultMap = new HashMap<>();
        long queryId= CommonUtil.stringToLong(id);
        MktCamEvtRelDO mktCamEvtRelDO = mktCamEvtRelMapper.selectByPrimaryKey(queryId);
        if(null == mktCamEvtRelDO){
            throw new SystemException("对应营销活动关联事件信息不存在!");
        }
        OpenMktCamEvtRel openMktCamEvtRel = BeanUtil.create(mktCamEvtRelDO,new OpenMktCamEvtRel());
        openMktCamEvtRel.setId(id);
        openMktCamEvtRel.setHref("/mktCamEvtRel/" + id);
        openMktCamEvtRel.setContactEvtId(mktCamEvtRelDO.getEventId());
        if(mktCamEvtRelDO.getStatusDate() != null) {
            openMktCamEvtRel.setStatusDate(DateUtil.getDatetime(mktCamEvtRelDO.getStatusDate()));
        }
        OpenEventRef openEventRef = new OpenEventRef();
        ContactEvt contactEvt = contactEvtMapper.getEventById(mktCamEvtRelDO.getEventId());
        if(contactEvt != null) {
            openEventRef.setId(String.valueOf(contactEvt.getContactEvtId()));
            openEventRef.setHref("/event/" + String.valueOf(contactEvt.getContactEvtId()));
            openEventRef.setName(contactEvt.getContactEvtName());
        }
        openMktCamEvtRel.setEventRef(openEventRef);

        resultMap.put("params",openMktCamEvtRel);
        return resultMap;
    }


    /**
     * 新增营销活动关联事件
     * @param object
     * @return
     */
    @Override
    public Map<String, Object> addByObject(Object object) {
        Map<String, Object> resultMap = new HashMap<>();
        OpenMktCamEvtRel openMktCamEvtRel = (OpenMktCamEvtRel) object;

        MktCamEvtRelDO mktCamEvtRelDO = BeanUtil.create(openMktCamEvtRel,new MktCamEvtRelDO());
        mktCamEvtRelDO.setEventId(openMktCamEvtRel.getContactEvtId());
        mktCamEvtRelDO.setMktCampaignId(openMktCamEvtRel.getMktCampaignId());
        mktCamEvtRelDO.setCreateDate(DateUtil.getCurrentTime());
        mktCamEvtRelDO.setUpdateDate(DateUtil.getCurrentTime());
        mktCamEvtRelDO.setStatusDate(DateUtil.getCurrentTime());
        mktCamEvtRelDO.setUpdateStaff(UserUtil.loginId());
        mktCamEvtRelDO.setCreateStaff(UserUtil.loginId());
        mktCamEvtRelDO.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
        mktCamEvtRelMapper.insert(mktCamEvtRelDO);

        MktCamEvtRelDO mktCamEvtRelDO1 = mktCamEvtRelMapper.findByCampaignIdAndEvtId(mktCamEvtRelDO.getMktCampaignId(),mktCamEvtRelDO.getEventId());
        OpenMktCamEvtRel result = BeanUtil.create(mktCamEvtRelDO1,new OpenMktCamEvtRel());
        result.setId(String.valueOf(mktCamEvtRelDO1.getMktCampEvtRelId()));
        result.setHref("/mktCamEvtRel" + mktCamEvtRelDO1.getMktCampEvtRelId());
        result.setContactEvtId(mktCamEvtRelDO1.getEventId());
        openMktCamEvtRel.setStatusDate(DateUtil.getDatetime(mktCamEvtRelDO.getStatusDate()));

        resultMap.put("params",result);
        return resultMap;
    }


    /**
     * 修改营销活动关联事件信息
     * @param id
     * @param object
     * @return
     */
    @Override
    public Map<String, Object> updateByParams(String id, Object object) {
        Map<String, Object> resultMap = new HashMap<>();
        Long queryId = CommonUtil.stringToLong(id);
        MktCamEvtRelDO mktCamEvtRelDO = mktCamEvtRelMapper.selectByPrimaryKey(queryId);
        if(null == mktCamEvtRelDO){
            throw new SystemException("对应营销活动关联事件信息不存在!");
        }

        JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(mktCamEvtRelDO));
        JSONArray array = (JSONArray) JSONArray.parse((String)object);
        for (int i = 0; i <array.size() ; i++) {
            JSONObject jsonObject = (JSONObject) array.get(i);
            if(((String)jsonObject.get("op")).equals("replace")) {
                String path = ((String) jsonObject.get("path")).substring(1);
                if (path.equals("contactEvtId")) {
                    json.put("eventId", jsonObject.getString("value"));
                } else{
                    json.put(path.substring(1), jsonObject.getString("value"));
                }
            }
        }
        MktCamEvtRelDO mktCamEvtRelDs = JSONObject.parseObject(json.toJSONString(), MktCamEvtRelDO.class);
        mktCamEvtRelMapper.updateByPrimaryKey(mktCamEvtRelDs);

        //查找
        OpenMktCamEvtRel openMktCamEvtRel = BeanUtil.create(mktCamEvtRelDs,new OpenMktCamEvtRel());
        openMktCamEvtRel.setId(id);
        openMktCamEvtRel.setHref("/mktCamEvtRel/" + id);
        openMktCamEvtRel.setContactEvtId(mktCamEvtRelDO.getEventId());

        resultMap.put("params",openMktCamEvtRel);
        return resultMap;
    }

    /**
     * 删除营销活动关联事件信息
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> deleteById(String id) {
        Map<String, Object> resultMap = new HashMap<>();
        Long queryId= CommonUtil.stringToLong(id);
        int i = mktCamEvtRelMapper.deleteByPrimaryKey(queryId);
        if(i == 0){
            throw new SystemException("对应营销活动关联事件不存在");
        }
        return resultMap;
    }


    /**
     * 查询营销活动关联事件信息列表
     * @param map
     * @return
     */
    @Override
    public Map<String, Object> queryListByMap(Map<String, Object> map) {
        Map<String, Object> resultMap = new HashMap<>();
        List<OpenMktCamEvtRel> openMktCamEvtRelList =new ArrayList<>();
        MktCamEvtRelDO mktCamEvtRelDO = new MktCamEvtRelDO();

        CommonUtil.setPage(map);

        if(StringUtils.isNotBlank((String) map.get("mktCampEvtRelId"))){
            mktCamEvtRelDO.setMktCampEvtRelId(Long.valueOf((String)map.get("mktCampEvtRelId")));
        }
        if(StringUtils.isNotBlank((String) map.get("mktCampaignId"))){
            mktCamEvtRelDO.setMktCampaignId(Long.valueOf((String)map.get("mktCampaignId")));
        }

        List<MktCamEvtRelDO> mktCamEvtRelDOList = mktCamEvtRelMapper.selectByMktCamEvtRel(mktCamEvtRelDO);
        if(mktCamEvtRelDOList == null){
            throw new SystemException("对应营销活动关联事件不存在");
        }

        for(MktCamEvtRelDO camEvtRelDO: mktCamEvtRelDOList){
            OpenMktCamEvtRel openMktCamEvtRel = BeanUtil.create(camEvtRelDO,new OpenMktCamEvtRel());
            openMktCamEvtRel.setId(String.valueOf(camEvtRelDO.getMktCampEvtRelId()));
            openMktCamEvtRel.setHref("/mktCamEvtRel/" + camEvtRelDO.getMktCampEvtRelId());
            openMktCamEvtRel.setContactEvtId(camEvtRelDO.getEventId());
            if(mktCamEvtRelDO.getStatusDate() != null) {
                openMktCamEvtRel.setStatusDate(DateUtil.getDatetime(camEvtRelDO.getStatusDate()));
            }
            openMktCamEvtRelList.add(openMktCamEvtRel);
        }

        Page pageInfo = new Page(new PageInfo(openMktCamEvtRelList));
        resultMap.put("size", String.valueOf(pageInfo.getTotal()));
        resultMap.put("params",openMktCamEvtRelList);
        return resultMap;
    }
}
