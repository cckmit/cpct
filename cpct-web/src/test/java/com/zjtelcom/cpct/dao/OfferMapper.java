package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.Offer;

import java.util.List;

public interface OfferMapper {
    int deleteByPrimaryKey(Long offerId);

    int insert(Offer record);

    Offer selectByPrimaryKey(Long offerId);

    List<Offer> selectAll();

    int updateByPrimaryKey(Offer record);
}