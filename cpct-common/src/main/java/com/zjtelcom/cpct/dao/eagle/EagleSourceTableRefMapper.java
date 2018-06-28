package com.zjtelcom.cpct.dao.eagle;


import com.zjtelcom.cpct.model.EagleSourceTableRef;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface EagleSourceTableRefMapper {
    int deleteByPrimaryKey(Integer ctasTwoTableRefRowId);

    void deleteAll();

    int insert(EagleSourceTableRef record);

    int insertBatch(@Param("record")
                            List<EagleSourceTableRef> record);

    int insertSelective(EagleSourceTableRef record);

    EagleSourceTableRef selectByPrimaryKey(Integer ctasTwoTableRefRowId);

    List<EagleSourceTableRef> queryAll();

    List<EagleSourceTableRef> queryByMainFlagAndFitDomain(@Param("slaveTableId")
                                                                  String slaveTableId, @Param("masterTableId")
                                                                  String masterTableId, @Param("fitDomain")
                                                                  String fitDomain);

    EagleSourceTableRef queryByMasterTable(@Param("tableName")
                                                   String tableName, @Param("dbConfId")
                                                   String dbConfId);

    int updateByPrimaryKeySelective(EagleSourceTableRef record);

    int updateByPrimaryKey(EagleSourceTableRef record);
}