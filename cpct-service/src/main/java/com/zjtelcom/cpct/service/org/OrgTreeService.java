package com.zjtelcom.cpct.service.org;

import com.zjtelcom.cpct.domain.org.OrgTree;

import java.util.List;
import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/9/30
 * @Description:
 */
public interface OrgTreeService {

    /**
     * 按条件查找
     * @param t
     * @return
     */
    List<OrgTree> queryList(OrgTree t);

    /**
     * 批量添加数据
     * @return
     */
    int addBatchData(List<OrgTree> list);

    /**
     * 从ftp服务器获取dat文件的数据
     */
    List<OrgTree> getDataByFtp(String path);


    /**
     * 通过父级菜单查询子菜单
     * @return
     */
    Map<String,Object> selectBySumAreaId(Map<String, Object> params);

    Map<String,Object> selectByAreaId(Map<String, Object> params);

    Map<String,Object> fuzzyQuery(Map<String,Object> params);

    Long getLandIdBySession();

}
