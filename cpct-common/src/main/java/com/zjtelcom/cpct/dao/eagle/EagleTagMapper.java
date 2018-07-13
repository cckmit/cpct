package com.zjtelcom.cpct.dao.eagle;


import com.zjtelcom.cpct.model.EagleTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface EagleTagMapper {
    int deleteByPrimaryKey(Integer tagRowId);

    int deleteAll();

    int insert(EagleTag record);

    int insertBatch(@Param("record")
                            List<EagleTag> record);

    int insertSelective(EagleTag record);

    EagleTag selectByPrimaryKey(Integer tagRowId);

    EagleTag selectByNameAndDomain(@Param("name")
                                           String name, @Param("fitDomain")
                                           String fitDomain);

    int updateByPrimaryKeySelective(EagleTag record);

    int updateByPrimaryKey(EagleTag record);

//    List<PageData> queryTagByPage(Page page);

    List<EagleTag> queryAll();

    List<EagleTag> queryByNotExistsTabRef(@Param("record")
                                                  List<Long> tagIdList, @Param("domain")
                                                  String domain, @Param("masterTableId")
                                                  String masterTableId);
}