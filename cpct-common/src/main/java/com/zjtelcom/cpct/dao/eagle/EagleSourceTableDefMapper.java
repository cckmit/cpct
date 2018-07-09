package com.zjtelcom.cpct.dao.eagle;


import com.zjtelcom.cpct.model.EagleSourceTableDef;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface EagleSourceTableDefMapper {
    int deleteByPrimaryKey(Integer ctasTableDefinitionRowId);

    void deleteAll();

    int insert(EagleSourceTableDef record);

    int insertBatch(@Param("record")
                            List<EagleSourceTableDef> record);

    int insertSelective(EagleSourceTableDef record);

    EagleSourceTableDef selectByPrimaryKey(Integer ctasTableDefinitionRowId);

    List<EagleSourceTableDef> queryAll();

    EagleSourceTableDef queryByTableNameAndDb(@Param("tableName")
                                                      String tableName, @Param("dbConfId")
                                                      String dbConf);

    int updateByPrimaryKeySelective(EagleSourceTableDef record);

    int updateByPrimaryKey(EagleSourceTableDef record);
}