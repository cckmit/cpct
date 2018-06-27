package com.zjtelcom.cpct.dao.channel;

import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.LabelValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface InjectionLabelValueMapper {
    int deleteByPrimaryKey(Long labelValueId);

    int insert(LabelValue record);

    LabelValue selectByPrimaryKey(Long labelValueId);

    List<LabelValue> selectAll();

    int updateByPrimaryKey(LabelValue record);

    int deleteAll();

    int insertBatch(@Param("record")List<LabelValue> record);
}