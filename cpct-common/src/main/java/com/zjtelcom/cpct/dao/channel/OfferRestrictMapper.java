package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.OfferRestrictEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OfferRestrictMapper {
    int deleteByPrimaryKey(Long offerRestrictId);

    int insert(OfferRestrictEntity record);

    OfferRestrictEntity selectByPrimaryKey(Long offerRestrictId);

    List<OfferRestrictEntity> selectAll();

    int updateByPrimaryKey(OfferRestrictEntity record);

    List<OfferRestrictEntity> selectByOfferId(@Param("offerId") Long offerId, @Param("type")String type);

    Long selectBatchNoNum();
}