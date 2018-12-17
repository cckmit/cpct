package com.zjtelcom.cpct.open.serviceImpl.camChlConf;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.dao.campaign.MktCamChlConfAttrMapper;
import com.zjtelcom.cpct.dao.campaign.MktCamChlConfMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamChlConfAttrDO;
import com.zjtelcom.cpct.domain.campaign.MktCamChlConfDO;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfAttr;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.common.CommonUtil;
import com.zjtelcom.cpct.open.base.service.BaseService;
import com.zjtelcom.cpct.open.entity.mktCamChlConf.OpenMktCamChlConf;
import com.zjtelcom.cpct.open.entity.mktCamChlConf.OpenMktCamChlConfAttr;
import com.zjtelcom.cpct.open.service.camChlConf.OpenCamChlConfService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.bouncycastle.asn1.ua.DSTU4145NamedCurves.params;

@Service
@Transactional
public class OpenCamChlConfServiceImpl extends BaseService implements OpenCamChlConfService {

    @Autowired
    private MktCamChlConfMapper mktCamChlConfMapper;
    @Autowired
    private MktCamChlConfAttrMapper mktCamChlConfAttrMapper;
    @Autowired
    private MktCampaignMapper mktCampaignMapper;

    /*
    **查询营销执行渠道推送规则详情
    */
    @Override
    public Map<String, Object> queryById(String id) {
        Map<String, Object> resultMap = new HashMap<>();
        MktCamChlConfDO mktCamChlConfDO = mktCamChlConfMapper.selectByPrimaryKey(Long.valueOf(id));
        if(null != mktCamChlConfDO) {
            OpenMktCamChlConf openMktCamChlConf = BeanUtil.create(mktCamChlConfDO, new OpenMktCamChlConf());
            openMktCamChlConf.setId(String.valueOf(id));
            openMktCamChlConf.setHref("/eventType/" + id);
            if(mktCamChlConfDO.getStatusDate() != null) {
                openMktCamChlConf.setStatusDate(DateUtil.getDatetime(mktCamChlConfDO.getStatusDate()));
            }
            MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(openMktCamChlConf.getMktCampaignId());
            if(mktCampaignDO != null) {
                openMktCamChlConf.setMktActivityNbr(mktCampaignDO.getMktActivityNbr());
            }
            //营销活动渠道推送配置属性
            List<MktCamChlConfAttrDO> mktCamChlConfAttrDOList = mktCamChlConfAttrMapper.selectByEvtContactConfId(mktCamChlConfDO.getEvtContactConfId());
            List<OpenMktCamChlConfAttr> openMktCamChlConfAttrList = new ArrayList<>();
            if(mktCamChlConfAttrDOList != null) {
                for(MktCamChlConfAttrDO mktCamChlConfAttrDO : mktCamChlConfAttrDOList){
                    OpenMktCamChlConfAttr openMktCamChlConfAttr = BeanUtil.create(mktCamChlConfAttrDO,new OpenMktCamChlConfAttr());
                    if(mktCamChlConfAttrDO.getStatusDate() != null) {
                        openMktCamChlConfAttr.setStatusDate(DateUtil.getDatetime(mktCamChlConfAttrDO.getStatusDate()));
                    }
                    openMktCamChlConfAttrList.add(openMktCamChlConfAttr);
                }
            }
            openMktCamChlConf.setMktCamChlConfAttr(openMktCamChlConfAttrList);

            resultMap.put("params", openMktCamChlConf);
            return resultMap;
        }else {
            throw new SystemException("营销执行渠道推送规则id为" + id + " 所对应的营销执行渠道推送规则不存在!");
        }

    }

    /*
    **新建营销执行渠道推送规则
    */
    @Override
    public Map<String, Object> addByObject(Object object) {
        Map<String, Object> resultMap = new HashMap<>();
        OpenMktCamChlConf openMktCamChlConf = (OpenMktCamChlConf) object;

        MktCamChlConfDO mktCamChlConfDO = BeanUtil.create(openMktCamChlConf,new MktCamChlConfDO());
        mktCamChlConfDO.setStatusDate(new Date());
        mktCamChlConfDO.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
        mktCamChlConfDO.setCreateDate(new Date());
        mktCamChlConfDO.setCreateStaff(UserUtil.loginId());
        mktCamChlConfDO.setUpdateDate(new Date());
        mktCamChlConfDO.setUpdateStaff(UserUtil.loginId());
        mktCamChlConfMapper.insert(mktCamChlConfDO);

        List<OpenMktCamChlConfAttr> openMktCamChlConfAttrList = openMktCamChlConf.getMktCamChlConfAttr();
        for(OpenMktCamChlConfAttr openMktCamChlConfAttr : openMktCamChlConfAttrList) {
            MktCamChlConfAttrDO mktCamChlConfAttrDO = BeanUtil.create(openMktCamChlConfAttr, new MktCamChlConfAttrDO());
            mktCamChlConfAttrDO.setEvtContactConfId(mktCamChlConfDO.getEvtContactConfId());
            mktCamChlConfAttrDO.setCreateDate(new Date());
            mktCamChlConfAttrDO.setCreateStaff(UserUtil.loginId());
            mktCamChlConfAttrDO.setUpdateDate(new Date());
            mktCamChlConfAttrDO.setUpdateStaff(UserUtil.loginId());
            mktCamChlConfAttrMapper.insert(mktCamChlConfAttrDO);
        }

        resultMap = queryById(String.valueOf(mktCamChlConfDO.getEvtContactConfId()));
        return resultMap;
    }

    /*
    **修改营销执行渠道推送规则
    */
    @Override
    public Map<String, Object> updateByParams(String id,Object object) {
        Map<String, Object> resultMap = new HashMap<>();

        JSONArray array = (JSONArray) JSONArray.parse((String)object);
        for (int i = 0; i <array.size() ; i++) {
            JSONObject jsonObject = (JSONObject) array.get(i);
            String op = (String) jsonObject.get("op");
            String path = (String) jsonObject.get("path");

            if(path.indexOf("/", 1) < 0){
                MktCamChlConfDO mktCamChlConfDO = mktCamChlConfMapper.selectByPrimaryKey(Long.valueOf(id));
                if (null == mktCamChlConfDO) {
                    throw new SystemException("对应营销执行渠道推送规则不存在");
                }
                if(op.equals("replace")) {
                    path = path.substring(1);
                    JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(mktCamChlConfDO));
                    json.put(path, jsonObject.getString("value"));
                    MktCamChlConfDO camChlConfDO = JSONObject.parseObject(json.toJSONString(), MktCamChlConfDO.class);
                    mktCamChlConfMapper.updateByPrimaryKey(camChlConfDO);
                }
            }else{
                if (op.equals("replace")) {
                    MktCamChlConfAttrDO mktCamChlConfAttrDO = mktCamChlConfAttrMapper.selectByPrimaryKey(Long.valueOf(path.substring(path.indexOf("/", 1) + 1)));
                    if(mktCamChlConfAttrDO != null) {
                        JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(mktCamChlConfAttrDO));
                        json.putAll((Map<String,Object>)jsonObject.get("value"));
                        MktCamChlConfAttrDO camChlConfAttrDO = JSONObject.parseObject(json.toJSONString(), MktCamChlConfAttrDO.class);
                        mktCamChlConfAttrMapper.updateByPrimaryKey(camChlConfAttrDO);
                    }else{
                        throw new SystemException("对应营销执行渠道推送规则条件不存在");
                    }
                }
            }
        }

        //查找
        resultMap = queryById(id);
        return resultMap;
    }


    /*
    **删除事件类型
    */
    @Override
    public Map<String, Object> deleteById(String id) {
        Map<String, Object> resultMap = new HashMap<>();
        int i = mktCamChlConfMapper.deleteByPrimaryKey(Long.valueOf(id));
        if(i == 0){
            throw new SystemException("对应营销执行渠道推送规则不存在");
        }else {
            mktCamChlConfAttrMapper.deleteByEvtContactConfId(Long.valueOf(id));
        }

        return resultMap;
    }

    /*
    **查询事件类型列表
    */
    @Override
    public Map<String, Object> queryListByMap(Map<String, Object> map) {
        Map<String, Object> resultMap = new HashMap<>();
        CommonUtil.setPage(map);

        MktCamChlConfDO mktCamChlConfDO = new MktCamChlConfDO();
        if(StringUtils.isNotBlank((String) map.get("evtContactConfId"))){
            mktCamChlConfDO.setEvtContactConfId(Long.valueOf((String)map.get("evtContactConfId")));
        }
        if(StringUtils.isNotBlank((String) map.get("evtContactConfName"))){
            mktCamChlConfDO.setEvtContactConfName((String)map.get("evtContactConfName"));
        }

        List<MktCamChlConfDO> mktCamChlConfDOList = mktCamChlConfMapper.selectByMktCamChlConf(mktCamChlConfDO);
        if(mktCamChlConfDOList == null) {
            throw new SystemException("对应营销执行渠道推送规则不存在");
        }

        List<OpenMktCamChlConf> openMktCamChlConfList = new ArrayList<>();
        for(MktCamChlConfDO camChlConfDO : mktCamChlConfDOList) {
            OpenMktCamChlConf openMktCamChlConf = BeanUtil.create(camChlConfDO, new OpenMktCamChlConf());
            openMktCamChlConf.setId(String.valueOf(camChlConfDO.getEvtContactConfId()));
            openMktCamChlConf.setHref("/eventType/" + camChlConfDO.getEvtContactConfId());
            if(mktCamChlConfDO.getStatusDate() != null) {
                openMktCamChlConf.setStatusDate(DateUtil.getDatetime(mktCamChlConfDO.getStatusDate()));
            }
            MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(openMktCamChlConf.getMktCampaignId());
            if(mktCampaignDO != null) {
                openMktCamChlConf.setMktActivityNbr(mktCampaignDO.getMktActivityNbr());
            }
            openMktCamChlConfList.add(openMktCamChlConf);
        }
        Page pageInfo = new Page(new PageInfo(openMktCamChlConfList));
        resultMap.put("params", openMktCamChlConfList);
        resultMap.put("size", String.valueOf(pageInfo.getTotal()));
        return resultMap;
    }
}
