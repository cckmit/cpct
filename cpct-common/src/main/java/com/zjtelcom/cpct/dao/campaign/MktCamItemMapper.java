package com.zjtelcom.cpct.dao.campaign;



import com.zjtelcom.cpct.domain.campaign.MktCamItem;

import java.util.List;

public interface MktCamItemMapper {
    int deleteByPrimaryKey(Long mktCamItemId);

    int insert(MktCamItem record);

    MktCamItem selectByPrimaryKey(Long mktCamItemId);

    List<MktCamItem> selectAll();

    int updateByPrimaryKey(MktCamItem record);
}