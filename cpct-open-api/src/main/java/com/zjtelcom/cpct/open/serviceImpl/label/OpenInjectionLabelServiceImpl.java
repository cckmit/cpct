package com.zjtelcom.cpct.open.serviceImpl.label;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.InjectionLabelGrpMbrMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelValueMapper;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.LabelGrpMbr;
import com.zjtelcom.cpct.domain.channel.LabelValue;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.common.CommonUtil;
import com.zjtelcom.cpct.open.base.service.BaseService;
import com.zjtelcom.cpct.open.entity.label.InjectionLabel;
import com.zjtelcom.cpct.open.entity.label.InjectionLabelGrpMbr;
import com.zjtelcom.cpct.open.entity.label.InjectionLabelValue;
import com.zjtelcom.cpct.open.service.label.OpenInjectionLabelService;
import com.zjtelcom.cpct.util.UserUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author: anson
 * @CreateDate: 2018-11-06 15:01:47
 * @version: V 1.0
 * 注智标签openapi相关服务
 */
@Service
@Transactional
public class OpenInjectionLabelServiceImpl extends BaseService implements OpenInjectionLabelService{

    @Autowired
    private InjectionLabelMapper injectionLabelMapper;
    @Autowired
    private InjectionLabelValueMapper injectionLabelValueMapper;
    @Autowired
    private InjectionLabelGrpMbrMapper injectionLabelGrpMbrMapper;

    /**
     * 查询注智标签信息
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> queryById(String id) {
        Map<String, Object> resultMap = new HashMap<>();
        Long queryId = CommonUtil.stringToLong(id);
        Label label = injectionLabelMapper.selectByPrimaryKey(queryId);
        if(null == label){
            throw new SystemException("对应注智标签信息不存在!");
        }
        //转换为openapi返回规范
        InjectionLabel openInjectionLabel = BeanUtil.create(label,new InjectionLabel());
        //设置id  和href  转换时间为对应格式
        openInjectionLabel.setId(id);
        openInjectionLabel.setHref("/injectionLabel/" + id);
        if(null != label.getStatusDate()){
            openInjectionLabel.setStatusDate(DateUtil.getDatetime(label.getStatusDate()));
        }

        List<InjectionLabelValue> injectionLabelValues = new ArrayList<>();
        List<LabelValue> labelValues = injectionLabelValueMapper.selectByLabelId(queryId);
        if(labelValues != null) {
            for(LabelValue labelValue : labelValues) {
                InjectionLabelValue injectionLabelValue = BeanUtil.create(labelValue, new InjectionLabelValue());
                injectionLabelValues.add(injectionLabelValue);
            }
        }
        openInjectionLabel.setInjectionLabelValue(injectionLabelValues);

        List<InjectionLabelGrpMbr> injectionLabelGrpMbrList= new ArrayList<>();
        List<LabelGrpMbr> labelGrpMbrList = injectionLabelGrpMbrMapper.findListByLabelId(queryId);
        if(labelGrpMbrList != null) {
            for(LabelGrpMbr labelGrpMbr : labelGrpMbrList) {
                InjectionLabelGrpMbr injectionLabelGrpMbr = BeanUtil.create(labelGrpMbr, new InjectionLabelGrpMbr());
                injectionLabelGrpMbrList.add(injectionLabelGrpMbr);
            }
        }
        openInjectionLabel.setInjectionLabelGrpMbr(injectionLabelGrpMbrList);

        resultMap.put("params",openInjectionLabel);
        return resultMap;
    }


    /**
     * 新增注智标签
     * @param object
     * @return
     */
    @Override
    public Map<String, Object> addByObject(Object object) {
        Map<String, Object> resultMap = new HashMap<>();
        InjectionLabel injectionLabel= (InjectionLabel) object;
        Label label = BeanUtil.create(injectionLabel,new Label());
        label.setScope(0);
        label.setClassName("0");
        label.setLabelType("1000");
        label.setCreateDate(new Date());
        label.setUpdateDate(new Date());
        label.setCreateStaff(UserUtil.loginId());
        label.setUpdateStaff(UserUtil.loginId());
        label.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
        label.setStatusDate(new Date());
        injectionLabelMapper.insert(label);

        List<InjectionLabelValue> injectionLabelValueList = injectionLabel.getInjectionLabelValue();
        for(InjectionLabelValue injectionLabelValue : injectionLabelValueList){
            LabelValue labelValue = BeanUtil.create(injectionLabelValue, new LabelValue());
            labelValue.setInjectionLabelId(label.getInjectionLabelId());
            labelValue.setStatusCd("1000");
            labelValue.setStatusDate(new Date());
            labelValue.setCreateDate(new Date());
            labelValue.setUpdateDate(new Date());
            labelValue.setCreateStaff(UserUtil.loginId());
            labelValue.setUpdateStaff(UserUtil.loginId());
            injectionLabelValueMapper.insert(labelValue);
        }

        List<InjectionLabelGrpMbr> injectionLabelGrpMbrList = injectionLabel.getInjectionLabelGrpMbr();
        for(InjectionLabelGrpMbr injectionLabelGrpMbr : injectionLabelGrpMbrList) {
            LabelGrpMbr labelGrpMbr = BeanUtil.create(injectionLabelGrpMbr, new LabelGrpMbr());
            labelGrpMbr.setInjectionLabelId(label.getInjectionLabelId());
            labelGrpMbr.setCreateDate(new Date());
            labelGrpMbr.setCreateStaff(UserUtil.loginId());
            labelGrpMbr.setUpdateDate(new Date());
            labelGrpMbr.setUpdateStaff(UserUtil.loginId());
            labelGrpMbr.setStatusCd("1000");
            labelGrpMbr.setStatusDate(new Date());
            injectionLabelGrpMbrMapper.insert(labelGrpMbr);
        }

        resultMap = queryById(String.valueOf(label.getInjectionLabelId()));
        return resultMap;
    }


    /**
     * 修改注智标签信息
     * @param id
     * @param object
     * @return
     */
    @Override
    public Map<String, Object> updateByParams(String id, Object object) {
        Map<String, Object> resultMap = new HashMap<>();
        Long queryId = CommonUtil.stringToLong(id);

        JSONArray array = (JSONArray) JSONArray.parse((String)object);
        for (int i = 0; i <array.size() ; i++) {
            //目前只考虑修改值的情况  replace   只对"path":"/injectionLabelName"  格式操作
            JSONObject jsonObject = (JSONObject) array.get(i);
            String op = (String) jsonObject.get("op");
            String path = (String) jsonObject.get("path");

            if(path.indexOf("/", 1) < 0){
                Label label = injectionLabelMapper.selectByPrimaryKey(queryId);
                if(null==label){
                    throw new SystemException("对应注智标签信息不存在!");
                }
                path = path.substring(1);
                JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(label));
                json.put(path,jsonObject.getString("value"));
                Label labels = JSONObject.parseObject(json.toJSONString(), Label.class);
                injectionLabelMapper.updateByPrimaryKey(labels);
            }else {
                if (path.substring(1, path.indexOf("/", 1)).equals("injectionLabelValue")) {
                    LabelValue labelValue = injectionLabelValueMapper.selectByPrimaryKey(Long.valueOf(path.substring(path.indexOf("/", 1) + 1)));
                    if(labelValue != null) {
                        JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(labelValue));
                        json.putAll((Map<String,Object>)jsonObject.get("value"));
                        LabelValue labelValues = JSONObject.parseObject(json.toJSONString(), LabelValue.class);
                        injectionLabelValueMapper.updateByPrimaryKey(labelValues);
                    }else {
                        throw new SystemException("对应注智标签值不存在");
                    }
                }
                else if (path.substring(1, path.indexOf("/", 1)).equals("injectionLabelGrpMbr")) {
                    LabelGrpMbr labelGrpMbr = injectionLabelGrpMbrMapper.selectByPrimaryKey(Long.valueOf(path.substring(path.indexOf("/", 1) + 1)));
                    if(labelGrpMbr != null) {
                        JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(labelGrpMbr));
                        json.putAll((Map<String,Object>)jsonObject.get("value"));
                        LabelGrpMbr labelGrpMbrs = JSONObject.parseObject(json.toJSONString(), LabelGrpMbr.class);
                        injectionLabelGrpMbrMapper.updateByPrimaryKey(labelGrpMbrs);
                    }else {
                        throw new SystemException("对应注智标签组不存在");
                    }
                }
            }
        }

        resultMap = queryById(id);
        return resultMap;
    }

    /**
     * 删除注智标签信息
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> deleteById(String id) {
        Map<String, Object> resultMap = new HashMap<>();
        long queryId= CommonUtil.stringToLong(id);
        int i = injectionLabelMapper.deleteByPrimaryKey(queryId);
        if(i == 0) {
            throw new SystemException("对应注智标签不存在");
        }else {
            injectionLabelValueMapper.deleteByLabelId(queryId);

            List<LabelGrpMbr> labelGrpMbrList = injectionLabelGrpMbrMapper.findListByLabelId(queryId);
            if(labelGrpMbrList !=null) {
                for(LabelGrpMbr labelGrpMbr : labelGrpMbrList) {
                    injectionLabelGrpMbrMapper.deleteByPrimaryKey(labelGrpMbr.getGrpMbrId());
                }
            }
        }
        return resultMap;
    }


    /**
     * 查询注智标签信息列表
     * @param map
     * @return
     */
    @Override
    public Map<String, Object> queryListByMap(Map<String, Object> map) {
        Map<String, Object> resultMap = new HashMap<>();
        List<InjectionLabel> injectionLabelList = new ArrayList<>();
        Label label = new Label();
        Long grpId = 0L;
        CommonUtil.setPage(map);

        if(StringUtils.isNotBlank((String) map.get("injectionLabelId"))){
            label.setInjectionLabelId(Long.valueOf((String)map.get("injectionLabelId")));
        }
        if(StringUtils.isNotBlank((String) map.get("injectionLabelCode"))){
            label.setInjectionLabelCode((String)map.get("injectionLabelCode"));
        }
        if(StringUtils.isNotBlank((String) map.get("injectionLabelName"))){
            label.setInjectionLabelName((String)map.get("injectionLabelName"));
        }
        if(StringUtils.isNotBlank((String) map.get("injectionLabelDesc"))){
            label.setInjectionLabelDesc((String)map.get("injectionLabelDesc"));
        }
        if(StringUtils.isNotBlank((String) map.get("labelType"))){
            label.setLabelType((String)map.get("labelType"));
        }
        if(StringUtils.isNotBlank((String) map.get("labelValueType"))){
            label.setLabelValueType((String)map.get("labelValueType"));
        }
        if(StringUtils.isNotBlank((String) map.get("labelDataType"))){
            label.setLabelDataType((String)map.get("labelDataType"));
        }
        if(StringUtils.isNotBlank((String) map.get("statusCd"))){
            label.setStatusCd((String)map.get("statusCd"));
        }
        if(StringUtils.isNotBlank((String) map.get("grpId"))){
            grpId = Long.valueOf((String)map.get("grpId"));
        }

        List<Label> labelList = injectionLabelMapper.findByLabel(label);
        if(labelList != null && grpId != 0L) {
            for (Label labels : labelList) {
                List<LabelGrpMbr> labelGrpMbrList = injectionLabelGrpMbrMapper.findListByLabelId(labels.getInjectionLabelId());
                if(labelGrpMbrList != null) {
                    List<Long> list = new ArrayList<>();
                    for(LabelGrpMbr labelGrpMbr : labelGrpMbrList) {
                        list.add(labelGrpMbr.getGrpId());
                    }
                    if(!list.contains(grpId)) {
                        labelList = null;
                    }
                }
            }
        }

        if(labelList != null) {
            for (Label labelOne : labelList) {
                InjectionLabel injectionLabel = BeanUtil.create(labelOne,new InjectionLabel());
                injectionLabel.setId(String.valueOf(labelOne.getInjectionLabelId()));
                injectionLabel.setHref("/injectionLabel/" + labelOne.getInjectionLabelId());
                if(null != label.getStatusDate()){
                    injectionLabel.setStatusDate(DateUtil.getDatetime(labelOne.getStatusDate()));
                }
                injectionLabelList.add(injectionLabel);
            }
        }

        Page pageInfo = new Page(new PageInfo(labelList));
        resultMap.put("params", injectionLabelList);
        resultMap.put("size",String.valueOf(pageInfo.getTotal()));
        return resultMap;
    }
}
