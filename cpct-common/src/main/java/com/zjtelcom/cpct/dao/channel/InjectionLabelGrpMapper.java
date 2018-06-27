package com.zjtelcom.cpct.dao.channel;

import com.zjtelcom.cpct.domain.channel.LabelGrp;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface InjectionLabelGrpMapper {
    int deleteByPrimaryKey(Long grpId);

    int insert(LabelGrp record);

    LabelGrp selectByPrimaryKey(Long grpId);

    List<LabelGrp> selectAll();

    int updateByPrimaryKey(LabelGrp record);
}