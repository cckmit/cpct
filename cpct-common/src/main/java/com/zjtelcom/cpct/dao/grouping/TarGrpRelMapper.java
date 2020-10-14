package com.zjtelcom.cpct.dao.grouping;

import com.zjtelcom.cpct.domain.grouping.TarGrpRel;
import com.zjtelcom.cpct.dto.grouping.TarGrp;
import com.zjtelcom.cpct.dto.grouping.TarGrpDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface TarGrpRelMapper {

    int insert(TarGrpRel tarGrpRel);

    int updateTarGrpRel(TarGrpRel tarGrpRel);

    TarGrpRel selectByPrimaryKey(Long id);

    int delTarGrpRel(Long id);

}