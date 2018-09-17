package com.zjtelcom.cpct.dao.campaign;



import com.zjtelcom.cpct.domain.campaign.MktCamItem;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktCamItemMapper {
    int deleteByPrimaryKey(Long mktCamItemId);

    int insert(MktCamItem record);

    MktCamItem selectByPrimaryKey(Long mktCamItemId);

    List<MktCamItem> selectAll();

    int updateByPrimaryKey(MktCamItem record);
}