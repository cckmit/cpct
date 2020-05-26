package com.zjtelcom.cpct.dao.channel;



import com.zjtelcom.cpct.domain.channel.ObjectLabelRel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ObjectLabelRelMapper {
    int deleteByPrimaryKey(Long objectLabelRelId);

    int insert(ObjectLabelRel record);

    ObjectLabelRel selectByPrimaryKey(Long objectLabelRelId);

    List<ObjectLabelRel> selectAll();

    int updateByPrimaryKey(ObjectLabelRel record);

    List<ObjectLabelRel> selectByObjId(Long objId);
}