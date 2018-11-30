package com.zjtelcom.cpct.dao.org;

import com.zjtelcom.cpct.domain.org.OrgTree;
import com.zjtelcom.cpct.domain.org.OrgTreeDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: anson
 * @Date: 2018/9/30
 * @Description:
 */
public interface OrgTreeMapper {

    int deleteByPrimaryKey(Integer areaId);

    int insert(OrgTree record);

    OrgTree selectByPrimaryKey(Integer areaId);

    List<OrgTree> selectAll();

    int updateByPrimaryKey(OrgTree record);

    int addBatch(List<OrgTree> list);

    List<OrgTree> queryList(OrgTree t);

    int deleteAll();

    List<OrgTreeDO> selectBySumAreaId(Integer sumAreaId);

    OrgTreeDO selectByAreaId(@Param("areaId")Integer areaId);

    List<OrgTreeDO> selectMenu();



}
