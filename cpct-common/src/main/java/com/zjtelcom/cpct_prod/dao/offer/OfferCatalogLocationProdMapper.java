package com.zjtelcom.cpct_prod.dao.offer;


import com.zjtelcom.cpct.domain.channel.OfferCatalogLocation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OfferCatalogLocationProdMapper {
    int deleteByPrimaryKey(Long offerCatLocId);

    int insert(OfferCatalogLocation record);

    OfferCatalogLocation selectByPrimaryKey(Long offerCatLocId);

    List<OfferCatalogLocation> selectAll();

    List<OfferCatalogLocation> selectByCatalogItemId(@Param("catalogItemId") Long catalogItemId);

    int updateByPrimaryKey(OfferCatalogLocation record);
}