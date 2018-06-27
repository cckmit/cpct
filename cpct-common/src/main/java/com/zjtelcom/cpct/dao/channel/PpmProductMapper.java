package com.zjtelcom.cpct.dao.channel;



import com.zjtelcom.cpct.domain.channel.PpmProduct;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface PpmProductMapper {
    int deleteByPrimaryKey(Long productId);

    int insert(PpmProduct record);

    PpmProduct selectByPrimaryKey(Long productId);

    List<PpmProduct> selectAll();



    int insertSelective(PpmProduct record);

    int updateByPrimaryKey(PpmProduct record);

    int updateByPrimaryKeySelective(PpmProduct record);

    int updateByCodeSelective(PpmProduct record);

    List<PpmProduct> getAllProduct();

    List<PpmProduct> selectPpmProductByCode(String code);

    List<PpmProduct> selectPpmProductByids(Map item);

    List<PpmProduct> getAllProductInUse();
}