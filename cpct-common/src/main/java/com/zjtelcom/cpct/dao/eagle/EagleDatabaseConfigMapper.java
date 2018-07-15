package com.zjtelcom.cpct.dao.eagle;


import com.zjtelcom.cpct.model.EagleDatabaseConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface EagleDatabaseConfigMapper {
    int deleteByPrimaryKey(Integer dbConfRowId);

    void deleteAll();

    int insert(EagleDatabaseConfig record);

    int insertBatch(@Param("record")
                            List<EagleDatabaseConfig> record);

    int insertSelective(EagleDatabaseConfig record);

    EagleDatabaseConfig selectByPrimaryKey(Integer dbConfRowId);

    List<EagleDatabaseConfig> queryAll();

    int updateByPrimaryKeySelective(EagleDatabaseConfig record);

    int updateByPrimaryKey(EagleDatabaseConfig record);
}