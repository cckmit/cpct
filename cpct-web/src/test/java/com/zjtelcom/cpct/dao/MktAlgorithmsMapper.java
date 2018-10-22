package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.MktAlgorithms;
import java.util.List;

public interface MktAlgorithmsMapper {
    int deleteByPrimaryKey(Long algoId);

    int insert(MktAlgorithms record);

    MktAlgorithms selectByPrimaryKey(Long algoId);

    List<MktAlgorithms> selectAll();

    int updateByPrimaryKey(MktAlgorithms record);
}