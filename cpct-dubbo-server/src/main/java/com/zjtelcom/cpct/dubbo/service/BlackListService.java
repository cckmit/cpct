package com.zjtelcom.cpct.dubbo.service;

import java.util.List;
import java.util.Map;

public interface BlackListService {
    Map<String,Object> deleteBlackList(List<String> phoneNumsDeleted);
}
