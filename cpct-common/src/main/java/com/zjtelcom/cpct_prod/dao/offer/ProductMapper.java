package com.zjtelcom.cpct_prod.dao.offer;



import com.zjtelcom.cpct.domain.channel.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ProductMapper {
    int deleteByPrimaryKey(Long prodId);

    int insert(Product record);

    Product selectByPrimaryKey(Long prodId);

    List<Product> selectAll();

    int updateByPrimaryKey(Product record);

    List<Product> selectByCode(@Param("code") String code);
}