package com.zjtelcom.cpct.open.serviceImpl.label;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ctzj.smt.bss.cooperate.service.dubbo.IContactTaskService;
import com.zjpii.biz.serv.YzServ;
import com.zjpii.biz.service.crm.AssetQueryService;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.enums.AreaNameEnum;
import com.zjtelcom.cpct.open.entity.label.OpenCustInjectionLabel;
import com.zjtelcom.cpct.open.service.label.OpenLabelService;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.MD5Util;
import com.zjtelcom.cpct.util.SpringUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/12/17
 * @Description: 1.客户编码           "custId":"171100020152783"
 * 2.资产编码+业务号码   1-GB1Jg033776   15356152333   需要通过crm接口得到资产编码
 * http://localhost:30809/api/custInjectionLabel?accNum=17788568424&fields=NEW_CORP_USER_NAME,SERV_START_DT,AGE_FLG,ZK_FLG
 */
@Service
@Transactional
public class OpenLabelServiceImpl implements OpenLabelService {


    private static final Logger log = Logger.getLogger(OpenLabelServiceImpl.class);

    @Autowired(required = false)
    private YzServ yzServ;

    @Autowired(required = false)
    private AssetQueryService assetQueryService;

    @Autowired(required = false)
    private IContactTaskService iContactTaskService;

    @Autowired
    private SysParamsMapper sysParamsMapper;


    @Value("${platformConfig.channel}")
    private  String channel;
    @Value("${platformConfig.channel_token}")
    private  String channel_token;
    @Value("${platformConfig.bis_module}")
    private  String bis_module;
    @Value("${platformConfig.bis_detail}")
    private  String bis_detail;
    @Value("${platformConfig.version}")
    private  String version;


    private final static String IS_USE_DUBBO_LABEL = "IS_USE_DUBBO_LABEL";    //是否开启dubbo调用的系统参数key

    private final static String OPEN = "2";   //开启dubbo调用


    @Override
    public Map<String, Object> queryById(String id) {
        return null;
    }

    @Override
    public Map<String, Object> addByObject(Object object) {
        return null;
    }

    @Override
    public Map<String, Object> updateByParams(String id, Object object) {
        return null;
    }

    @Override
    public Map<String, Object> deleteById(String id) {
        return null;
    }


    /**
     * 传参查询   custId客户级   accNum资产级
     *
     * @param map
     * @return
     */
    @Override
    public Map<String, Object> queryListByMap(Map<String, Object> map) {
        Map<String, Object> resultMap = new HashMap<>();
        //首先判断是客户级查询还是资产级查询
        String custId = (String) map.get("custId");   //客户级
        String accNum = (String) map.get("accNum");   //资产级
        JSONObject json = new JSONObject();
        if (StringUtils.isBlank(custId) && StringUtils.isBlank(accNum)) {
            json.put("message", "custId和accNum不能全部为空!");
            resultMap.put("params", json);
            return resultMap;
        }
        //客户级和资产级只能二选一
        if (!StringUtils.isBlank(custId) && !StringUtils.isBlank(accNum)) {
            json.put("message", "请选择custId或accNum其中一个查询");
            resultMap.put("params", json);
            return resultMap;
        }
        //contactTaskId  接触任务标示  如果存在可以调用协同的获取资产编码和C3信息
        String contactTaskId = (String) map.get("contactTaskId");
        String fields = (String) map.get("fields");
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("custId", custId);
        queryMap.put("accNum", accNum);
        queryMap.put("contactTaskId", contactTaskId);
        queryMap.put("fields", fields);
        if (!StringUtils.isBlank(custId)) {
            //客户级查询
            if(!StringUtils.isBlank(contactTaskId)){
                //如果接触任务id存在则调用协同呢中心接口获取信息
                Map<String, String> taskId = getTaskId(contactTaskId);
                if("1".equals(taskId.get("resultCode"))){
                    taskId.put("fields", fields);
                    Map<String, Object> stringObjectMap = selectCustomerLabelByEs(taskId);
                    if ("1".equals(stringObjectMap.get("resultCode"))) {
                        //将得到的标签封装成标签对象
                        JSONArray jsonArray = changeToLabel((JSONObject) stringObjectMap.get("body"));
                        resultMap.put("params", jsonArray);
                    } else {
                        resultMap.put("params", stringObjectMap);
                        return resultMap;
                    }

                }else{
                    json.put("message",taskId.get("message"));
                    resultMap.put("params", json);
                    return resultMap;
                }

            }else{
                //调用crm资产查询服务
                Map<String, String> stringObjectMap = queryCrm(queryMap);
                if("1".equals(stringObjectMap.get("resultCode"))){
                    stringObjectMap.put("fields", fields);
                    Map<String, Object> crmMap = selectCustomerLabelByEs(stringObjectMap);
                    if ("1".equals(crmMap.get("resultCode"))) {
                        //将得到的标签封装成标签对象
                        JSONArray jsonArray = changeToLabel((JSONObject) crmMap.get("body"));
                        resultMap.put("params", jsonArray);
                    } else {
                        resultMap.put("params", crmMap);
                        return resultMap;
                    }
                }else{
                    json.put("message",stringObjectMap.get("message"));
                    resultMap.put("params", json);
                    return resultMap;
                }

            }


        } else {
            //资产级查询  CustviewResultObject
            if(!StringUtils.isBlank(contactTaskId)){
                Map<String, String> taskId = getTaskId(contactTaskId);
                if("1".equals(taskId.get("resultCode"))){
                    taskId.put("fields", fields);
                    Map<String, Object> stringObjectMap = selectAssetLabelByEs(taskId);
                    System.out.println("客户级查询："+stringObjectMap);
                    if ("1".equals(stringObjectMap.get("resultCode"))) {
                        //将得到的标签封装成标签对象
                        JSONArray jsonArray = changeToLabel((JSONObject) stringObjectMap.get("body"));
                        resultMap.put("params", jsonArray);
                    } else {
                        resultMap.put("params", stringObjectMap);
                        return resultMap;
                    }

                }else{
                    json.put("message",taskId.get("message"));
                    resultMap.put("params", json);
                    return resultMap;
                }

            }else{
                //调用crm资产查询服务
                Map<String, String> stringObjectMap = queryCrm(queryMap);
                if("1".equals(stringObjectMap.get("resultCode"))){
                    stringObjectMap.put("fields", fields);
                    Map<String, Object> crmMap = selectAssetLabelByEs(stringObjectMap);
                    System.out.println("资产级查询："+crmMap);
                    if ("1".equals(crmMap.get("resultCode"))) {
                        //将得到的标签封装成标签对象
                        JSONArray jsonArray = changeToLabel((JSONObject) crmMap.get("body"));
                        resultMap.put("params", jsonArray);
                    } else {
                        resultMap.put("params", crmMap);
                        return resultMap;
                    }
                }else{
                    json.put("message",stringObjectMap.get("message"));
                    resultMap.put("params", json);
                    return resultMap;
                }

            }

        }
        return resultMap;
    }


    /**
     * 因子查询结果判断
     * @param stringObjectMap
     * @param resultMap
     * @return
     */
    public Map<String, Object>  getResult( Map<String, Object> stringObjectMap,Map<String, Object> resultMap){
        resultMap.put("flg",true);
        if ((int) (stringObjectMap.get("resultCode")) != 0) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", stringObjectMap.get("result"));
            resultMap.put("params", jsonObject);
            resultMap.put("flg",false);
            return resultMap;
        } else {
            //将得到的标签封装成标签对象
            JSONArray jsonArray = changeToLabel((JSONObject) stringObjectMap.get("body"));
            resultMap.put("params", jsonArray);
        }
        return resultMap;
    }




    /**
     * 资产级标签查询
     *
     * @param map
     * @return
     */
    public Map<String, Object> selectAssetLabelByEs(Map<String, String> map) {
        Map<String, Object> dubboLabel = new HashMap<>();
        JSONObject param = new JSONObject();
        dubboLabel.put("resultCode", "1");   //1请求成功
        //查询标识
        param.put("queryNum", map.get("accNum"));
        param.put("c3", map.get("lanId"));
        param.put("queryId", map.get("integrationId"));
        param.put("type", "1");
        param.put("queryFields", fieldsMap(map.get("fields")));
        log.info("资产级查询param " + param.toString());
        Map<String, Object> dubboResult = null;
        try {
            dubboResult = yzServ.queryYz(JSON.toJSONString(param));
        } catch (Exception e) {
            e.printStackTrace();
            dubboLabel.put("result", "实时因子查询接口调用失败");
            dubboLabel.put("resultCode", "0");
            return dubboLabel;
        }
        log.info("因子查询返回：" + dubboResult);
        if ("0".equals(dubboResult.get("result_code").toString())) {
            //查询成功
            JSONObject dataJson=JSONObject.parseObject(JSON.toJSONString(dubboResult));
            JSONObject body = (JSONObject)(dataJson.get("msgbody"));
            if (body.isEmpty()) {
                dubboLabel.put("result", "标签信息不存在");
                dubboLabel.put("resultCode", "0");
            } else {
                dubboLabel.put("body", body);
            }
        } else {
            //查询失败
            dubboLabel.put("result", "查询标签实例失败");
            dubboLabel.put("resultCode", "0");
        }
        return dubboLabel;
    }


    /**
     * 客户级查询
     *
     * @param map
     * @return
     */
    public Map<String, Object> selectCustomerLabelByEs(Map<String, String> map) {
        Map<String, Object> dubboLabel = new HashMap<>();
        JSONObject param = new JSONObject();
        dubboLabel.put("resultCode", "1");   //1请求成功
        //查询标识
        param.put("queryNum", "");
        param.put("c3", map.get("lanId"));
        param.put("queryId", map.get("custId"));
        param.put("queryFields", fieldsMap(map.get("fields")));
        param.put("type", "2");
        log.info("客户级查询param " + param.toString());
        Map<String, Object> dubboResult = yzServ.queryYz(JSON.toJSONString(param));
        log.info("因子查询返回：" + dubboResult);
        if ("0".equals(dubboResult.get("result_code").toString())) {
            //查询成功
            JSONObject body = new JSONObject((HashMap) dubboResult.get("msgbody"));
            if(body.isEmpty()){
                dubboLabel.put("result", "标签信息不存在");
                dubboLabel.put("resultCode", "0");
            }else{
                dubboLabel.put("body", body);
            }
        } else {
            //查询失败
            dubboLabel.put("result", "查询标签实例失败");
            dubboLabel.put("resultCode", "0");
        }

        return dubboLabel;
    }


    /**
     * 随机返回一个资产编码
     *
     * @param code
     * @return
     */
    public static String assetSelect(String code) {
        String result = "";
        Map<String, String> map = new HashMap<>();
        map.put("18967120690", "1-H1CDl087068");
        map.put("17757146292", "1-GCRHV099686");
        map.put("15372446210", "1-H19Bt022144");
        map.put("18057140441", "3-QS47XDF");
        map.put("13372562971", "1-H14Ax078743");
        map.put("15336505771", "3-QOQ0Y44");
        map.put("15382331292", "1-H16A2016040");
        for (String str : map.keySet()) {
            if (str.equals(code)) {
                result = map.get(str);
                log.info("有符合的返回：" + result);
                return result;
            }
        }
        //没有符合的随机取一个返回
        int ram = (int) (Math.random() * (map.size() - 1));
        int i = 0;
        for (String str : map.keySet()) {
            if (i == ram) {
                return map.get(str);
            }
            i++;
        }
        return "1-H1CDl087068";
    }


    /**
     * 测试资产级标签查询的map
     *
     * @return
     */
    public static Map<String, String> assetMap(String fields, String servCode) {
        Map<String, String> dubboLabel = new HashMap<>();
        dubboLabel.put("accNum", "18967120690");
        dubboLabel.put("lanId", "571");
        dubboLabel.put("servCode", assetSelect(servCode));
        if (!StringUtils.isBlank(fields)) {
            dubboLabel.put("fields", fields);
        } else {
            dubboLabel.put("fields", "SERV_START_DT,AGE_FLG,PROM_LIST,PROM_AMT,QQW_CNT,ZK_FLG,PROM_NAME_TYPE,BXL_LLB_FLG,NEW_CORP_USER_NAME");
        }
        return dubboLabel;
    }


    /**
     * 测试的客户级标签查询
     *
     * @return
     */
    public static Map<String, String> customerMap(String fields) {
        Map<String, String> dubboLabel = new HashMap<>();
        dubboLabel.put("lanId", "571");
        dubboLabel.put("custId", "171100040292117");
        if (!StringUtils.isBlank(fields)) {
            dubboLabel.put("fields", fields);
        } else {
            dubboLabel.put("fields", "SERV_START_DT,AGE_FLG,PROM_LIST,PROM_AMT,QQW_CNT,ZK_FLG,PROM_NAME_TYPE,BXL_LLB_FLG,NEW_CORP_USER_NAME");
        }
        return dubboLabel;
    }


    /**
     * 如果没传具体标签 则查询如下指定标签
     * @param fields
     * @return
     */
    public static String fieldsMap(String fields) {
        SysParamsMapper sysParamsMapper= SpringUtil.getBean(SysParamsMapper.class);
        boolean flag=false;
        List<SysParams> sysParams = sysParamsMapper.listParamsByKeyForCampaign("IS_USE_DUBBO_LABEL");
        //如果不存在 则返回所有标签
        if (!sysParams.isEmpty()) {
            if ("2".equals(sysParams.get(0).getParamValue())) {
                log.info("返回特定标签");
                flag = true;
            }
        }
            if(flag){
                fields="SERV_START_DT,AGE_FLG,PROM_LIST,PROM_AMT,QQW_CNT,ZK_FLG,PROM_NAME_TYPE,BXL_LLB_FLG,NEW_CORP_USER_NAME";
            }
            return fields;
    }




    /**
     * 转换为集团格式
     *
     * @param json
     * @return
     */
    public static JSONArray changeToLabel(JSONObject json) {
        //{"SERV_START_DT":"2018-08-22","AGE_FLG":"[25岁，30岁)","PROM_LIST":"5100000501100017&8817331330010065"}
        JSONArray jsonArray = new JSONArray();
        for (String str : json.keySet()) {
            OpenCustInjectionLabel openCustInjectionLabel = new OpenCustInjectionLabel();
            openCustInjectionLabel.setInjectionLabelName(str);
            openCustInjectionLabel.setLabelValue((String) json.get(str));
            String s = JSON.toJSONString(openCustInjectionLabel, SerializerFeature.WriteMapNullValue);
            jsonArray.add(JSONObject.parse(s));
        }

        return jsonArray;
    }


    /**
     * 接触任务ID获取用户信息   客户编码   业务号码   集成编码
     *
     * @return
     */
    public Map<String, String> getTaskId(String taskId) {
        Map<String, String> result = new HashMap<>();//返回参数
        Map<String, String> map = new HashMap<>();
        map.put("contactTaskId", taskId);
        log.info("协同查询入参："+map);
        Map<String, Object> stringObjectMap = null;
        try {
            stringObjectMap = iContactTaskService.queryTaskDetail4openApi(map);
            log.info("协同返回结果："+stringObjectMap);
            if (!stringObjectMap.isEmpty()) {
                if ("1".equals(stringObjectMap.get("resultCode"))){
                    //请求成功
                    result.put("resultCode","1");
                    result.put("custId",(String) stringObjectMap.get("custNumber"));      //客户编码
                    result.put("accNum",(String) stringObjectMap.get("accNum"));              //业务号码
                    result.put("integrationId",(String) stringObjectMap.get("targetObjNbr"));  //集成编码 和crm接口入参统一
                    result.put("lanId",String.valueOf(stringObjectMap.get("lanId")) );         //c3  如杭州571
                }else{
                    result.put("resultCode","0");
                    result.put("message", (String) stringObjectMap.get("resultMsg"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("resultCode","0");
            result.put("message", "调用协同中心接触任务接口失败");
        }

        return result;

    }


    /**
     * 获取统一平台调用参数
     * @return
     */
    public  Map<String, Object> headMap(){
        Map<String, Object> headMap=new HashMap<>();
        //获取统一平台配置参数
        headMap.put("channel",channel);
        //渠道秘钥+ channel +年月日（20160101）做MD5（32位大写）加密。
        String token=channel_token+channel+ DateUtil.getNowTime();
        headMap.put("channel_token", MD5Util.encodePassword(token).toUpperCase());
        headMap.put("bis_module",bis_module);
        headMap.put("bis_detail",bis_detail);
        headMap.put("version",version);
        return  headMap;
    }


    /**
     * 通过客户id或者资产号码 得到用户信息
     * @param parameterMap
     * @return
     */
    public Map<String, String> queryCrm(Map<String, String> parameterMap ){
        Map<String, String> result=new HashMap<>();
        String custId= parameterMap.get("custId");
        String number= parameterMap.get("accNum");
        Map<String, Object> eventExtMap=new HashMap<>();
        Map<String, Object> headMap=headMap();
        //具体要查的参数
        Map<String, Object> paramMap=new HashMap<>();
        if(!StringUtils.isBlank(custId)){
            //通过客户id查  会返回很多资产
            paramMap.put("paramType","ACCOUNTNUMBER");
            paramMap.put("paramValue",custId);
        }else if(!StringUtils.isBlank(number)){
            //通过资产号码查
            paramMap.put("paramType","SERVICEID");
            paramMap.put("paramValue",number);
        }
        paramMap.put("needInactiveFlg",false);
        paramMap.put("cityName","");
        log.info("crm请求参数："+paramMap);
        Map<String, Object> stringObjectMap = null;
        try {
            stringObjectMap = assetQueryService.assetQueryList(headMap, paramMap, eventExtMap);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("resultCode","0");  //失败
            result.put("message", "调用crm资产接口查询失败");
            return  result;
        }
        log.info("crm请求返回："+stringObjectMap);
        //解析得到资产编码 和地市信息
        JSONObject dataJson=JSONObject.parseObject(JSON.toJSONString(stringObjectMap));
        JSONObject msghead = (JSONObject) dataJson.get("msghead");
        if("1".equals(msghead.get("result_code"))){
            result.put("resultCode","1");
        }else{
            result.put("resultCode","0");  //失败
            result.put("message", (String) msghead.get("result_msg"));
            return  result;
        }
            JSONArray body =  JSONObject.parseArray((String) dataJson.get("msgbody"));
            if (body.isEmpty()){
                result.put("resultCode","0");  //失败
                result.put("message", "查询crm资产返回无信息");
                return  result;
            }
            //只解析一个
            JSONObject jsonObject=body.getJSONObject(0);
            result.put("integrationId", (String) jsonObject.get("integrationId"));     //集成编码
            String c3Name=(String) jsonObject.get("c3Name");
            result.put("lanId",String.valueOf(AreaNameEnum.getLanIdByName(c3Name)));   //c3
            result.put("custId",(String) jsonObject.get("accountNumber"));             //客户id
            result.put("accNum",(String) jsonObject.get("serviceId"));                 //客户号码
        return  result;

    }


    public static void main(String[] args) {



    }

}
