package com.zjtelcom.cpct.open.serviceImpl.tarGrp;

import com.zjtelcom.cpct.dao.grouping.TarGrpMapper;
import com.zjtelcom.cpct.dto.grouping.TarGrp;
import com.zjtelcom.cpct.dto.grouping.TarGrpDetail;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.common.CommonUtil;
import com.zjtelcom.cpct.open.base.service.BaseService;
import com.zjtelcom.cpct.open.entity.tarGrp.OpenTarGrp;
import com.zjtelcom.cpct.open.service.tarGrp.OpenTarGrpService;
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
 * @CreateDate: 2018-11-05 12:20:59
 * @version: V 1.0
 * 试算目标分群openapi相关服务
 */
@Service
@Transactional
public class OpenTarGrpServiceImpl extends BaseService implements OpenTarGrpService {

    @Autowired
    private TarGrpMapper tarGrpMapper;


    /**
     * 查询试算目标分群信息
     *
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> queryById(String id) {
        Map<String, Object> resultMap = new HashMap<>();
        long queryId = CommonUtil.stringToLong(id);
        TarGrp tarGrp = tarGrpMapper.selectByPrimaryKey(queryId);
        if (null == tarGrp) {
            throw new SystemException("对应试算目标分群信息不存在!");
        }
        //转换为openapi返回规范
        OpenTarGrp openTarGrp = BeanUtil.create(tarGrp, new OpenTarGrp());
        //设置id  和href  转换时间为对应格式
        //openTarGrp.setId(id);
        //openTarGrp.setHref("/tarGrp/" + id);
        if (null != tarGrp.getStatusDate()) {
            openTarGrp.setStatusDate(DateUtil.getDatetime(tarGrp.getStatusDate()));
        }
        resultMap.put("params", openTarGrp);
        return resultMap;
    }


    /**
     * 新增试算目标分群
     *
     * @param object
     * @return
     */
    @Override
    public Map<String, Object> addByObject(Object object) {
        Map<String, Object> resultMap = new HashMap<>();
        TarGrpDetail tarGrp = (TarGrpDetail) object;
        tarGrpMapper.insert(tarGrp);
        TarGrp result = tarGrpMapper.selectByPrimaryKey(tarGrp.getTarGrpId());
        OpenTarGrp openTarGrp = BeanUtil.create(result, new OpenTarGrp());
        //设置id  和href  转换时间为对应格式
        //openTarGrp.setId(result.getTarGrpId().toString());
        //openTarGrp.setHref("/tarGrp/" + result.getTarGrpId().toString());
        if (null != result.getStatusDate()) {
            openTarGrp.setStatusDate(DateUtil.getDatetime(result.getStatusDate()));
        }
        resultMap.put("params", result);
        return resultMap;
    }


    /**
     * 修改试算目标分群信息
     *
     * @param id
     * @param object
     * @return
     */
    @Override
    public Map<String, Object> updateByParams(String id, Object object) {
        Map<String, Object> resultMap = new HashMap<>();
        Long queryId = CommonUtil.stringToLong(id);
        TarGrp tarGrp = tarGrpMapper.selectByPrimaryKey(queryId);
        if (null == tarGrp) {
            throw new SystemException("对应试算目标分群信息不存在!");
        }
        JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(tarGrp));
        JSONArray array = (JSONArray) JSONArray.parse((String) object);
        for (int i = 0; i < array.size(); i++) {
            //目前只考虑修改值的情况  replace   只对"path":"/injectionLabelName"  格式操作
            JSONObject jsonObject = (JSONObject) array.get(i);
            String path = ((String) jsonObject.get("path")).substring(1);
            json.put(path, jsonObject.getString("value"));
        }
        //赋值以后转为对象 再去更新
        TarGrpDetail change = JSONObject.parseObject(json.toJSONString(), TarGrpDetail.class);
        tarGrpMapper.updateByPrimaryKey(change);
        json.put("id", change.getTarGrpId().toString());
        json.put("href", "/tarGrp/" + change.getTarGrpId().toString());
        if (change.getStatusDate() != null) {
            json.put("statusDate", DateUtil.getDatetime(change.getStatusDate()));
        }
        //转为集团返回规范
        OpenTarGrp openTarGrp = JSONObject.parseObject(json.toJSONString(), OpenTarGrp.class);
        resultMap.put("params", openTarGrp);
        return resultMap;
    }

    /**
     * 删除试算目标分群信息
     *
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> deleteById(String id) {
        Map<String, Object> resultMap = new HashMap<>();
        long queryId = CommonUtil.stringToLong(id);
        tarGrpMapper.deleteByPrimaryKey(queryId);
        return resultMap;
    }


    /**
     * 查询试算目标分群信息列表
     * @param map
     * @return
     */
    @Override
    public Map<String, Object> queryListByMap(Map<String, Object> map) {
        Map<String, Object> resultMap = new HashMap<>();
        TarGrp tarGrp = new TarGrp();
        CommonUtil.setPage(map);
        List<TarGrp> list = tarGrpMapper.queryList(tarGrp);
        if (list.isEmpty()) {
            throw new SystemException("对应试算目标分群信息不存在！");
        }
        //转化为集团返回规范 设置id  href  转换时间格式
        List<OpenTarGrp> returnList = new ArrayList<>();
        for (TarGrp m : list) {
            OpenTarGrp openTarGrp = BeanUtil.create(m, new OpenTarGrp());
            //openTarGrp.setId(m.getTarGrpId().toString());
            //openTarGrp.setHref("/tarGrp/" + m.getTarGrpId().toString());
            if (null != tarGrp.getStatusDate()) {
                openTarGrp.setStatusDate(DateUtil.getDatetime(tarGrp.getStatusDate()));
            }
            returnList.add(openTarGrp);
        }
        Page pageInfo = new Page(new PageInfo(list));
        resultMap.put("params", returnList);
        resultMap.put("size", String.valueOf(pageInfo.getTotal()));
        return resultMap;
    }
}
