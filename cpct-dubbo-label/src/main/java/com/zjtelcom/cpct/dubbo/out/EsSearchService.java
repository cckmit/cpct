package com.zjtelcom.cpct.dubbo.out;

import java.util.Map;

public interface EsSearchService {

    Map<String, Object> queryCustomerByCcustId4Out(Map<String, String> param);
}
