package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.Catalog;

import java.util.List;

public interface CatalogMapper {
    int deleteByPrimaryKey(Long catalogId);

    int insert(Catalog record);

    Catalog selectByPrimaryKey(Long catalogId);

    List<Catalog> selectAll();

    int updateByPrimaryKey(Catalog record);
}