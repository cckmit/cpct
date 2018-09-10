package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.OfferCatalogLocation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OfferCatalogLocationMapper {
    int deleteByPrimaryKey(Long offerCatLocId);

    int insert(OfferCatalogLocation record);

    OfferCatalogLocation selectByPrimaryKey(Long offerCatLocId);

    List<OfferCatalogLocation> selectAll();

    List<OfferCatalogLocation> selectByCatalogItemId(@Param("catalogItemId")Long catalogItemId);

    int updateByPrimaryKey(OfferCatalogLocation record);
}