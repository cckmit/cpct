package com.zjtelcom.cpct.open.serviceImpl.label;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ctzj.smt.bss.custview.model.common.CustviewResultObject;
import com.ctzj.smt.bss.custview.model.dto.CustViewIdMappingDto;
import com.ctzj.smt.bss.custview.query.service.api.ICustomerDubboService;
import com.zjpii.biz.serv.YzServ;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.open.entity.label.OpenCustInjectionLabel;
import com.zjtelcom.cpct.open.service.label.OpenLabelService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private ICustomerDubboService iCustomerDubboService;

    @Autowired(required = false)
    private YzServ yzServ;

    @Autowired
    private SysParamsMapper sysParamsMapper;


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
        JSONObject json=new JSONObject();
        if (StringUtils.isBlank(custId) && StringUtils.isBlank(accNum)) {
            json.put("message","custId和accNum不能全部为空!");
            resultMap.put("params", json);
            return resultMap;
        }
        //客户级和资产级只能二选一
        if (!StringUtils.isBlank(custId) && !StringUtils.isBlank(accNum)) {
            json.put("message","请选择custId或accNum其中一个查询");
            resultMap.put("params", json);
            return resultMap;
        }
        //contactTaskId  接触任务标示  目前没用到
        String contactTaskId = (String) map.get("contactTaskId");
        //得到查询的指定标签  SERV_START_DT,SERV_MONTH,CPRD_NAME,CCUST_ID  格式
        String fields = (String) map.get("fields");
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("custId", custId);
        queryMap.put("accNum", accNum);
        queryMap.put("contactTaskId", contactTaskId);
        queryMap.put("fields", fields);


        //首先判断是否开启dubbo接口的调用
        boolean isOpen = false;
        List<SysParams> sysParams = sysParamsMapper.listParamsByKeyForCampaign(IS_USE_DUBBO_LABEL);
        if (!sysParams.isEmpty()) {
            if (OPEN.equals(sysParams.get(0).getParamValue())) {
                isOpen = true;
            }
        }
        CustviewResultObject<CustViewIdMappingDto> crmPrivil = new CustviewResultObject<CustViewIdMappingDto>();
        if (!StringUtils.isBlank(custId)) {
            //客户级查询
            if(isOpen){
                //    objType=1&objCode=100000033
                crmPrivil = iCustomerDubboService.getCrmPrivil(1L, Long.valueOf(custId));
                log.info("crm返回结果："+crmPrivil.getResultMsg()+" "+crmPrivil.getResultCode()+" "+crmPrivil.getResultObject().getLanName());
            }else{
                crmPrivil = result();
            }
            //得到c3再调用因子查询接口得到标签信息
//            log.info("crm接口信息 resultCode："+crmPrivil.getResultCode() + " resultMsg " + crmPrivil.getResultMsg());
            if (crmPrivil.getResultObject() != null) {
                //得到c3地市
                map.put("lanId", crmPrivil.getResultObject().getLanName());
//                Map<String, Object> stringObjectMap = selectCustomerLabelByEs(queryMap);
                Map<String, Object> stringObjectMap = selectCustomerLabelByEs(customerMap(fields));
                if ((int) (stringObjectMap.get("resultCode")) != 0) {
                    JSONObject jsonObject=new JSONObject();
                    jsonObject.put("message",stringObjectMap.get("result"));
                    resultMap.put("params", jsonObject);
                    return resultMap;
                } else {
                    //将得到的标签封装成标签对象
                    JSONArray jsonArray = changeToLabel((JSONObject) stringObjectMap.get("body"));
                    resultMap.put("params", jsonArray);
                }
            }

        } else {
            //资产级查询  CustviewResultObject
            if(isOpen){
                log.info("资产级查询："+accNum);
                crmPrivil = iCustomerDubboService.getCrmPrivil(2L, Long.valueOf(accNum));
                log.info(crmPrivil.getResultCode()+" "+crmPrivil.getResultMsg()+" object"+crmPrivil.getResultObject());
            }else{
                crmPrivil = result();
            }
//            log.info(crmPrivil.getResultCode() + " " + crmPrivil.getResultMsg());
            if (crmPrivil.getResultObject() != null) {
                //得到c3地市和资产集成编码
                map.put("lanId", crmPrivil.getResultObject().getLanName());
                map.put("servCode", crmPrivil.getResultObject().getServCode());
//                Map<String, Object> stringObjectMap = selectAssetLabelByEs(queryMap);
                Map<String, Object> stringObjectMap = selectAssetLabelByEs(assetMap(fields,accNum));
                if ((int) (stringObjectMap.get("resultCode")) != 0) {
                    JSONObject jsonObject=new JSONObject();
                    jsonObject.put("message",stringObjectMap.get("result"));
                    resultMap.put("params", jsonObject);
                    return resultMap;
                } else {
                    log.info("接口返回主体:"+stringObjectMap.get("body"));
                    JSONArray jsonArray = changeToLabel((JSONObject)stringObjectMap.get("body"));
                    resultMap.put("params", jsonArray);
                }
            }

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
        dubboLabel.put("resultCode", 0);   //0请求成功
        //查询标识
        param.put("queryNum", map.get("accNum"));
        param.put("c3", map.get("lanId"));
        param.put("queryId", map.get("servCode"));
        param.put("type", "1");
        param.put("queryFields", map.get("fields"));
        log.info("资产级查询param " + param.toString());
        Map<String, Object> dubboResult = yzServ.queryYz(JSON.toJSONString(param));
        log.info("因子查询返回："+dubboResult);
        if ("0".equals(dubboResult.get("result_code").toString())) {
            //查询成功
            JSONObject body = new JSONObject((HashMap) dubboResult.get("msgbody"));
            if(body.isEmpty()){
                dubboLabel.put("result", "标签信息不存在");
                dubboLabel.put("resultCode", 1);
            }else{
                dubboLabel.put("body", body);
            }
        } else {
            //查询失败
            dubboLabel.put("result", "查询标签实例失败");
            dubboLabel.put("resultCode", 1);
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
        dubboLabel.put("resultCode", 0);   //0请求成功
        //查询标识
        param.put("queryNum", "");
        param.put("c3", map.get("lanId"));
        param.put("queryId", map.get("custId"));
        param.put("queryFields", map.get("fields"));
        param.put("type", "2");
        log.info("客户级查询param " + param.toString());
        Map<String, Object> dubboResult = yzServ.queryYz(JSON.toJSONString(param));
        log.info("因子查询返回："+dubboResult);
        if ("0".equals(dubboResult.get("result_code").toString())) {
            //查询成功
            JSONObject body = new JSONObject((HashMap) dubboResult.get("msgbody"));
            dubboLabel.put("body", body);
        } else {
            //查询失败
            dubboLabel.put("result", "查询标签实例失败");
            dubboLabel.put("resultCode", 1);
        }

        return dubboLabel;
    }


    /**
     * 随机返回一个资产编码
     * @param code
     * @return
     */
    public static String assetSelect(String code){
        String result="";
        Map<String,String> map=new HashMap<>();
        map.put("18967120690","1-H1CDl087068");
        map.put("17757146292","1-GCRHV099686");
        map.put("15372446210","1-H19Bt022144");
        map.put("18057140441","3-QS47XDF");
        map.put("13372562971","1-H14Ax078743");
        map.put("15336505771","3-QOQ0Y44");
        map.put("15382331292","1-H16A2016040");
        for (String str:map.keySet()){
           if (str.equals(code)){
               result=map.get(str);
               log.info("有符合的返回："+result);
               return  result;
           }
        }
        //没有符合的随机取一个返回
        int ram=(int)(Math.random()*(map.size()-1));
        int i=0;
        for (String str:map.keySet()){
            if(i==ram){
                return  map.get(str);
            }
            i++;
        }
        return  "1-H1CDl087068";
    }



    /**
     * 测试资产级标签查询的map
     *
     * @return
     */
    public static Map<String, String> assetMap(String fields,String servCode) {
        Map<String, String> dubboLabel = new HashMap<>();
        dubboLabel.put("accNum", "18967120690");
        dubboLabel.put("lanId", "571");
        dubboLabel.put("servCode", assetSelect(servCode));
        if(!StringUtils.isBlank(fields)){
            dubboLabel.put("fields",fields);
        }else{
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
        if(!StringUtils.isBlank(fields)){
            dubboLabel.put("fields",fields);
        }else{
            dubboLabel.put("fields", "SERV_START_DT,AGE_FLG,PROM_LIST,PROM_AMT,QQW_CNT,ZK_FLG,PROM_NAME_TYPE,BXL_LLB_FLG,NEW_CORP_USER_NAME");
        }
        return dubboLabel;
    }


    /**
     * crm接口 pst环境目前没有 先写死
     * @return
     */
    public static CustviewResultObject<CustViewIdMappingDto> result() {
        CustviewResultObject<CustViewIdMappingDto> crmPrivil = new CustviewResultObject<CustViewIdMappingDto>();
        CustViewIdMappingDto custViewIdMappingDto=new CustViewIdMappingDto();
        custViewIdMappingDto.setLanName("571");                    //本地网
        custViewIdMappingDto.setServCode("1-GB1Jg033776");         //资产集成编码
        custViewIdMappingDto.setCustNum("171100020152783");        //客户编码
        crmPrivil.setResultObject(custViewIdMappingDto);
        return crmPrivil;

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
        for (String str:json.keySet()){
            OpenCustInjectionLabel openCustInjectionLabel = new OpenCustInjectionLabel();
            openCustInjectionLabel.setInjectionLabelName(str);
            openCustInjectionLabel.setLabelValue((String) json.get(str));
            openCustInjectionLabel.setCustId("123");
            String s = JSON.toJSONString(openCustInjectionLabel, SerializerFeature.WriteMapNullValue);
            jsonArray.add(JSONObject.parse(s));
        }

        return jsonArray;
    }

    public static void main(String[] args) {
//        String result = "{SERV_START_DT=2018-08-22, AGE_FLG=[25岁，35岁), PROM_LIST=5100000501100017&8817331330010065}";
//        System.out.println(JSON.toJSONString(changeToLabel(JSONObject.parseObject(result)), SerializerFeature.WriteMapNullValue));
        Map<String,String> map=new HashMap<>();
        map.put("18967120690","1-H1CDl087068");
        map.put("17757146292","1-GCRHV099686");
        map.put("15372446210","1-H19Bt022144");
        map.put("18057140441","3-QS47XDF");
        map.put("13372562971","1-H14Ax078743");
        map.put("15336505771","3-QOQ0Y44");
        map.put("15382331292","1-H16A2016040");
        // map.size()=7
        int ram=(int)(Math.random()*(map.size()-1));
        int i=0;
        for (String str:map.keySet()){
            if(i==ram){
                String str1 = map.get(str);
                System.out.println("当前资产："+str1);
                break;
            }
            i++;
        }
    }

}
