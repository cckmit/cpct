package com.zjtelcom.cpct.dubbo.out;

import java.util.List;
import java.util.Map;

public interface BlackListService {
    Map<String,Object> addBlackList(List<Map<String, Object>> blackListContent);
    Map<String,Object> deleteBlackList(List<String> phoneNumsDeleted);
    Map<String,Object> getBlackListById(List<String> phoneNums);
    Map<String,Object> getAllBlackList();

}
