package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.Label;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface InjectionLabelMapper {
    int deleteByPrimaryKey(Long injectionLabelId);

    int insert(Label record);

    Label selectByPrimaryKey(Long injectionLabelId);

    List<Label> selectAll();

    List<Label> findByParam(@Param("labelName")String labelName,@Param("fitDomain")String fitDomain);

    int updateByPrimaryKey(Label record);

    int deleteAll();

    int insertBatch(@Param("record")List<Label> record);


}