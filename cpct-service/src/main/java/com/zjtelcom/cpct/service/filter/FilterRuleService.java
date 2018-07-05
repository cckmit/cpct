package com.zjtelcom.cpct.service.filter;

import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dto.user.UserList;
import com.zjtelcom.cpct.request.filter.FilterRuleReq;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * @Description 规律规则Service
 * @Author pengy
 * @Date 2018/6/21 9:45
 */
public interface FilterRuleService {

    Map<String,Object> qryFilterRule(FilterRuleReq filterRuleReq);

    Map<String,Object> delFilterRule(FilterRule filterRule);

    Map<String,Object> getFilterRule(FilterRule filterRule);

    Map<String,Object> createFilterRule(FilterRule filterRule);

    Map<String,Object> modFilterRule(FilterRule filterRule);

    Map<String,Object> qryFilterRules(FilterRuleReq filterRuleReq);

    Map<String,Object> importUserList(MultipartFile multipartFile ,Long ruleId) throws IOException;

    Map<String,Object> listUserList(UserList userList) throws IOException;

}
