package com.zjtelcom.cpct_prod.dao.offer;



import com.zjtelcom.cpct.domain.channel.CatalogItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CatalogItemProdMapper {
    int deleteByPrimaryKey(Long catalogItemId);

    int insert(CatalogItem record);

    CatalogItem selectByPrimaryKey(Long catalogItemId);

    List<CatalogItem> selectAll();

    List<CatalogItem> selectByCatalog(@Param("catalogId") Long catalogId);

    List<CatalogItem> selectByParentId(@Param("parentId") Long parentId);

    int updateByPrimaryKey(CatalogItem record);
}