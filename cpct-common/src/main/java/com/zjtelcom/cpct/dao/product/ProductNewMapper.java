package com.zjtelcom.cpct.dao.product;



import com.zjtelcom.cpct.domain.channel.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ProductNewMapper {

    List<Product> selectByProdName(@Param("prodName") String prodName);

    List<Map<String, Object>> selectAttrSpec(@Param("prodId") Long prodId, @Param("attrName") String attrName);

    List<Map<String, Object>> selectProdAttrValue(@Param("prodAttrId") Long prodAttrId);

}