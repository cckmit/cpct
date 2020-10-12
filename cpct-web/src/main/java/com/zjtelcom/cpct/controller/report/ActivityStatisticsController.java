package com.zjtelcom.cpct.controller.report;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dao.channel.ContactChannelMapper;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import com.zjtelcom.cpct.service.grouping.TrialOperationService;
import com.zjtelcom.cpct.service.report.ActivityStatisticsService;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**  2019/11/7
 *  报表展示数据 老接口
 */


@RestController
@RequestMapping("${adminPath}/report")
public class ActivityStatisticsController extends BaseController {


    @Autowired
    ActivityStatisticsService activityStatisticsService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private TrialOperationService trialOperationService;

    /**
     *
     * 根据用户登入信息 权限定位 C2 C3 C4 C5
     * 根据父节点查询字节点下所有节点返回
     */
    @PostMapping("/getStoreForUser")
    @CrossOrigin
    public String getStoreForUser(@RequestBody Map<String, Object> params) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = activityStatisticsService.getStoreForUser(params);
        } catch (Exception e) {
            logger.error("[op:ActivityStatisticsController] fail to listEvents for getStoreForUser = {}! Exception: ", JSONArray.toJSON(params), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 根据选中的父节点 获取父节点下所有子节点下的门店信息
     * A_ORG_ID Z_ORG_ID（关联关系）  org_rel （表）
     * 递归 获取 门店信息
     */
    @PostMapping("/getStore")
    @CrossOrigin
    public String getStore(@RequestBody Map<String, Object> params) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = activityStatisticsService.getStore(params);
        } catch (Exception e) {
            logger.error("[op:ActivityStatisticsController] fail to listEvents for getStore = {}! Exception: ", JSONArray.toJSON(params), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     *  营销活动获取渠道信息
     *  //实时（随销） 5,6 问正义
     *  // 批量（派单） 4,5 问正义
     * @param params
     * @return
     */
    @PostMapping("/getChannel")
    @CrossOrigin
    public Map<String,Object> getChannel(@RequestBody Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try {
            map = activityStatisticsService.getChannel(params);
        } catch (Exception e) {
            logger.error("[op:ActivityStatisticsController] fail to listEvents for getChannel = {}! Exception: ", JSONArray.toJSON(params), e);
            return map;
        }
        return map;
    }


    //销报表查询接口
    @PostMapping("/getRptEventOrder")
    @CrossOrigin
    public Map<String,Object> getRptEventOrder(@RequestBody Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try {
            map = activityStatisticsService.getRptEventOrder(params);
        } catch (Exception e) {
            logger.error("[op:ActivityStatisticsController] fail to listEvents for getRptEventOrder = {}! Exception: ", JSONArray.toJSON(params), e);
            return map;
        }
        return map;
    }


    //活动报表查询接口
    @PostMapping("/getRptBatchOrder")
    @CrossOrigin
    public Map<String,Object> getRptBatchOrder(@RequestBody Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try {
            map = activityStatisticsService.getRptBatchOrder(params);
        } catch (Exception e) {
            logger.error("[op:ActivityStatisticsController] fail to listEvents for getRptBatchOrder = {}! Exception: ", JSONArray.toJSON(params), e);
            return map;
        }
        return map;
    }

    @PostMapping("/queryRptBatchOrderTest")
    @CrossOrigin
    public Map<String,Object> queryRptBatchOrderTest(@RequestBody Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try {
            map = activityStatisticsService.queryRptBatchOrderTest(params);
        } catch (Exception e) {
            logger.error("[op:ActivityStatisticsController] fail to listEvents for queryRptBatchOrder = {}! Exception: ", JSONArray.toJSON(params), e);
            return map;
        }
        return map;
    }


    @PostMapping("/getMktCampaignDetails")
    @CrossOrigin
    public Map<String,Object> getMktCampaignDetails(@RequestBody Map<String, Object> params){
        return activityStatisticsService.getMktCampaignDetails(params);
    }

    // xyl 活动报表模糊搜索 type 1000 随销  2000 派单
    @PostMapping("/getActivityStatisticsByName")
    @CrossOrigin
    public Map<String,Object> getActivityStatisticsByName(@RequestBody Map<String, Object> params){
        return activityStatisticsService.getActivityStatisticsByName(params);
    }


    //xyl 随销报表导出成excel文件1000,  活动报表导出成excel文件2000
    @GetMapping("/exportExcel")
    @CrossOrigin
    public void exportExcel(HttpServletRequest request, HttpServletResponse response,String reqId,String type) throws UnsupportedEncodingException {
        Map<String, Object> map = new HashMap<>();
//        String types = "1000";
        Object o = redisUtils.get(reqId);
        if (o!=null && o!=""){
            HashMap<String, Object> paramMap = (HashMap<String, Object>)o;
//        if (1==1){
            if (StringUtils.isNotBlank(type) && "1000".equals(type)){
                //随销报表
                try {
                    if (paramMap.get("mktCampaignId")!=null && paramMap.get("mktCampaignId").toString().contains(",")){
                        paramMap.put("mktCampaignId","");
                    }
                    map = activityStatisticsService.getRptEventOrder(paramMap);
//                    String str = "{\"total\":742,\"resultCode\":\"200\",\"pageSize\":5,\"page\":1,\"resultMsg\":[{\"mktCampaignName\":\"智能组网201812\",\"mktCampaignType\":\"营销活动\",\"channel\":\"iSale\",\"statusCd\":\"2008\",\"beginTime\":\"2018-12-20\",\"endTime\":\"2019-09-30\",\"mktActivityBnr\":\"MKT000020\",\"statistics\":[{\"name\":\"客户接触数\",\"nub\":1},{\"name\":\"商机推荐数\",\"nub\":1},{\"name\":\"商机成功数\",\"nub\":0},{\"name\":\"客触转化率\",\"nub\":\"0.00%\"},{\"name\":\"商机转化率\",\"nub\":\"0.00%\"},{\"name\":\"收入低迁数\",\"nub\":0},{\"name\":\"收入低迁率\",\"nub\":\"0.00%\"},{\"name\":\"门店有销率\",\"nub\":\"0.00%\"},{\"name\":\"是否框架子活动\",\"nub\":\"否\"}]},{\"mktCampaignName\":\"智能组网201812\",\"mktCampaignType\":\"营销活动\",\"channel\":\"iSale\",\"statusCd\":\"2008\",\"beginTime\":\"2018-12-20\",\"endTime\":\"2019-09-30\",\"mktActivityBnr\":\"MKT000020\",\"statistics\":[{\"name\":\"客户接触数\",\"nub\":6},{\"name\":\"商机推荐数\",\"nub\":6},{\"name\":\"商机成功数\",\"nub\":0},{\"name\":\"客触转化率\",\"nub\":\"0.00%\"},{\"name\":\"商机转化率\",\"nub\":\"0.00%\"},{\"name\":\"收入低迁数\",\"nub\":0},{\"name\":\"收入低迁率\",\"nub\":\"0.00%\"},{\"name\":\"门店有销率\",\"nub\":\"0.00%\"},{\"name\":\"是否框架子活动\",\"nub\":\"否\"}]},{\"mktCampaignName\":\"智能组网201812\",\"mktCampaignType\":\"营销活动\",\"channel\":\"iSale\",\"statusCd\":\"2008\",\"beginTime\":\"2018-12-20\",\"endTime\":\"2019-09-30\",\"mktActivityBnr\":\"MKT000020\",\"statistics\":[{\"name\":\"客户接触数\",\"nub\":7},{\"name\":\"商机推荐数\",\"nub\":8},{\"name\":\"商机成功数\",\"nub\":0},{\"name\":\"客触转化率\",\"nub\":\"0.00%\"},{\"name\":\"商机转化率\",\"nub\":\"0.00%\"},{\"name\":\"收入低迁数\",\"nub\":0},{\"name\":\"收入低迁率\",\"nub\":\"0.00%\"},{\"name\":\"门店有销率\",\"nub\":\"0.00%\"},{\"name\":\"是否框架子活动\",\"nub\":\"否\"}]},{\"mktCampaignName\":\"智能组网201812\",\"mktCampaignType\":\"营销活动\",\"channel\":\"爱装维\",\"statusCd\":\"2008\",\"beginTime\":\"2018-12-20\",\"endTime\":\"2019-09-30\",\"mktActivityBnr\":\"MKT000020\",\"statistics\":[{\"name\":\"客户接触数\",\"nub\":2},{\"name\":\"商机推荐数\",\"nub\":2},{\"name\":\"商机成功数\",\"nub\":0},{\"name\":\"客触转化率\",\"nub\":\"0.00%\"},{\"name\":\"商机转化率\",\"nub\":\"0.00%\"},{\"name\":\"收入低迁数\",\"nub\":0},{\"name\":\"收入低迁率\",\"nub\":\"0.00%\"},{\"name\":\"门店有销率\",\"nub\":\"0.00%\"},{\"name\":\"是否框架子活动\",\"nub\":\"否\"}]},{\"mktCampaignName\":\"智能组网201812\",\"mktCampaignType\":\"营销活动\",\"channel\":\"iSale\",\"statusCd\":\"2008\",\"beginTime\":\"2018-12-20\",\"endTime\":\"2019-09-30\",\"mktActivityBnr\":\"MKT000020\",\"statistics\":[{\"name\":\"客户接触数\",\"nub\":20},{\"name\":\"商机推荐数\",\"nub\":20},{\"name\":\"商机成功数\",\"nub\":0},{\"name\":\"客触转化率\",\"nub\":\"0.00%\"},{\"name\":\"商机转化率\",\"nub\":\"0.00%\"},{\"name\":\"收入低迁数\",\"nub\":0},{\"name\":\"收入低迁率\",\"nub\":\"0.00%\"},{\"name\":\"门店有销率\",\"nub\":\"0.00%\"},{\"name\":\"是否框架子活动\",\"nub\":\"否\"}]}]}";
//                    map = (Map)JSON.parse(str);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("随销报表查询异常", e);
                }
//                HSSFWorkbook wb = new HSSFWorkbook(); //创建excel
//                Sheet sheetName = wb.createSheet("明知山有虎，知难而退？");//新建表 数据随销报表
                String sheetName = "随销报表";
                String[] title = {"活动名称", "活动状态", "活动类型", "活动编码", "活动主题",	"活动目录", "活动渠道", "活动生效时间", "活动失效时间",
                        "关单规则名称", "所属地市","客户接触数", "商机推荐数","商机成功数","客触转化率","商机转化率",
                        "收入低迁数","收入低迁率", "门店有销率","是否框架子活动","人员Y编码","人员姓名"};
                String fileName = "随销报表"+ DateUtil.formatDate(new Date())+".xls"; //表名
                //开始解析
                Object resultMsg = map.get("resultMsg");
                if (resultMsg!=null){
                    List<HashMap<String, Object>> hashMaps = ( List<HashMap<String, Object>>)resultMsg;
                    String[][] content = new String[hashMaps.size()][title.length+1];
                    try {
                        for (int i = 0; i < hashMaps.size(); i++) {
                            Map map2 = fixedMap(hashMaps, content, i);
                            List<HashMap<String, Object>> statisicts = (List<HashMap<String, Object>>) map2.get("statistics");
                            for (int j = 0; j < statisicts.size(); j++) {
                                HashMap<String, Object> map3 = statisicts.get(j);
                                String name = map3.get("name").toString();
                                if (name.equals("客户接触数")){
                                    //客户接触数 contactNum
                                    content[i][11] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("商机推荐数")){
                                    //商机推荐数 orderNum
                                    content[i][12] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("商机成功数")){
                                    //商机成功数 orderSuccessNum
                                    content[i][13] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("商机转化率")){
                                    //商机转化率 orderRate
                                    content[i][14] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("客触转化率")){
                                    //客触转化率 contactRate
                                    content[i][15] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("收入低迁数")){
                                    //收入低迁数 revenueReduceNum
                                    content[i][16] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("收入低迁率")){
                                    //收入低迁率 revenueReduceRate
                                    content[i][17] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("门店有销率")){
                                    //门店有销率 orgChannelRate
                                    content[i][18] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("是否框架子活动")){
                                    //是否框架子活动 yesOrNo
                                    content[i][19] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("人员Y编码")){
                                    //是否框架子活动 yesOrNo
                                    content[i][20] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("人员姓名")){
                                    //是否框架子活动 yesOrNo
                                    content[i][21] = String.valueOf(map3.get("nub"));
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("随销报表拼接参数异常",e);
                    }
                    excelWrite(response, sheetName, title, fileName, content, "随销报表导出成excel文件异常");
                }
            } else if (StringUtils.isNotBlank(type) && "2000".equals(type)){
                //活动报表
                try {
                    if (paramMap.get("mktCampaignId")!=null && paramMap.get("mktCampaignId").toString().contains(",")){
                        paramMap.put("mktCampaignId","");
                    }
                    map = activityStatisticsService.getRptBatchOrder(paramMap);
                } catch (Exception e) {
                    logger.error("活动报表查询异常", e);
                }
                String sheetName = "派单报表";
                String[] title = {"活动名称", "活动状态", "活动类型", "活动编码", "活动主题", " 活动目录", "活动渠道", "活动生效时间", "活动失效时间",
                        "关单规则名称","所属地市","批次编码","派单方式","派单数","外呼数", "处理数", "过扰关单数" ,"成功数","回单数","回单率","外呼率","转化率",
                        "处理率", "过扰关单率", "门店有销率", "是否框架子活动", "对应框架母活动编码", "对应母框架活动生效时间",
                        "成功/已接触,成功办理","成功/转商机单","失败/没有需求","失败/价格太高","失败/已转他网", "失败/拒绝","营销过滤/已办理",
                        "二次营销/有意向","二次营销/犹豫中","二次营销/接触失败","二次营销/二次营销","接触成功量","接触成功率","短信过扰差值","黑名单过滤个数","销售品过滤个数"};
                String fileName = "派单报表"+ DateUtil.formatDate(new Date())+".xls"; //表名
                //开始解析
                Object resultMsg = map.get("resultMsg");
                if (resultMsg!=null) {
                    List<HashMap<String, Object>> hashMaps = (List<HashMap<String, Object>>) resultMsg;
                    String[][] content = new String[hashMaps.size()][title.length + 1];
                    try{
                        for (int i = 0; i < hashMaps.size(); i++) {
                            Map map2 = fixedMap(hashMaps, content, i);
                            List<HashMap<String, Object>> statisicts = (List<HashMap<String, Object>>) map2.get("statistics");
                            for (int j = 0; j < statisicts.size(); j++) {
//                                JSONObject jsonObject2 = JSON.parseObject(String.valueOf(statisicts.get(j)));
//                                Map map3 = JSONObject.parseObject(jsonObject2.toJSONString(), Map.class);
                                HashMap<String, Object> map3 = statisicts.get(j);
                                String name = map3.get("name").toString();
                                if (name.equals("派单数")){
                                    content[i][13] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("外呼数")){
                                    content[i][14] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("处理数")){
                                    content[i][15] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("过扰关单数")){
                                    content[i][16] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("成功数")){
                                    content[i][17] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("回单数")){
                                    content[i][18] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("回单率")){
                                    content[i][19] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("外呼率")){
                                    content[i][20] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("转化率")){
                                    content[i][21] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("处理率")){
                                    content[i][22] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("过扰关单率")){
                                    content[i][23] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("门店有销率")){
                                    content[i][24] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("是否框架子活动")){
                                    content[i][25] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("对应框架母活动编码")){
                                    content[i][26] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("对应母框架活动生效时间")){
                                    content[i][27] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("成功/已接触,成功办理")){
                                    content[i][28] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("成功/转商机单")){
                                    content[i][29] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("失败/没有需求")){
                                    content[i][30] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("失败/价格太高")){
                                    content[i][31] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("失败/已转他网")){
                                    content[i][32] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("失败/拒绝")){
                                    content[i][33] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("营销过滤/已办理")){
                                    content[i][34] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("二次营销/有意向")){
                                    content[i][35] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("二次营销/犹豫中")){
                                    content[i][36] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("二次营销/接触失败")){
                                    content[i][37] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("二次营销/二次营销")){
                                    content[i][38] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("接触成功量")){
                                    content[i][39] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("接触成功率")){
                                    content[i][40] = String.valueOf(map3.get("nub"));
                                }
                            }
                            // 获取批次号
                            String batchNum = content[i][9] == null ? "" : content[i][9];
                            logger.info("batchNum--->"  + batchNum );
                            TrialOperation trialOperation = trialOperationService.selectByBatchNum(batchNum);
                            if (trialOperation!=null){
                                // 短信过扰差值
                                content[i][41] = trialOperation.getSubNum() ==null ? "0" : trialOperation.getSubNum();
                                // 黑名单过滤个数
                                content[i][42] = trialOperation.getBeforeNum() ==null ? "0" : trialOperation.getBeforeNum();
                                // 销售品过滤个数
                                content[i][43] = trialOperation.getEndNum() ==null ? "0" : trialOperation.getEndNum();
                            } else {
                                // 短信过扰差值
                                content[i][41] = "0";
                                // 黑名单过滤个数
                                content[i][42] = "0";
                                // 销售品过滤个数
                                content[i][43] = "0";
                            }
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                        logger.error("随销报表拼接参数异常",e);
                    }
                    excelWrite(response, sheetName, title, fileName, content, "派单报表导出成excel文件异常");
                }
            }
        }
    }

    private void excelWrite(HttpServletResponse response, String sheetName, String[] title, String fileName, String[][] content, String 派单报表导出成excel文件异常) {
        HSSFWorkbook wb = getHSSFWorkbook(sheetName, title, content, null);
        try {
            // 响应到客户端
            this.setResponseHeader(response, fileName);
            OutputStream os = response.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(派单报表导出成excel文件异常, e);
        }
    }

    private Map fixedMap(List<HashMap<String, Object>> hashMaps, String[][] content, int i) {
        HashMap<String, Object> stringObjectHashMap = hashMaps.get(i);
        //活动名称
        content[i][0] = String.valueOf(stringObjectHashMap.get("mktCampaignName").toString());
        //活动状态 2002 已发布 2008 调整中 2010 已下线
        if (stringObjectHashMap.get("statusCd").toString().equals("2002")) {
            content[i][1] = String.valueOf("已发布");
        } else  if (stringObjectHashMap.get("statusCd").toString().equals("2008")) {
            content[i][1] = String.valueOf("调整中");
        } else  if (stringObjectHashMap.get("statusCd").toString().equals("2010")) {
            content[i][1] = String.valueOf("已下线");
        }
        //活动类型 mktCampaignType
        content[i][2] = String.valueOf(stringObjectHashMap.get("mktCampaignType").toString());
        //活动编码 mktActivityBnr
        content[i][3] = String.valueOf(stringObjectHashMap.get("mktActivityBnr").toString());
        // 活动主题
        content[i][4] = String.valueOf(stringObjectHashMap.get("theMeValue").toString());
        // 活动目录
        content[i][5] = String.valueOf(stringObjectHashMap.get("catalogItemName").toString());
        //活动渠道 channel
        content[i][6] = String.valueOf(stringObjectHashMap.get("channel").toString());
        //活动生效时间 beginTime
        content[i][7] = String.valueOf(stringObjectHashMap.get("beginTime").toString());
        //活动失效时间 endTime
        content[i][8] = String.valueOf(stringObjectHashMap.get("endTime").toString());
        content[i][9] = String.valueOf(stringObjectHashMap.get("mktCloseRuleName").toString());
        content[i][10] = String.valueOf(stringObjectHashMap.get("area").toString());
        Object batchNum = stringObjectHashMap.get("batchNum");
        if ( batchNum!= null && "" != batchNum){
            content[i][11] = String.valueOf(stringObjectHashMap.get("batchNum").toString());
        }
        Object dispatchForm = stringObjectHashMap.get("dispatchForm");
        if (dispatchForm!=null && "" != dispatchForm){
            content[i][12] = String.valueOf(stringObjectHashMap.get("dispatchForm").toString());
        }
        return stringObjectHashMap;
    }


    public static HSSFWorkbook getHSSFWorkbook(String sheetName, String[] title, String[][] values, HSSFWorkbook wb) {

        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        if (wb == null)
            wb = new HSSFWorkbook();

        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet(sheetName);

        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
        HSSFRow row = sheet.createRow(0);

        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
//        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

        //声明列对象
        HSSFCell cell = null;

        //创建标题
        for (int i = 0; i < title.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }

        //创建内容
        for (int i = 0; i < values.length; i++) {
            row = sheet.createRow(i + 1);
            for (int j = 0; j < values[i].length; j++) {
                //将内容按顺序赋给对应的列对象s
                row.createCell(j).setCellValue(values[i][j]);
            }
        }
        return wb;
    }

    public void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
//            response.setContentType("application/vnd.ms-excel");
//            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setContentType("application/ms-excel");
            response.setHeader("Content-Disposition",
                    "inline;filename="+
                            new String(fileName.getBytes(),"iso8859-1"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //consumer_log数据表删除数据
    @PostMapping("/delectConsumerlogByDate")
    @CrossOrigin
    public Map<String,Object> delectConsumerlogByDate(@RequestBody Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try {
            map = activityStatisticsService.delectConsumerlogByDate(params);
        } catch (Exception e) {
            logger.error("[op:ActivityStatisticsController] fail to listEvents for delectConsumerlogByDate = {}! Exception: ", JSONArray.toJSON(params), e);
            return map;
        }
        return map;
    }


    //Level 6 7 选择实体门店和营业员 传level 5 6
    @PostMapping("/getSalesClerk")
    @CrossOrigin
    public Map<String,Object> getSalesClerk(@RequestBody Map<String, Object> params){
        Map<String, Object> map = new HashMap<>();
        try {
            map = activityStatisticsService.getSalesClerk(params);
        } catch (Exception e) {
            logger.error("[op:ActivityStatisticsController] fail to listEvents for getSalesClerk = {}! Exception: ", JSONArray.toJSON(params), e);
            return map;
        }
        return map;
    }


    //随销清单查询接口
    @PostMapping("/queryEventOrderByReport")
    @CrossOrigin
    public Map<String,Object> queryEventOrderByReport(@RequestBody Map<String, String> params){
        Map<String, Object> map = new HashMap<>();
        try {
            map = activityStatisticsService.queryEventOrderByReport(params);
        } catch (Exception e) {
            logger.error("[op:ActivityStatisticsController] fail to listEvents for queryEventOrderByReport = {}! Exception: ", JSONArray.toJSON(params), e);
            return map;
        }
        return map;
    }


    //渠道报表查询接口
    @PostMapping("/queryEventOrderChlListByReport")
    @CrossOrigin
    public Map<String,Object> queryEventOrderChlListByReport(@RequestBody Map<String, String> params){
        Map<String, Object> map = new HashMap<>();
        try {
            map = activityStatisticsService.queryEventOrderChlListByReport(params);
        } catch (Exception e) {
            logger.error("[op:ActivityStatisticsController] fail to listEvents for queryEventOrderChlListByReport = {}! Exception: ", JSONArray.toJSON(params), e);
            return map;
        }
        return map;
    }


    /**
     * 分渠道报表导出成excel todo 未测试
     */
    @GetMapping("/exportChannelExcel")
    @CrossOrigin
    public void exportChannelExcel(HttpServletRequest request, HttpServletResponse response, String reqId)
            throws UnsupportedEncodingException {
        //根据reqId获取查询接口参数
        Map<String, Object> map = new HashMap<>();
        Object o = redisUtils.get(reqId);
        if (o != null && o != "") {
            HashMap<String, String> paramMap = (HashMap<String, String>) o;
            try {
                map = activityStatisticsService.queryEventOrderChlListByReport(paramMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String sheetName = "分渠道报表";
            String[] title = {"区域名称", "渠道名称", "活动目录", "活动类型", "客触数",
                    "成功数", "成功率", "商机数", "回单数", "商机回单率"};
            String fileName = "分渠道报表" + DateUtil.formatDate(new Date()) + ".xls"; //表名

            Object resultMsg = map.get("resultMsg");
            if (resultMsg != null) {
                List<Map<String, Object>> hashMaps = (List<Map<String, Object>>) resultMsg;
                String[][] content = new String[hashMaps.size()][title.length + 1];
                try {
                    for (int i = 0; i < hashMaps.size(); i++) {
                        //区域名称 orgName
                        content[i][0] = String.valueOf(hashMaps.get(i).get("orgName").toString());
                        //渠道名称 channelCode
                        content[i][1] = String.valueOf(hashMaps.get(i).get("channelName").toString());
                        //活动目录 theme
                        content[i][2] = String.valueOf(hashMaps.get(i).get("theme").toString());
                        //活动类型  isee_flg 是否沙盘 statType 这个!
                        String statType = hashMaps.get(i).get("statType").toString();
                        if (statType.equals("0")){
                            content[i][3] = String.valueOf("所有");
                        }else {
                            content[i][3] = String.valueOf("是");
                        }

                        //客触数 contactNum
                        content[i][4] = String.valueOf(hashMaps.get(i).get("contactNum").toString());
                        //成功数 orderSuccessNum
                        content[i][5] = String.valueOf(hashMaps.get(i).get("orderSuccessNum").toString());
                        //成功率 orderRate
                        content[i][6] = String.valueOf(hashMaps.get(i).get("orderRate").toString());
                        //商机数 orderNum
                        content[i][7] = String.valueOf(hashMaps.get(i).get("orderNum").toString());
                        //回单数 resultNum
                        content[i][8] = String.valueOf(hashMaps.get(i).get("resultNum").toString());
                        //商机回单率 resultRate
                        content[i][9] = String.valueOf(hashMaps.get(i).get("resultRate").toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("分渠道报表", e);
                }
                excelWrite(response, sheetName, title, fileName, content, "分渠道报表导出成excel文件异常");
            }
        }
    }
}
