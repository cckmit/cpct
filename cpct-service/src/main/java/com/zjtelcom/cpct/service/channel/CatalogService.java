package com.zjtelcom.cpct.service.channel;

import java.util.Map;

public interface CatalogService {

    Map<String,Object> listProductTree();

    Map<String,Object> listOfferByCatalogId(Long catalogId,String productName,Integer page,Integer pageSize);

    Map<String,Object> listCatalogItemTree();

}
