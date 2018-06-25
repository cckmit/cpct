package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.Label;

import java.util.List;

public interface InjectionLabelMapper {
    int deleteByPrimaryKey(Long injectionLabelId);

    int insert(Label record);

    Label selectByPrimaryKey(Long injectionLabelId);

    List<Label> selectAll();

    int updateByPrimaryKey(Label record);
}