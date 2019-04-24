package com.zjtelcom.cpct.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktAlgorithms;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktAlgorithmsMapper {

    List<MktAlgorithms> selectAll();

    MktAlgorithms selectByPrimaryKey(@Param("algoId") Long algoId);

    int saveMktAlgorithms(MktAlgorithms mktAlgorithms);

    int updateMktAlgorithms(MktAlgorithms mktAlgorithms);

    int deleteByPrimaryKey(@Param("algoId") Long algoId);

    List<MktAlgorithms> selectByMktAlgorithms(MktAlgorithms mktAlgorithms);
}
