package com.zjtelcom.cpct.service.campaign;

import java.util.Map;

public interface MktCamResourceQRCodeService {
    //海报获取二维码元素 参数：规则id,渠道id
    Map<String,Object> generatePoster(Map<String,Object> params);

    //保存海报url到渠道属性表中
    Map<String,Object> savePostToChanlAttr(Map<String,Object> params);

}
