package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.CatalogItem;

import java.util.List;

public interface CatalogItemMapper {
    int deleteByPrimaryKey(Long catalogItemId);

    int insert(CatalogItem record);

    CatalogItem selectByPrimaryKey(Long catalogItemId);

    List<CatalogItem> selectAll();

    int updateByPrimaryKey(CatalogItem record);
}