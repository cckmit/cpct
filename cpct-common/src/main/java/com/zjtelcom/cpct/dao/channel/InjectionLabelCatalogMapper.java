package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.LabelCatalog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InjectionLabelCatalogMapper {
    int deleteByPrimaryKey(Long catalogId);

    int insert(LabelCatalog record);

    LabelCatalog selectByPrimaryKey(Long catalogId);

    List<LabelCatalog> selectAll();

    List<LabelCatalog> findByLevelId(@Param("level")Long level);

    List<LabelCatalog> findByParentId(@Param("parentId")Long parentId);

    int updateByPrimaryKey(LabelCatalog record);
}