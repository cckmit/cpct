package com.zjtelcom.cpct.dao.system;

import com.zjtelcom.cpct.domain.SysArea;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysAreaMapper {
    int deleteByPrimaryKey(Integer areaId);

    int insert(SysArea record);

    SysArea selectByPrimaryKey(Integer areaId);

    List<SysArea> selectAll();

    List<SysArea> selectByAreaLevel(Integer areaLevel);

    List<SysArea> selectByParnetArea(Integer parentArea);

    int updateByPrimaryKey(SysArea record);
}