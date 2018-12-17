package com.zjtelcom.cpct.open.serviceImpl.mktCpcAlgorithmsRule;

import com.zjtelcom.cpct.dao.campaign.MktCpcAlgorithmsRulMapper;
import com.zjtelcom.cpct.domain.campaign.MktCpcAlgorithmsRulDO;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.common.CommonUtil;
import com.zjtelcom.cpct.open.base.service.BaseService;
import com.zjtelcom.cpct.open.entity.mktCpcAlgorithmsRule.OpenMktCpcAlgorithmsRule;
import com.zjtelcom.cpct.open.service.mktCpcAlgorithmsRule.OpenMktCpcAlgorithmsRuleService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: anson
 * @CreateDate: 2018-11-07 15:05:02
 * @version: V 1.0
 * CPC算法规则openapi相关服务
 */
@Service
@Transactional
public class OpenMktCpcAlgorithmsRuleServiceImpl extends BaseService implements OpenMktCpcAlgorithmsRuleService {

    @Autowired
    private MktCpcAlgorithmsRulMapper mktCpcAlgorithmsRulMapper;


    /**
     * 查询CPC算法规则信息
     *
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> queryById(String id) {
        Map<String, Object> resultMap = new HashMap<>();
        long queryId = CommonUtil.stringToLong(id);
        MktCpcAlgorithmsRulDO mktCpcAlgorithmsRulDO = mktCpcAlgorithmsRulMapper.selectByPrimaryKey(queryId);
        if (null == mktCpcAlgorithmsRulDO) {
            throw new SystemException("对应CPC算法规则信息不存在!");
        }
        //转换为openapi返回规范
        OpenMktCpcAlgorithmsRule openMktCpcAlgorithmsRule = BeanUtil.create(mktCpcAlgorithmsRulDO, new OpenMktCpcAlgorithmsRule());
        //设置id  和href  转换时间为对应格式
        openMktCpcAlgorithmsRule.setId(id);
        openMktCpcAlgorithmsRule.setHref("/mktCpcAlgorithmsRul/" + id);
        if (null != mktCpcAlgorithmsRulDO.getStatusDate()) {
            openMktCpcAlgorithmsRule.setStatusDate(DateUtil.getDatetime(mktCpcAlgorithmsRulDO.getStatusDate()));
        }
        resultMap.put("params", openMktCpcAlgorithmsRule);
        return resultMap;
    }


    /**
     * 新增CPC算法规则
     *
     * @param object
     * @return
     */
    @Override
    public Map<String, Object> addByObject(Object object) {
        Map<String, Object> resultMap = new HashMap<>();
        MktCpcAlgorithmsRulDO mktCpcAlgorithmsRulDO = (MktCpcAlgorithmsRulDO) object;
        mktCpcAlgorithmsRulMapper.insert(mktCpcAlgorithmsRulDO);
        MktCpcAlgorithmsRulDO result = mktCpcAlgorithmsRulMapper.selectByPrimaryKey(mktCpcAlgorithmsRulDO.getAlgorithmsRulId());
        OpenMktCpcAlgorithmsRule openMktCpcAlgorithmsRule = BeanUtil.create(result, new OpenMktCpcAlgorithmsRule());
        //设置id  和href  转换时间为对应格式
        openMktCpcAlgorithmsRule.setId(result.getAlgorithmsRulId().toString());
        openMktCpcAlgorithmsRule.setHref("/mktCpcAlgorithmsRul/" + result.getAlgorithmsRulId().toString());
        if (null != result.getStatusDate()) {
            openMktCpcAlgorithmsRule.setStatusDate(DateUtil.getDatetime(result.getStatusDate()));
        }
        resultMap.put("params", openMktCpcAlgorithmsRule);
        return resultMap;
    }


    /**
     * 修改CPC算法规则信息
     *
     * @param id
     * @param object
     * @return
     */
    @Override
    public Map<String, Object> updateByParams(String id, Object object) {
        Map<String, Object> resultMap = new HashMap<>();
        Long queryId = CommonUtil.stringToLong(id);
        MktCpcAlgorithmsRulDO mktCpcAlgorithmsRulDO = mktCpcAlgorithmsRulMapper.selectByPrimaryKey(queryId);
        if (null == mktCpcAlgorithmsRulDO) {
            throw new SystemException("对应CPC算法规则信息不存在!");
        }
        JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(mktCpcAlgorithmsRulDO));
        JSONArray array = (JSONArray) JSONArray.parse((String) object);
        for (int i = 0; i < array.size(); i++) {
            //目前只考虑修改值的情况  replace   只对"path":"/injectionLabelName"  格式操作
            JSONObject jsonObject = (JSONObject) array.get(i);
            String path = ((String) jsonObject.get("path")).substring(1);
            json.put(path, jsonObject.getString("value"));
        }
        //赋值以后转为对象 再去更新
        MktCpcAlgorithmsRulDO change = JSONObject.parseObject(json.toJSONString(), MktCpcAlgorithmsRulDO.class);
        mktCpcAlgorithmsRulMapper.updateByPrimaryKey(change);
        json.put("id", change.getAlgorithmsRulId().toString());
        json.put("href", "/mktCpcAlgorithmsRul/" + change.getAlgorithmsRulId().toString());
        if (change.getStatusDate() != null) {
            json.put("statusDate", DateUtil.getDatetime(change.getStatusDate()));
        }
        //转为集团返回规范
        OpenMktCpcAlgorithmsRule openMktCpcAlgorithmsRule = JSONObject.parseObject(json.toJSONString(), OpenMktCpcAlgorithmsRule.class);
        resultMap.put("params", openMktCpcAlgorithmsRule);
        return resultMap;
    }

    /**
     * 删除CPC算法规则信息
     *
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> deleteById(String id) {
        Map<String, Object> resultMap = new HashMap<>();
        long queryId = CommonUtil.stringToLong(id);
        mktCpcAlgorithmsRulMapper.deleteByPrimaryKey(queryId);
        return resultMap;
    }


    /**
     * 查询CPC算法规则信息列表
     *
     * @param map
     * @return
     */
    @Override
    public Map<String, Object> queryListByMap(Map<String, Object> map) {
        Map<String, Object> resultMap = new HashMap<>();
        MktCpcAlgorithmsRulDO mktCpcAlgorithmsRulDO = new MktCpcAlgorithmsRulDO();
        CommonUtil.setPage(map);
        List<MktCpcAlgorithmsRulDO> list = mktCpcAlgorithmsRulMapper.queryList(mktCpcAlgorithmsRulDO);
        if (list.isEmpty()) {
            throw new SystemException("对应CPC算法规则信息不存在！");
        }
        //转化为集团返回规范 设置id  href  转换时间格式
        List<OpenMktCpcAlgorithmsRule> returnList = new ArrayList<>();
        for (MktCpcAlgorithmsRulDO m : list) {
            OpenMktCpcAlgorithmsRule openMktCpcAlgorithmsRule = BeanUtil.create(m, new OpenMktCpcAlgorithmsRule());
            openMktCpcAlgorithmsRule.setId(m.getAlgorithmsRulId().toString());
            openMktCpcAlgorithmsRule.setHref("/mktCpcAlgorithmsRul/" + m.getAlgorithmsRulId().toString());
            if (null != mktCpcAlgorithmsRulDO.getStatusDate()) {
                openMktCpcAlgorithmsRule.setStatusDate(DateUtil.getDatetime(mktCpcAlgorithmsRulDO.getStatusDate()));
            }
            returnList.add(openMktCpcAlgorithmsRule);
        }
        Page pageInfo = new Page(new PageInfo(list));
        resultMap.put("params", returnList);
        resultMap.put("size", String.valueOf(pageInfo.getTotal()));
        return resultMap;
    }
}
