package com.zjtelcom.cpct.dao.channel;



import com.zjtelcom.cpct.domain.channel.MktProductAttr;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktProductAttrMapper {
    int deleteByPrimaryKey(Long mktProductAttrId);

    int insert(MktProductAttr record);

    MktProductAttr selectByPrimaryKey(Long mktProductAttrId);

    List<MktProductAttr> selectAll();

    int updateByPrimaryKey(MktProductAttr record);

    List<MktProductAttr> selectByProduct(MktProductAttr record);

    List<MktProductAttr> selectByRule(Long ruleId);

}