package com.zjtelcom.cpct.service.impl.filter;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.dao.user.UserListMapper;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dto.filter.FilterRuleVO;
import com.zjtelcom.cpct.dto.user.UserList;
import com.zjtelcom.cpct.request.filter.FilterRuleReq;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.filter.FilterRuleService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import com.zjtelcom.cpct.util.UserUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

/**
 * @Description 规律规则实现类
 * @Author pengy
 * @Date 2018/6/21 9:46
 */
@Service
@Transactional
public class FilterRuleServiceImpl extends BaseService implements FilterRuleService {

    @Autowired
    private FilterRuleMapper filterRuleMapper;
    @Autowired
    private UserListMapper userListMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private SysParamsMapper sysParamsMapper;

    /**
     * 过滤规则列表（含分页）
     */
    @Override
    public Map<String, Object> qryFilterRule(FilterRuleReq filterRuleReq) {
        Map<String, Object> maps = new HashMap<>();
        Page pageInfo = filterRuleReq.getPageInfo();
        PageHelper.startPage(pageInfo.getPage(), pageInfo.getPageSize());
        List<FilterRule> filterRules = filterRuleMapper.qryFilterRule(filterRuleReq.getFilterRule());
        Page page = new Page(new PageInfo(filterRules));
        List<FilterRuleVO> voList = new ArrayList<>();
        for (FilterRule rule : filterRules){
            FilterRuleVO vo = BeanUtil.create(rule,new FilterRuleVO());
            SysParams sysParams = sysParamsMapper.findParamsByValue("FILTER_RULE_TYPE",rule.getFilterType());
            if (sysParams!=null){
                vo.setFilterTypeName(sysParams.getParamName());
            }
            voList.add(vo);
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("filterRules", voList);
        maps.put("pageInfo",page);
        return maps;
    }

    /**
     * 过滤规则列表（不含分页）
     */
    @Override
    public Map<String, Object> qryFilterRules(FilterRuleReq filterRuleReq) {
        Map<String, Object> maps = new HashMap<>();
        List<FilterRule> filterRules = filterRuleMapper.qryFilterRule(filterRuleReq.getFilterRule());
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("filterRules", filterRules);
        return maps;
    }

    /**
     * 导入用户名单
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> importUserList(MultipartFile multipartFile, Long ruleId) throws IOException {
        Map<String, Object> maps = new HashMap<>();

        InputStream inputStream = multipartFile.getInputStream();
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
        Sheet sheet = wb.getSheetAt(0);
        Integer rowNums = sheet.getLastRowNum() + 1;
        for (int i = 0; i < rowNums; i++) {
            UserList userList = new UserList();
            Row row = sheet.getRow(i);
            for (int j = 0; j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                switch (j) {
                    case 0:
                        userList.setUserName(cell.getStringCellValue());
                        break;
                    case 1:
                        cell.setCellType(CellType.STRING);
                        userList.setUserPhone(cell.getStringCellValue());
                        break;
                    case 2:
                        userList.setFilterType(cell.getStringCellValue());
                        break;
                }
            }
            UserList userListT = userListMapper.getUserList(userList);
            if (userListT != null) {
                continue;
            }
            userList.setRuleId(ruleId);
            userList.setCreateDate(new Date());
            userList.setUpdateDate(new Date());
            userList.setStatusDate(new Date());
            userList.setRemark("123");
            userList.setLanId(1L);
            userList.setUpdateStaff(UserUtil.loginId());
            userList.setCreateStaff(UserUtil.loginId());
            userList.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
            userListMapper.insert(userList);

            //新用户存入redis;.
            redisUtils.hmSet(String.valueOf(userList.getUserId()), "userName", userList.getUserName());
            redisUtils.hmSet(String.valueOf(userList.getUserId()), "userPhone", userList.getUserPhone());
            redisUtils.hmSet(String.valueOf(userList.getUserId()), "filterType", userList.getFilterType());
            redisUtils.hmSet(String.valueOf(userList.getUserId()), "ruleId", String.valueOf(ruleId));
            System.out.println(redisUtils.hmGet(String.valueOf(userList.getUserId()), "userName"));
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 从缓存中获取用户列表
     */
    @Override
    public Map<String, Object> listUserList(UserList userList) throws IOException {
        Map<String, Object> maps = new HashMap<>();
        List<UserList> userLists = new ArrayList<>();
        Set<Object> keys = redisUtils.keys("[1-9]*");
        for (Object str : keys) {
            UserList userListT = new UserList();
            userListT.setUserName(String.valueOf(redisUtils.hmGet((String) str, "userName")));
            userListT.setUserPhone(String.valueOf(redisUtils.hmGet((String) str, "userPhone")));
            userLists.add(userListT);
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("userLists", userLists);
        return maps;
    }

    /**
     * 删除过滤规则
     */
    @Override
    public Map<String, Object> delFilterRule(FilterRule filterRule) {
        Map<String, Object> maps = new HashMap<>();
        filterRuleMapper.delFilterRule(filterRule);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 查询单个过滤规则
     */
    @Override
    public Map<String, Object> getFilterRule(FilterRule filterRule) {
        Map<String, Object> map = new HashMap<>();
        FilterRule filterRuleT = filterRuleMapper.getFilterRule(filterRule);
        map.put("resultCode", CommonConstant.CODE_SUCCESS);
        map.put("resultMsg", StringUtils.EMPTY);
        map.put("filterRule", filterRuleT);
        return map;
    }

    /**
     * 新建过滤规则
     */
    @Override
    public Map<String, Object> createFilterRule(FilterRule filterRule) {
        Map<String, Object> maps = new HashMap<>();
        filterRule.setCreateDate(DateUtil.getCurrentTime());
        filterRule.setUpdateDate(DateUtil.getCurrentTime());
        filterRule.setStatusDate(DateUtil.getCurrentTime());
        filterRule.setUpdateStaff(UserUtil.loginId());
        filterRule.setCreateStaff(UserUtil.loginId());
        filterRule.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
        //销售品互斥过滤 加labelcode
        if (filterRule.getFilterType().equals("3000")){
            filterRule.setLabelCode("PROM_LIST");
        }
        filterRuleMapper.createFilterRule(filterRule);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("filterRule", filterRule);
        return maps;
    }

    /**
     * 修改过滤规则
     */
    @Override
    public Map<String, Object> modFilterRule(FilterRule filterRule) {
        Map<String, Object> maps = new HashMap<>();
        filterRule.setUpdateDate(DateUtil.getCurrentTime());
        filterRule.setUpdateStaff(UserUtil.loginId());
        if (filterRule.getFilterType().equals("3000")){
            filterRule.setLabelCode("PROM_LIST");
        }
        filterRuleMapper.modFilterRule(filterRule);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("filterRule", filterRule);
        return maps;
    }


    /**
     * 根据过滤规则id集合查询过滤规则集合
     */
    @Override
    public Map<String, Object> getFilterRule(List<Integer> filterRuleIdList) {
        Map<String, Object> map = new HashMap<>();
        List<FilterRule> filterRuleList = new ArrayList<>();
        for (Integer filterRuleId : filterRuleIdList) {
            FilterRule filterRule = filterRuleMapper.selectByPrimaryKey(filterRuleId.longValue());
            filterRuleList.add(filterRule);
        }
        map.put("resultCode", CommonConstant.CODE_SUCCESS);
        map.put("resultMsg", StringUtils.EMPTY);
        map.put("filterRuleList", filterRuleList);
        return map;
    }

}
