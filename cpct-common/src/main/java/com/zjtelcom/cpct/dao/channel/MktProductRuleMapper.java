package com.zjtelcom.cpct.dao.channel;



import com.zjtelcom.cpct.domain.channel.MktProductRule;

import java.util.List;

public interface MktProductRuleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MktProductRule record);

    MktProductRule selectByPrimaryKey(Long id);

    List<MktProductRule> selectAll();

    int updateByPrimaryKey(MktProductRule record);
}