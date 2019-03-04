package com.zjtelcom.cpct_prod.dao.offer;



import com.zjtelcom.cpct.domain.channel.OfferProdRel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface OfferProdRelMapper {
    int deleteByPrimaryKey(Long offerProdRelId);

    int insert(OfferProdRel record);

    OfferProdRel selectByPrimaryKey(Long offerProdRelId);

    List<OfferProdRel> selectAll();

    int updateByPrimaryKey(OfferProdRel record);
}