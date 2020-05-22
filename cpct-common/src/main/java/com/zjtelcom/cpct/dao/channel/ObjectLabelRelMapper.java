package com.zjtelcom.cpct.dao.channel;



import com.zjtelcom.cpct.domain.channel.ObjectLabelRel;

import java.util.List;

public interface ObjectLabelRelMapper {
    int deleteByPrimaryKey(Long objectLabelRelId);

    int insert(ObjectLabelRel record);

    ObjectLabelRel selectByPrimaryKey(Long objectLabelRelId);

    List<ObjectLabelRel> selectAll();

    int updateByPrimaryKey(ObjectLabelRel record);
}