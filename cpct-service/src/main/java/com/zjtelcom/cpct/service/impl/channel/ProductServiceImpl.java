package com.zjtelcom.cpct.service.impl.channel;

import com.zjtelcom.cpct.dao.channel.PpmProductMapper;
import com.zjtelcom.cpct.domain.channel.PpmProduct;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl extends BaseService implements ProductService {

    @Autowired
    private PpmProductMapper productMapper;


    @Override
    public List<PpmProduct> getProductList(Long userId,String productName){
        List<PpmProduct> productList = new ArrayList<>();
        try {
            productList = productMapper.findByProductName(productName);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[op:ProductServiceImpl] fail to getProductList ", e);
        }
        return productList;
    }
}
