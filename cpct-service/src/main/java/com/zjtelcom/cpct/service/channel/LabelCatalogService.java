package com.zjtelcom.cpct.service.channel;

import com.zjtelcom.cpct.domain.channel.LabelCatalog;
import com.zjtelcom.cpct.dto.channel.LabelAddVO;

import java.util.List;
import java.util.Map;

public interface LabelCatalogService  {

    Map<String,Object> batchAdd(List<String> nameList,Long parentId,Long level);

    Map<String,Object> addLabelCatalog( LabelCatalog addVO);

    Map<String,Object> listLabelCatalog();

    Map<String,Object> listLabelByCatalogId(Long catalogId);



}
