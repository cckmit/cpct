package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.LabelCatalog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface InjectionLabelCatalogMapper {
    int deleteByPrimaryKey(Long catalogId);

    int insert(LabelCatalog record);

    LabelCatalog selectByPrimaryKey(Long catalogId);

    List<LabelCatalog> selectAll();

    List<LabelCatalog> findByLevelId(@Param("level") Long level);

    List<LabelCatalog> findByParentId(@Param("parentId") String parentId);

    int updateByPrimaryKey(LabelCatalog record);

    LabelCatalog findByCodeAndLevel(@Param("catalogCode") String catalogCode, @Param("levelId") Long levelId);
}