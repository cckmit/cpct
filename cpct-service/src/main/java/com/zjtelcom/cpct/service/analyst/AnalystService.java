package com.zjtelcom.cpct.service.analyst;

import java.util.HashMap;
import java.util.Map;

public interface AnalystService {
    Map<String,Object> statisticalAnalysts(HashMap<String, Object> params);

    Map<String,Object> getCustomByLabel(String name);

}
