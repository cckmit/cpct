package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.LabelGrpMbr;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface InjectionLabelGrpMbrMapper {
    int deleteByPrimaryKey(Long grpMbrId);

    int insert(LabelGrpMbr record);

    LabelGrpMbr selectByPrimaryKey(Long grpMbrId);

    LabelGrpMbr selectByLabelIdAndGrpId(@Param("labelId")Long labelId,@Param("grpId")Long grpId);

    List<LabelGrpMbr> selectAll();

    int updateByPrimaryKey(LabelGrpMbr record);
}