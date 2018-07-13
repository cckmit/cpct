package com.zjtelcom.cpct.dao.system;


import com.zjtelcom.cpct.dto.system.SystemParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SystemParamMapper {


    /**
     * 系统参数查询
     * @param page
     * @return
     */
//    List<PageData> querySystemParamByPage(Page page);


    /**
     * 系统参数单个新增接口
     * @param systemParam
     * @return
     */
    int insertSystemParamOne(SystemParam systemParam);


    /**
     * 系统参数修改接口
     * @param systemParam
     * @return
     */
    int updateSystemParam(SystemParam systemParam);

    /**
     * 系统参数删除接口
     * @param systemParam
     * @return
     */
    int deleteSystemParam(SystemParam systemParam);


    int deleteByPrimaryKey(Integer paramId);

    int insert(SystemParam record);

    int insertSelective(SystemParam record);

    SystemParam selectByPrimaryKey(Integer paramId);




    int updateByPrimaryKeySelective(SystemParam record);

    int updateByPrimaryKey(SystemParam record);

    /**
     * 查询所有的系统参数
     * @return
     */
    List<SystemParam> queryAllSystemParam();
    
    SystemParam selectByParamKey(@Param("paramKey") String paramKey);


	List<SystemParam> findParamKeyIn(String paramKey);
}