package com.zjtelcom.cpct.dubbo.service;

import com.zjtelcom.cpct.dubbo.model.LabelCatalog;
import com.zjtelcom.cpct.dubbo.model.LabelCatalogModel;

import java.util.Map;

public interface CatalogService {

    Map<String,Object> syncLabelCatalogList(LabelCatalogModel req);

    Map<String,Object> syncLabelCatalog(LabelCatalog req);


}
