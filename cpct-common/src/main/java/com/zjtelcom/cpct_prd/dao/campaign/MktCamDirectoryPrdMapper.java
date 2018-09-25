package com.zjtelcom.cpct_prd.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCamDirectoryDO;

import java.util.List;

public interface MktCamDirectoryPrdMapper {
    int deleteByPrimaryKey(Long mktCamDirectoryId);

    int insert(MktCamDirectoryDO mktCamDirectoryDO);

    MktCamDirectoryDO selectByPrimaryKey(Long mktCamDirectoryId);

    List<MktCamDirectoryDO> selectByParentId(Long parentId);

    List<MktCamDirectoryDO> selectAll();

    int updateByPrimaryKey(MktCamDirectoryDO mktCamDirectoryDO);
}