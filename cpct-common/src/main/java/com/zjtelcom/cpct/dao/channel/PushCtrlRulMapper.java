package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.PushCtrlRul;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface PushCtrlRulMapper {
    int deleteByPrimaryKey(Long pushCtrlRulId);

    int insert(PushCtrlRul record);

    PushCtrlRul selectByPrimaryKey(Long pushCtrlRulId);

    List<PushCtrlRul> selectAll();

    int updateByPrimaryKey(PushCtrlRul record);
}