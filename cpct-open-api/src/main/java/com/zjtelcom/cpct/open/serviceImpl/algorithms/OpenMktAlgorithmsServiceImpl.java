package com.zjtelcom.cpct.open.serviceImpl.algorithms;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktAlgorithmsMapper;
import com.zjtelcom.cpct.domain.campaign.MktAlgorithms;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.common.CommonUtil;
import com.zjtelcom.cpct.open.base.service.BaseService;
import com.zjtelcom.cpct.open.entity.mktAlgorithms.OpenMktAlgorithms;
import com.zjtelcom.cpct.open.service.algorithms.OpenMktAlgorithmsService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class OpenMktAlgorithmsServiceImpl  extends BaseService implements OpenMktAlgorithmsService{

    @Autowired
    MktAlgorithmsMapper mktAlgorithmsMapper;

     /*
    **查询算法定义详情
    */
     @Override
     public Map<String, Object> getMktAlgorithms(String id) {
         Map<String, Object> resultMap = new HashMap<>();
         Long algoId = Long.valueOf(id);
         MktAlgorithms mktAlgorithms = mktAlgorithmsMapper.selectByPrimaryKey(algoId);
         if(mktAlgorithms != null){
             OpenMktAlgorithms openMktAlgorithms = BeanUtil.create(mktAlgorithms, new OpenMktAlgorithms());
             openMktAlgorithms.setId(id);
             openMktAlgorithms.setHref("/mktAlgorithms/" + id);
             if(mktAlgorithms.getStatusDate() != null) {
                 openMktAlgorithms.setStatusDate(DateUtil.getDatetime(mktAlgorithms.getStatusDate()));
             }
             resultMap.put("params", openMktAlgorithms);
             return resultMap;
         }else{
             throw new SystemException("算法定义不存在!");
         }
     }

     /*
    **新建算法定义
    */
    @Override
    public Map<String, Object> saveMktAlgorithms(OpenMktAlgorithms openMktAlgorithms) {
        Map<String, Object> resultMap = new HashMap<>();

        MktAlgorithms mktAlgorithms = BeanUtil.create(openMktAlgorithms, new MktAlgorithms());
        mktAlgorithms.setCreateDate(DateUtil.getCurrentTime());
        mktAlgorithms.setUpdateDate(DateUtil.getCurrentTime());
        mktAlgorithms.setStatusDate(DateUtil.getCurrentTime());
        mktAlgorithms.setUpdateStaff(UserUtil.loginId());
        mktAlgorithms.setCreateStaff(UserUtil.loginId());
        mktAlgorithms.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
        mktAlgorithmsMapper.saveMktAlgorithms(mktAlgorithms);

        resultMap = getMktAlgorithms(String.valueOf(mktAlgorithms.getAlgoId()));
        return resultMap;
    }

     /*
    **修改算法定义
    */
    @Override
    public Map<String, Object> updateMktAlgorithms(String id, String params) {
        Map<String, Object> resultMap = new HashMap<>();
        Long algoId = Long.valueOf(id);
        MktAlgorithms mktAlgorithms = mktAlgorithmsMapper.selectByPrimaryKey(algoId);
        if(mktAlgorithms == null){
            throw new SystemException("算法定义不存在!");
        }
        JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(mktAlgorithms));
        JSONArray array = (JSONArray) JSONArray.parse(params);
        for (int i = 0; i <array.size() ; i++) {
            JSONObject jsonObject = (JSONObject) array.get(i);
            if(((String)jsonObject.get("op")).equals("replace")) {
                String path = ((String) jsonObject.get("path")).substring(1);
                json.put(path, jsonObject.getString("value"));
            }
        }
        MktAlgorithms algorithms = JSONObject.parseObject(json.toJSONString(), MktAlgorithms.class);
        mktAlgorithmsMapper.updateMktAlgorithms(algorithms);

        resultMap = getMktAlgorithms(id);
        return resultMap;
    }

      /*
    **删除算法定义
    */
    @Override
    public Map<String, Object> deleteMktAlgorithms(String id){
        Map<String, Object> resultMap = new HashMap<>();
        Long algoId = Long.valueOf(id);
        int i = mktAlgorithmsMapper.deleteByPrimaryKey(algoId);
        if(i == 0){
            throw new SystemException("算法定义不存在!");
        }
        return resultMap;
    }

      /*
    **根据多种查询条件查询算法定义
    */
    @Override
    public Map<String, Object> listMktAlgorithms(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        CommonUtil.setPage(params);
        MktAlgorithms algorithms = new MktAlgorithms();

        if(StringUtils.isNotBlank((String) params.get("algoId"))){
            algorithms.setAlgoId(Long.valueOf((String)params.get("algoId")));
        }
        if(StringUtils.isNotBlank((String) params.get("algoName"))){
            algorithms.setAlgoName((String)params.get("algoName"));
        }

        List<MktAlgorithms> mktAlgorithmsList = mktAlgorithmsMapper.selectByMktAlgorithms(algorithms);
        if(mktAlgorithmsList == null){
            throw new SystemException("算法定义不存在!");
        }

        List<OpenMktAlgorithms> openMktAlgorithmsList = new ArrayList<>();
        for(MktAlgorithms mktAlgorithms : mktAlgorithmsList) {
            OpenMktAlgorithms openMktAlgorithms = BeanUtil.create(mktAlgorithms, new OpenMktAlgorithms());
            openMktAlgorithms.setId(String.valueOf(mktAlgorithms.getAlgoId()));
            openMktAlgorithms.setHref("/mktAlgorithms/" + mktAlgorithms.getAlgoId());
            if(mktAlgorithms.getStatusDate() != null) {
                openMktAlgorithms.setStatusDate(DateUtil.getDatetime(mktAlgorithms.getStatusDate()));
            }
            openMktAlgorithmsList.add(openMktAlgorithms);
        }

        Page pageInfo = new Page(new PageInfo(mktAlgorithmsList));
        resultMap.put("params", openMktAlgorithmsList);
        resultMap.put("size", String.valueOf(pageInfo.getTotal()));
        return resultMap;

    }

}
