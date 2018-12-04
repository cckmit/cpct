package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.OfferEvtSceneRel;

import java.util.List;

public interface OfferEvtSceneRelMapper {
    int deleteByPrimaryKey(Long offerSceneRelId);

    int insert(OfferEvtSceneRel record);

    OfferEvtSceneRel selectByPrimaryKey(Long offerSceneRelId);

    List<OfferEvtSceneRel> selectAll();

    int updateByPrimaryKey(OfferEvtSceneRel record);
}