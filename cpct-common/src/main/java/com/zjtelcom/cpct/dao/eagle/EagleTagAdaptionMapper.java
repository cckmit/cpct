package com.zjtelcom.cpct.dao.eagle;


import com.zjtelcom.cpct.model.EagleTagAdaption;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface EagleTagAdaptionMapper {
    int deleteByPrimaryKey(Integer adapId);

    int insert(EagleTagAdaption record);

    int insertSelective(EagleTagAdaption record);

    EagleTagAdaption selectByPrimaryKey(Integer adapId);

    int updateByPrimaryKeySelective(EagleTagAdaption record);

    int updateByPrimaryKey(EagleTagAdaption record);

    List<EagleTagAdaption> queryAll();

    EagleTagAdaption queryByNameAndDomain(@Param("tag")
                                                  String tag, @Param("fitDomain")
                                                  String fitDomain, @Param("adapClassify")
                                                  String adapClassify);
}