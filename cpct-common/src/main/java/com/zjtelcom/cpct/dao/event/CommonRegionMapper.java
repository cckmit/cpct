package com.zjtelcom.cpct.dao.event;

import com.zjtelcom.cpct.domain.event.CommonRegion;
import com.zjtelcom.cpct.domain.event.OfferExpenseDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CommonRegionMapper {
    int deleteByPrimaryKey(Long commonRegionId);

    int insert(CommonRegion record);

    CommonRegion selectByPrimaryKey(Long commonRegionId);
    CommonRegion selectByC4RegionId(Long c4RegionId);
    List<CommonRegion> selectAll();

    int updateByPrimaryKey(CommonRegion record);

    List<OfferExpenseDO> getExpenseByOfferNbr(String offerNbr);

    List<OfferExpenseDO> getExpenseByOfferInfo(String offerNbr);



}