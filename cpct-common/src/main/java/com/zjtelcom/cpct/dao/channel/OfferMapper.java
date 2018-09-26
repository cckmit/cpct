package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.Offer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface OfferMapper {
    int deleteByPrimaryKey(Integer offerId);

    int insert(Offer record);

    Offer selectByPrimaryKey(Integer offerId);

    List<Offer> selectAll();

    Offer selectByCamItemId(Long camItemId);

    List<Offer> findByName(@Param("offerName")String name);

    Offer selectByPrimaryKeyAndName(@Param("offerId") Integer offerId,@Param("productName")String productName);

    int updateByPrimaryKey(Offer record);
}