package com.zjtelcom.cpct.open.base.service;

import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/10/31
 * @Description:公共的查询方法
 */
public interface BaseDao {

    //通过主键查询
    Map<String,Object> queryById(String id);
    //新增对象
    Map<String,Object> addByObject(Object object);
    //修改对象信息
    Map<String,Object> updateByParams(String id,Object object);
    //删除对象信息
    Map<String,Object> deleteById(String id);
    //查询列表
    Map<String,Object> queryListByMap(Map<String, Object> map);


}
