package com.zjtelcom.cpct.dao.channel;



import com.zjtelcom.cpct.domain.channel.MktCamResource;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktCamResourceMapper {
    int deleteByPrimaryKey(Long mktCamResourceId);

    int insert(MktCamResource record);

    MktCamResource selectByPrimaryKey(Long mktCamResourceId);

    List<MktCamResource> selectAll();

    int updateByPrimaryKey(MktCamResource record);
}