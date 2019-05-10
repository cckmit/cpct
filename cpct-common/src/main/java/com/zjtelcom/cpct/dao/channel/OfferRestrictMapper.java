package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.OfferRestrict;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OfferRestrictMapper {
    int deleteByPrimaryKey(Long offerRestrictId);

    int insert(OfferRestrict record);

    OfferRestrict selectByPrimaryKey(Long offerRestrictId);

    List<OfferRestrict> selectAll();

    int updateByPrimaryKey(OfferRestrict record);

    List<OfferRestrict> selectByOfferId(@Param("offerId") Long offerId,@Param("type")String type);

    Long selectBatchNoNum();
}