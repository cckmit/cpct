package com.zjtelcom.cpct.open.serviceImpl.mktStrategy;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.dao.strategy.MktStrategyMapper;
import com.zjtelcom.cpct.dto.strategy.MktStrategy;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.common.CommonUtil;
import com.zjtelcom.cpct.open.base.service.BaseService;
import com.zjtelcom.cpct.open.entity.mktStrategy.OpenMktStrategy;
import com.zjtelcom.cpct.open.service.mktStrategy.OpenMktStrategyService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: anson
 * @CreateDate: 2018-11-02 11:07:38
 * @version: V 1.0
 * 营销委婉策略openapi相关服务
 */
@Service
@Transactional
public class OpenMktStrategyServiceImpl extends BaseService implements OpenMktStrategyService {

    @Autowired
    private MktStrategyMapper mktStrategyMapper;


    /**
     * 查询营销委婉策略信息
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> queryById(String id) {
        Map<String, Object> resultMap = new HashMap<>();
        long queryId = CommonUtil.stringToLong(id);
        MktStrategy mktStrategy = mktStrategyMapper.selectByPrimaryKey(queryId);
        if (null == mktStrategy) {
            throw new SystemException("对应营销委婉策略信息不存在!");
        }
        //转换为openapi返回规范
        OpenMktStrategy openMktStrategy=BeanUtil.create(mktStrategy,new OpenMktStrategy());
        //设置id  和href  转换时间为对应格式

        if(null!=mktStrategy.getStatusDate()){
            openMktStrategy.setStatusDate(DateUtil.getDatetime(mktStrategy.getStatusDate()));
        }
        resultMap.put("params", openMktStrategy);
        return resultMap;
    }


    /**
     * 新增营销委婉策略
     *
     * @param object
     * @return
     */
    @Override
    public Map<String, Object> addByObject(Object object) {
        Map<String, Object> resultMap = new HashMap<>();
        MktStrategy mktStrategy = (MktStrategy) object;
        mktStrategyMapper.insert(mktStrategy);
        MktStrategy result = mktStrategyMapper.selectByPrimaryKey(mktStrategy.getStrategyId());
        OpenMktStrategy openMktStrategy=BeanUtil.create(result,new OpenMktStrategy());
        //设置id  和href  转换时间为对应格式

        if(null!=result.getStatusDate()){
            openMktStrategy.setStatusDate(DateUtil.getDatetime(result.getStatusDate()));
        }
        resultMap.put("params", openMktStrategy);
        return resultMap;
    }


    /**
     * 修改营销委婉策略信息
     * @param id
     * @param object
     * @return
     */
    @Override
    public Map<String, Object> updateByParams(String id, Object object){
        Map<String, Object> resultMap = new HashMap<>();
        Long queryId = CommonUtil.stringToLong(id);
        MktStrategy mktStrategy = mktStrategyMapper.selectByPrimaryKey(queryId);
        if (null == mktStrategy) {
            throw new SystemException("对应触点渠道信息不存在!");
        }
        JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(mktStrategy));
        //开始判断需要修改的字段  params应该是一个jsonArray
        JSONArray array = (JSONArray) JSONArray.parse((String)object);
        for (int i = 0; i <array.size() ; i++) {
            //目前只考虑修改值的情况  replace   只对"path":"/injectionLabelName"  格式操作
            JSONObject jsonObject = (JSONObject) array.get(i);
            String path = ((String) jsonObject.get("path")).substring(1);
            json.put(path,jsonObject.getString("value"));
        }
        //赋值以后转为对象 再去更新
        MktStrategy change=JSONObject.parseObject(json.toJSONString(),MktStrategy.class);
        mktStrategyMapper.updateByPrimaryKey(change);
        json.put("id",change.getStrategyId().toString());
        json.put("href","/mktStrategy/" + change.getStrategyId().toString());
        if(change.getStatusDate()!=null){
            json.put("statusDate",DateUtil.getDatetime(change.getStatusDate()));
        }
        //转为集团返回规范
        OpenMktStrategy openMktStrategy=JSONObject.parseObject(json.toJSONString(),OpenMktStrategy.class);
        resultMap.put("params", openMktStrategy);
        return resultMap;
    }

    /**
     * 删除营销委婉策略信息
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> deleteById(String id) {
        Map<String, Object> resultMap = new HashMap<>();
        long queryId = CommonUtil.stringToLong(id);
        mktStrategyMapper.deleteByPrimaryKey(queryId);
        return resultMap;
    }


    /**
     * 查询营销委婉策略信息列表
     * @param map
     * @return
     */
    @Override
    public Map<String, Object> queryListByMap(Map<String, Object> map) {
        Map<String, Object> resultMap = new HashMap<>();
        MktStrategy mktStrategy = new MktStrategy();
        CommonUtil.setPage(map);
        List<MktStrategy> list = mktStrategyMapper.queryList(mktStrategy);
        if(list.isEmpty()){
            throw new SystemException("对应营销维挽策略信息不存在！");
        }
        //转化为集团返回规范 设置id  href  转换时间格式
        List<OpenMktStrategy> returnList=new ArrayList<>();
        for(MktStrategy m:list){
            OpenMktStrategy openMktStrategy=BeanUtil.create(m,new OpenMktStrategy());
            if(null!=mktStrategy.getStatusDate()){
                openMktStrategy.setStatusDate(DateUtil.getDatetime(mktStrategy.getStatusDate()));
            }
            returnList.add(openMktStrategy);
        }
        Page pageInfo = new Page(new PageInfo(list));
        resultMap.put("params", returnList);
        resultMap.put("size",String.valueOf(pageInfo.getTotal()));
        return resultMap;
    }




}
