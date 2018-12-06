package com.zjtelcom.cpct_prod.dao.offer;

import com.zjtelcom.cpct.domain.channel.MktResource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktResourceProdMapper {

    int deleteByPrimaryKey(Long mktResId);

    int insert(MktResource record);

    MktResource selectByPrimaryKey(Long mktResId);

    List<MktResource> selectAll();

    int updateByPrimaryKey(MktResource record);

    List<MktResource> selectByResourceName(@Param("mktResName") String mktResName);
}
