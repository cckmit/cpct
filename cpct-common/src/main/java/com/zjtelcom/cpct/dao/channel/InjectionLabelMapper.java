package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.Label;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface InjectionLabelMapper {
    int deleteByPrimaryKey(Long injectionLabelId);

    int insert(Label record);

    Label selectByPrimaryKey(Long injectionLabelId);

    List<Label> selectAll();

    int updateByPrimaryKey(Label record);
}