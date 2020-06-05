package com.zjtelcom.cpct.dao.channel;



import com.zjtelcom.cpct.dto.event.Catalog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CatalogMapper {
    int deleteByPrimaryKey(Long catalogId);

    int insert(Catalog record);

    Catalog selectByPrimaryKey(Long catalogId);

    List<Catalog> selectAll();

    int updateByPrimaryKey(Catalog record);

    Catalog selectByType(@Param("type") String type);
}