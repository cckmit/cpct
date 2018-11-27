package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.OfferRestrict;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OfferRestrictMapper {
    int deleteByPrimaryKey(Long offerRestrictId);

    int insert(OfferRestrict record);

    OfferRestrict selectByPrimaryKey(Long offerRestrictId);

    List<OfferRestrict> selectAll();

    int updateByPrimaryKey(OfferRestrict record);

    OfferRestrict selectByOfferId(@Param("offerId") Long offerId,@Param("type")String type);
}