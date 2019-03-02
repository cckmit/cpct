package com.zjtelcom.cpct.dao.channel;



import com.zjtelcom.cpct.domain.channel.Organization;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OrganizationMapper {
    int deleteByPrimaryKey(Long orgId);

    int insert(Organization record);

    Organization selectByPrimaryKey(Long orgId);

    List<Organization> selectAll();

    int updateByPrimaryKey(Organization record);
}