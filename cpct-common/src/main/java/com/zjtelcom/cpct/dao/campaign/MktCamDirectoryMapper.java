package com.zjtelcom.cpct.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCamDirectoryDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktCamDirectoryMapper {
    int deleteByPrimaryKey(Long mktCamDirectoryId);

    int insert(MktCamDirectoryDO mktCamDirectoryDO);

    MktCamDirectoryDO selectByPrimaryKey(Long mktCamDirectoryId);

    List<MktCamDirectoryDO> selectByParentId(Long parentId);

    List<MktCamDirectoryDO> selectAll();

    int updateByPrimaryKey(MktCamDirectoryDO mktCamDirectoryDO);
}