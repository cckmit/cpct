package com.zjtelcom.cpct.count.serviceImpl.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zjtelcom.cpct.count.base.enums.ResultEnum;
import com.zjtelcom.cpct.count.base.util.FormatUtil;
import com.zjtelcom.cpct.count.controller.GroupApiController;
import com.zjtelcom.cpct.count.service.api.GroupApiService;
import com.zjtelcom.cpct.count.service.api.TrialService;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpMapper;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.dto.grouping.TarGrp;
import com.zjtelcom.cpct.util.HttpUtil;
import com.zjtelcom.es.es.entity.model.TrialResponseES;
import com.zjtelcom.es.es.service.EsService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/12/28
 * @Description:分群id试算标签
 */

@Service
@Transactional
public class GroupApiServiceImpl implements GroupApiService {

    private Logger log = LoggerFactory.getLogger(GroupApiServiceImpl.class);

    @Autowired
    private TarGrpMapper tarGrpMapper;

    @Autowired
    private TrialService trialService;

    @Autowired
    private InjectionLabelMapper injectionLabelMapper;


    /**
     * 通过分群id
     * @param paramMap
     * @return
     */
    @Override
    public Map<String, Object> groupTrial(Map<String, Object> paramMap) {
        Map<String, Object> map=new HashMap<>();
        map.put("resultCode",ResultEnum.SUCCESS.getStatus());
        map.put("resultMsg", ResultEnum.SUCCESS);
        log.info("分群请求参数："+paramMap);
        String groupId = String.valueOf(paramMap.get("groupId")) ;

        if(StringUtils.isBlank(groupId)){
            map.put("resultCode",ResultEnum.FAILED.getStatus());
            map.put("resultMsg","分群id信息不能为空");
            return  map;
        }
        //分割字符串判断分群信息是否存在
        List<String> groupList=getGroupList(groupId);
        //依次搜索分群id信息是否存在
        for (String str:groupList){
            TarGrp tarGrp = tarGrpMapper.selectByPrimaryKey(Long.valueOf(str));
            if(tarGrp==null){
                map.put("resultCode",ResultEnum.FAILED.getStatus());
                map.put("resultMsg","分群id "+str+" 信息不存在");
                return  map;
            }
        }
        //调用es的试算服务
        log.info("cpc入参："+paramMap);
        TrialResponseES trialResponseES = trialService.trialTarGrp(paramMap);
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(trialResponseES));
//        String sp="{\"total\":168176109,\"resultCode\":\"200\",\"hitsList\":{\"total\":{\"40\":18579986,\"41\":149596123},\"40\":[{\"CCUST_ID\":\"17201606223\",\"PROM_INTEG_ID\":\"1-I89Gp027077\",\"ACCS_NBR\":\"5723946668\",\"LATN_ID\":\"11\",\"STAT_NAME\":\"现行\",\"MIX_CDSC_FLG\":\"1\",\"_id\":\"1-129OJJL4\"},{\"ACCS_NBR\":\"13362261810\",\"CCUST_ID\":\"1720966071\",\"PROM_INTEG_ID\":\"5-DMM2KAF\",\"LATN_ID\":\"11\",\"STAT_NAME\":\"现行\",\"MIX_CDSC_FLG\":\"1\",\"_id\":\"1-12EK7PZ8\"},{\"ACCS_NBR\":\"18057273188\",\"CCUST_ID\":\"172100019684156\",\"PROM_INTEG_ID\":\"1-I3SGJ052373\",\"LATN_ID\":\"11\",\"STAT_NAME\":\"现行\",\"MIX_CDSC_FLG\":\"1\",\"_id\":\"1-12IBQ14B\"},{\"CCUST_ID\":\"172100016064543\",\"PROM_INTEG_ID\":\"1-12JYS2YO\",\"ACCS_NBR\":\"5728811050\",\"LATN_ID\":\"11\",\"STAT_NAME\":\"现行\",\"MIX_CDSC_FLG\":\"1\",\"_id\":\"1-12JYS34B\"},{\"ACCS_NBR\":\"13336853161\",\"CCUST_ID\":\"172100019721476\",\"PROM_INTEG_ID\":\"1-I5TEe084164\",\"LATN_ID\":\"11\",\"STAT_NAME\":\"现行\",\"MIX_CDSC_FLG\":\"1\",\"_id\":\"1-12KNQKLA\"},{\"CCUST_ID\":\"172100019735302\",\"PROM_INTEG_ID\":\"1-I459P082770\",\"ACCS_NBR\":\"5725016817\",\"LATN_ID\":\"11\",\"STAT_NAME\":\"现行\",\"MIX_CDSC_FLG\":\"1\",\"_id\":\"1-12LC58A2\"},{\"CCUST_ID\":\"172100036650440\",\"PROM_INTEG_ID\":\"3-FGCS8J\",\"ACCS_NBR\":\"5725050089\",\"LATN_ID\":\"11\",\"STAT_NAME\":\"现行\",\"MIX_CDSC_FLG\":\"1\",\"_id\":\"1-15JCKUI4\"},{\"ACCS_NBR\":\"tv157216985216@itv\",\"CCUST_ID\":\"1720260774\",\"PROM_INTEG_ID\":\"1-IA291008596\",\"LATN_ID\":\"11\",\"STAT_NAME\":\"现行\",\"MIX_CDSC_FLG\":\"1\",\"_id\":\"1-15JS0LGF\"},{\"CCUST_ID\":\"172100009614428\",\"PROM_INTEG_ID\":\"5-GVUNKF6\",\"ACCS_NBR\":\"5723088879\",\"LATN_ID\":\"11\",\"STAT_NAME\":\"现行\",\"MIX_CDSC_FLG\":\"1\",\"_id\":\"1-15MW70B2\"},{\"ACCS_NBR\":\"05725305186\",\"CCUST_ID\":\"172100036353096\",\"PROM_INTEG_ID\":\"3-7GTSQ3Z\",\"LATN_ID\":\"11\",\"STAT_NAME\":\"现行\",\"MIX_CDSC_FLG\":\"1\",\"_id\":\"1-15PV6Y2S\"},{\"ACCS_NBR\":\"tv157217018921@itv\",\"CCUST_ID\":\"172100021206691\",\"PROM_INTEG_ID\":\"1-I6LDZ023616\",\"LATN_ID\":\"11\",\"STAT_NAME\":\"现行\",\"MIX_CDSC_FLG\":\"1\",\"_id\":\"1-15PZR3QS\"},{\"ACCS_NBR\":\"tv157217028002@itv\",\"CCUST_ID\":\"172100021209431\",\"PROM_INTEG_ID\":\"3-AIHXT9B\",\"LATN_ID\":\"11\",\"STAT_NAME\":\"现行\",\"MIX_CDSC_FLG\":\"1\",\"_id\":\"1-15R9I6WV\"},{\"ACCS_NBR\":\"13385728188\",\"CCUST_ID\":\"172100038136577\",\"PROM_INTEG_ID\":\"1-H9AE0080457\",\"LATN_ID\":\"11\",\"STAT_NAME\":\"现行\",\"MIX_CDSC_FLG\":\"1\",\"_id\":\"1-15RGUL6Y\"},{\"CCUST_ID\":\"172100021239432\",\"PROM_INTEG_ID\":\"5-FSOMLDL\",\"ACCS_NBR\":\"5728838603\",\"LATN_ID\":\"11\",\"STAT_NAME\":\"现行\",\"MIX_CDSC_FLG\":\"1\",\"_id\":\"1-15TX9DXP\"},{\"ACCS_NBR\":\"tv157217056600@itv\",\"CCUST_ID\":\"172100002880889\",\"PROM_INTEG_ID\":\"1-I2SFW065348\",\"LATN_ID\":\"11\",\"STAT_NAME\":\"现行\",\"MIX_CDSC_FLG\":\"1\",\"_id\":\"1-15WW149P\"},{\"ACCS_NBR\":\"15381283181\",\"CCUST_ID\":\"172100021320616\",\"PROM_INTEG_ID\":\"1-HBOEI062449\",\"LATN_ID\":\"11\",\"STAT_NAME\":\"现行\",\"MIX_CDSC_FLG\":\"1\",\"_id\":\"1-15ZE4VR0\"},{\"ACCS_NBR\":\"05722089686\",\"CCUST_ID\":\"17201500809\",\"PROM_INTEG_ID\":\"5-DYB1UET\",\"LATN_ID\":\"11\",\"STAT_NAME\":\"现行\",\"MIX_CDSC_FLG\":\"1\",\"_id\":\"1-160NQ325\"},{\"ACCS_NBR\":\"tv157217074825@itv\",\"CCUST_ID\":\"172100021356075\",\"PROM_INTEG_ID\":\"1-I43B9039729\",\"LATN_ID\":\"11\",\"STAT_NAME\":\"现行\",\"MIX_CDSC_FLG\":\"1\",\"_id\":\"1-160OU8K6\"},{\"ACCS_NBR\":\"13385822381\",\"CCUST_ID\":\"1720369059\",\"PROM_INTEG_ID\":\"5-EZAHURQ\",\"LATN_ID\":\"11\",\"STAT_NAME\":\"现行\",\"MIX_CDSC_FLG\":\"1\",\"_id\":\"1-162H4APW\"},{\"ACCS_NBR\":\"tv157217090083@itv\",\"CCUST_ID\":\"17201526099\",\"PROM_INTEG_ID\":\"1-HCGBs039648\",\"LATN_ID\":\"11\",\"STAT_NAME\":\"现行\",\"MIX_CDSC_FLG\":\"1\",\"_id\":\"1-163AOY0H\"}],\"41\":[{\"LATN_ID\":\"10\",\"_id\":\"3-12W1FXR9\"},{\"LATN_ID\":\"10\",\"_id\":\"3-IKHOSI0\"},{\"LATN_ID\":\"10\",\"_id\":\"3-IKHWYRQ\"},{\"LATN_ID\":\"10\",\"_id\":\"3-IKJB3V6\"},{\"LATN_ID\":\"10\",\"_id\":\"3-IKKBFP6\"},{\"LATN_ID\":\"10\",\"_id\":\"1-ZALLOBW\"},{\"LATN_ID\":\"10\",\"_id\":\"1-ZALPQES\"},{\"LATN_ID\":\"10\",\"_id\":\"1-ZALZL1B\"},{\"LATN_ID\":\"10\",\"_id\":\"3-6B8VBAH\"},{\"LATN_ID\":\"10\",\"_id\":\"1-I4HCl088410\"},{\"LATN_ID\":\"10\",\"_id\":\"1-IB8Ba039481\"},{\"LATN_ID\":\"10\",\"_id\":\"1-OYTWXEI\"},{\"LATN_ID\":\"10\",\"_id\":\"1-OYU07GD\"},{\"LATN_ID\":\"10\",\"_id\":\"1-OYWN7C5\"},{\"LATN_ID\":\"10\",\"_id\":\"1-OYWS967\"},{\"LATN_ID\":\"10\",\"_id\":\"1-17APPQ8\"},{\"LATN_ID\":\"10\",\"_id\":\"1-17AQ3BP\"},{\"LATN_ID\":\"10\",\"_id\":\"1-17AQF77G\"},{\"LATN_ID\":\"10\",\"_id\":\"1-17AQG19C\"},{\"LATN_ID\":\"10\",\"_id\":\"1-17AQQOA4\"}]},\"resultMsg\":\"试算成功\"}";
//        JSONObject jsonObject=JSONObject.parseObject(sp);
        log.info("转化后json:"+jsonObject);
        JSONObject hitsList = (JSONObject) jsonObject.get("hitsList");
        JSONObject array=new JSONObject();
        for(String str: hitsList.keySet()){
             if(!str.equals("total")&&!str.equals("cloumn")){
                 System.out.println("当前str:"+str);
                 JSONArray jsonArray= (JSONArray) hitsList.get(str);
//                 System.out.println("文件："+o);
//                 JSONArray jsonArray = (JSONArray) JSONArray.parse(JSON.toJSONString(o));
                 log.info("解析后："+jsonArray);
                 JSONObject json= (JSONObject) jsonArray.get(0);
                 JSONObject news=new JSONObject();
                 for (String s:json.keySet()){
                     Label label = injectionLabelMapper.selectByLabelCode(s);
                     if(label!=null){
                         news.put(s,label.getInjectionLabelName());
                     }
                 }
                 array.put(str,news);
             }
        }
        jsonObject.put("column",array);
        jsonObject.put("resultCode",ResultEnum.SUCCESS.getStatus());

//        log.info("试算返回信息"+ JSONObject.parseObject(JSON.toJSONString(trialResponseES)));
//        //
//        Map<String, Object> stringObjectMap = FormatUtil.objectToMap(trialResponseES);
//        log.info("转化map后："+stringObjectMap);
        log.info("试算返回信息: "+ jsonObject);
        return jsonObject;
    }





    /**
     * 得到分群id集合
     * @param groupId
     * @return
     */
    public List<String> getGroupList(String groupId){
        List<String> groupList=new ArrayList<>();
        if(groupId.contains(";")){
            String[] split = groupId.split(";");
            for (String s:split){
                groupList.add(s);
            }
        }else if(groupId.contains(",")){
            String[] split = groupId.split(",");
            for (String s:split){
                groupList.add(s);
            }
        }else{
            groupList.add(groupId);
        }



        return groupList;

    }


    /**
     * 验证判断身份权限
     * @param paramMap
     * @return
     */
    public Map<String,String> verification(Map<String, Object> paramMap){
        Map<String,String> map=new HashMap<>();
        String channel = (String) paramMap.get("channel");
        String channelToken = (String) paramMap.get("channelToken");


        return map;

    }







    public static void main(String[] args) {

    }



}
