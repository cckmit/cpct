package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.OfferResRel;

import java.util.List;

public interface OfferResRelMapper {
    int deleteByPrimaryKey(Long offerResRelId);

    int insert(OfferResRel record);

    OfferResRel selectByPrimaryKey(Long offerResRelId);

    List<OfferResRel> selectAll();

    int updateByPrimaryKey(OfferResRel record);
}