package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.LabelSaturation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabelSaturationMapper {
    int deleteByPrimaryKey(Long labelSaturationId);

    int insert(LabelSaturation record);

    int insertBatch(List<LabelSaturation> labelSaturationList);

    LabelSaturation selectByPrimaryKey(Long labelSaturationId);

    List<LabelSaturation> selectAll();

    int updateByPrimaryKey(LabelSaturation record);
}