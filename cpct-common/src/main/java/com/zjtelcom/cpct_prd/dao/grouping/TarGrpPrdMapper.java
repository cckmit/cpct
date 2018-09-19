package com.zjtelcom.cpct_prd.dao.grouping;

import com.zjtelcom.cpct.dto.grouping.TarGrp;
import com.zjtelcom.cpct.dto.grouping.TarGrpDetail;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TarGrpPrdMapper {
    int deleteByPrimaryKey(Long tarGrpId);

    int insert(TarGrpDetail record);

    TarGrp selectByPrimaryKey(Long tarGrpId);

    List<TarGrpDetail> selectAll();

    int updateByPrimaryKey(TarGrpDetail record);

    int createTarGrp(TarGrp tarGrp);

    int modTarGrp(TarGrp record);

    int delTarGrp(TarGrp tarGrp);

}