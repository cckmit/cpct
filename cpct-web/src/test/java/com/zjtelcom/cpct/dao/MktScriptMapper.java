package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.MktScript;
import java.util.List;

public interface MktScriptMapper {
    int deleteByPrimaryKey(Long scriptId);

    int insert(MktScript record);

    MktScript selectByPrimaryKey(Long scriptId);

    List<MktScript> selectAll();

    int updateByPrimaryKey(MktScript record);
}