package com.zjtelcom.cpct.controller.test;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dao.channel.MktVerbalConditionMapper;
import com.zjtelcom.cpct.dao.es.EmployeeRepository;
import com.zjtelcom.cpct.domain.Rule;
import com.zjtelcom.cpct.domain.RuleDetail;
import com.zjtelcom.cpct.domain.channel.MktVerbalCondition;
import com.zjtelcom.cpct.dto.es.Employee;
import com.zjtelcom.cpct.service.EngineTestService;
import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.dto.grouping.TarGrpDetail;
import com.zjtelcom.cpct.service.grouping.TarGrpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @Description test
 * @Author pengy
 * @Date 2018/6/25 11:42
 */
@RestController
@RequestMapping("${adminPath}/test")
public class TestController extends BaseController {

    @Autowired
    private TarGrpService tarGrpService;

    @Autowired
    private EngineTestService engineTestService;

    @Autowired
    private MktVerbalConditionMapper mktVerbalConditionMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * 添加
     * @return
     */
    @RequestMapping("add")
    public String add() {
        Employee employee = new Employee();
        employee.setId("2");
        employee.setFirstName("xuxu");
        employee.setLastName("zh");
        employee.setAge(26);
        employee.setAbout("i am in peking");
        employeeRepository.save(employee);
        System.err.println("add a obj");
        return "success";
    }

    /**
     * 删除
     * @return
     */
    @RequestMapping("delete")
    public String delete() {
        Employee employee = employeeRepository.queryEmployeeById("1");
        employeeRepository.delete(employee);
        return "success";
    }

    /**
     * 局部更新
     * @return
     */
    @RequestMapping("update")
    public String update() {
        Employee employee = employeeRepository.queryEmployeeById("1");
        employee.setFirstName("哈哈");
        employeeRepository.save(employee);
        System.err.println("update a obj");
        return "success";
    }
    /**
     * 查询
     * @return
     */
    @RequestMapping("query")
    public Employee query() {
        Employee accountInfo = employeeRepository.queryEmployeeById("2");
        System.err.println(new Gson().toJson(accountInfo));
        return accountInfo;
    }


    @RequestMapping("/test")
    @CrossOrigin
    public String test() {
        TarGrpDetail tarGrpDetail = new TarGrpDetail();
        tarGrpDetail.setTarGrpType("1000");
        tarGrpDetail.setStatusCd("1000");
        Map<String, Object> maps = tarGrpService.createTarGrp(tarGrpDetail);
        return JSON.toJSONString(maps);

    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        System.out.println(list.size());
    }

    @RequestMapping("/engine")
    @CrossOrigin
    public String engine(@RequestBody Map<String, String> map) {
        engineTestService.test(map);
        return initSuccRespInfo(null);
    }

    /**
     * 规则插入
     * @param param
     * @return
     */
    @RequestMapping("/ruleInsertTest")
    @CrossOrigin
    public String ruleInsertTest(@RequestBody String param) {
        ruleInsert(param);
        return initSuccRespInfo(null);
    }

    public void ruleInsert(String param) {

        //转换为json对象
        JSONObject jsonObject = JSONObject.parseObject(param);
        System.out.println(jsonObject.toString());
        //解析参数
        Rule rule = JSON.parseObject(param, new TypeReference<Rule>() {});
        System.out.println(jsonObject.toString());

        saveDetail(rule);
        System.out.println("-------");
    }


    public Long saveDetail(Rule rule) {

        //保存规则
        List<RuleDetail> list = rule.getListData();
        Long idLeft;
        Long idRight;
        String type = rule.getType();
        if (list.size() > 0) {  //第一层
            //先保存第一条标签因子
            idLeft = insert(list.get(0).getId(), list.get(0).getOperType(), list.get(0).getContent());
            for (int i = 1; i < list.size(); i++) {
                idRight = insert(list.get(i).getId(), list.get(i).getOperType(), list.get(i).getContent());
                idLeft = insert(idLeft, type, idRight);
            }
            if (rule.getRuleChildren() != null) {
                idLeft = insert(idLeft, type, saveDetail(rule.getRuleChildren()));
            }
        } else {
            idLeft = 0L;
        }
        System.out.println("----");

        return idLeft;
    }


    public Long insert(Integer left, String operType, String right) {
        MktVerbalCondition mktVerbalCondition = new MktVerbalCondition();
        Long evtContactConfId = 10001L;
        mktVerbalCondition.setVerbalId(evtContactConfId);
        mktVerbalCondition.setLeftParam(left.toString());
        mktVerbalCondition.setRightParam(right);
        mktVerbalCondition.setOperType(operType);
        mktVerbalCondition.setLeftParamType("1000"); //标签因子
        mktVerbalCondition.setRightParamType("3000"); //固定值
        mktVerbalCondition.setConditionType("1"); //类型
        mktVerbalConditionMapper.insert(mktVerbalCondition);
        Long l = mktVerbalCondition.getConditionId();
        System.out.println("ID = " + l + ";左参 = " + left + ";右参 = " + right + ";类型 = " + operType);
        return l;
    }

    public Long insert(Long left, String operType, Long right) {
        MktVerbalCondition mktVerbalCondition = new MktVerbalCondition();
        Long evtContactConfId = 10001L;
        mktVerbalCondition.setVerbalId(evtContactConfId);
        mktVerbalCondition.setLeftParam(left.toString());
        mktVerbalCondition.setRightParam(right.toString());
        mktVerbalCondition.setOperType(operType);
        mktVerbalCondition.setLeftParamType("2000"); //表达式
        mktVerbalCondition.setRightParamType("2000"); //表达式
        mktVerbalCondition.setConditionType("1"); //类型
        mktVerbalConditionMapper.insert(mktVerbalCondition);
        Long l = mktVerbalCondition.getConditionId();
        System.out.println("ID = " + l + ";左参 = " + left + ";右参 = " + right + ";类型 = " + operType);
        return l;
    }

    /**
     * 规则查询
     * @return
     */
    @RequestMapping("/ruleSelectTest")
    @CrossOrigin
    public String ruleSelectTest() {
        String result = ruleSelect();
        return initSuccRespInfo(result);
    }

    public String ruleSelect() {
        //唯一ID
        Long evtContactConfId = 10001L;
        //查询出所有规则
        List<MktVerbalCondition> mktVerbalConditions = mktVerbalConditionMapper.findConditionListByVerbalId(evtContactConfId);

        List<MktVerbalCondition> labels = new ArrayList<>(); //标签因子
        List<MktVerbalCondition> expressions = new ArrayList<>(); //表达式

        //分类
        for (MktVerbalCondition mktVerbalCondition : mktVerbalConditions) {
            if ("1000".equals(mktVerbalCondition.getLeftParamType())) {
                labels.add(mktVerbalCondition);
            } else if ("2000".equals(mktVerbalCondition.getLeftParamType())) {
                expressions.add(mktVerbalCondition);
            }
        }
        System.out.println("-------");
        Rule rule = parseRules(labels, expressions, 0);
        return JSON.toJSONString(rule);
    }

    public Rule parseRules(List<MktVerbalCondition> labels, List<MktVerbalCondition> expressions, int index) {
        Rule rule = new Rule();
        List<RuleDetail> ruleDetails = new ArrayList<>();
        RuleDetail ruleDetail;

        //遍历所有表达式
        rule.setType(expressions.get(index).getOperType());
        for (int i = index; i < expressions.size(); i++) {
            //判断类型  如果不相同就进入下一级
            if (rule.getType().equals(expressions.get(i).getOperType())) {
                for (MktVerbalCondition condition : labels) {
                    if (expressions.get(i).getLeftParam().equals(condition.getConditionId().toString())
                            || expressions.get(i).getRightParam().equals(condition.getConditionId().toString())) {
                        ruleDetail = new RuleDetail();
                        ruleDetail.setId(Integer.parseInt(condition.getLeftParam()));
                        ruleDetail.setContent(condition.getRightParam());
                        ruleDetail.setOperType(condition.getOperType());
                        ruleDetails.add(ruleDetail);
                    }
                }
            } else {
                rule.setRuleChildren(parseRules(labels, expressions, i));
                break;
            }
        }
        if (ruleDetails.size() == 0) {
            return null;
        }
        System.out.println("-------");
        rule.setListData(ruleDetails);
        return rule;
    }

    /**
     * 规则拼接测试
     * @return
     */
    @RequestMapping("/expressionTest")
    @CrossOrigin
    public String expressionTest() {
        //唯一ID
        Long evtContactConfId = 10001L;
        //查询出所有规则
        List<MktVerbalCondition> mktVerbalConditions = mktVerbalConditionMapper.findConditionListByVerbalId(evtContactConfId);

        String expression = expressionMatching(mktVerbalConditions, mktVerbalConditions.get(mktVerbalConditions.size() - 1).getConditionId());

        Map<String,String> map = new HashMap<>();
        map.put("expression",expression);
        engineTestService.test(map);

        return initSuccRespInfo(expression);
    }


    public String expressionMatching(List<MktVerbalCondition> expressions, Long id) {
        StringBuilder sb = new StringBuilder();
        for (MktVerbalCondition mktVerbalCondition : expressions) {
            if (id.equals(mktVerbalCondition.getConditionId())) {
                //左参
                Long idLeft = Long.parseLong(mktVerbalCondition.getLeftParam());
                //右参
                Long idRight = Long.parseLong(mktVerbalCondition.getRightParam());

                sb.append("(");
                //左参结果
                forLabel(sb, idLeft, expressions);

                if ("7000".equals(mktVerbalCondition.getOperType())) {
                    sb.append("&&");
                } else if ("8000".equals(mktVerbalCondition.getOperType())) {
                    sb.append("||");
                }

                //右参结果
                forLabel(sb, idRight, expressions);
                sb.append(")");

            }
        }

        return sb.toString();
    }


    public void forLabel(StringBuilder sb, Long id, List<MktVerbalCondition> expressions) {
        for (MktVerbalCondition condition : expressions) {
            if (id.equals(condition.getConditionId())) {
                if ("1000".equals(condition.getLeftParamType())) {
                    sb.append(condition.getLeftParam());
                    //这里待修改
                    if ("1000".equals(condition.getOperType())) {
                        sb.append(">");
                    } else if ("2000".equals(condition.getOperType())) {
                        sb.append("<");
                    } else if ("3000".equals(condition.getOperType())) {
                        sb.append("==");
                    }
                    sb.append(condition.getRightParam());
                } else {
                    sb.append(expressionMatching(expressions, condition.getConditionId()));
                }
                break;
            }
        }
    }



}


