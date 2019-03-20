package com.zjtelcom.cpct.service.filter;

import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dto.filter.FilterRuleAddVO;
import com.zjtelcom.cpct.dto.user.UserList;
import com.zjtelcom.cpct.request.filter.FilterRuleReq;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Description 规律规则Service
 * @Author pengy
 * @Date 2018/6/21 9:45
 */
public interface FilterRuleService {

    Map<String,Object> qryFilterRule(FilterRuleReq filterRuleReq);

    Map<String,Object> delFilterRule(FilterRule filterRule);

    Map<String,Object> getFilterRule(Long ruleId);

    Map<String,Object> createFilterRule(FilterRuleAddVO addVO);

    Map<String,Object> modFilterRule(FilterRuleAddVO editVO);

    Map<String,Object> qryFilterRules(FilterRuleReq filterRuleReq);

    Map<String,Object> importUserList(MultipartFile multipartFile, FilterRule filterRule) throws IOException;

    Map<String,Object> listUserList(UserList userList) throws IOException;

    Map<String, Object> getFilterRule(List<Integer> filterRuleIdList);


}
