package com.zjtelcom.cpct.controller.report;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.service.report.ActivityStatisticsService;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
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
                    logger.error("随销报表查询异常", e);
                }
//                HSSFWorkbook wb = new HSSFWorkbook(); //创建excel
//                Sheet sheetName = wb.createSheet("明知山有虎，知难而退？");//新建表 数据随销报表
                String sheetName = "随销报表";
                String[] title = {"活动名称", "活动状态", "活动类型", "活动编码", "活动渠道", "活动生效时间", "活动失效时间",
                        "关单规则名称", "所属地市","客户接触数", "商机推荐数","商机成功数","客触转化率","商机转化率",
                        "收入低迁数","收入低迁率", "门店有销率","是否框架子活动"};
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
                                    content[i][9] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("商机推荐数")){
                                    //商机推荐数 orderNum
                                    content[i][10] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("商机成功数")){
                                    //商机成功数 orderSuccessNum
                                    content[i][11] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("商机转化率")){
                                    //商机转化率 orderRate
                                    content[i][12] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("客触转化率")){
                                    //客触转化率 contactRate
                                    content[i][13] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("收入低迁数")){
                                    //收入低迁数 revenueReduceNum
                                    content[i][14] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("收入低迁率")){
                                    //收入低迁率 revenueReduceRate
                                    content[i][15] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("门店有销率")){
                                    //门店有销率 orgChannelRate
                                    content[i][16] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("是否框架子活动")){
                                    //是否框架子活动 yesOrNo
                                    content[i][17] = String.valueOf(map3.get("nub"));
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
                String[] title = {"活动名称", "活动状态", "活动类型", "活动编码", "活动渠道", "活动生效时间", "活动失效时间",
                        "关单规则名称","所属地市","批次编码","派单方式","派单数", "接单数","外呼数","成功数","接单率","外呼率","转化率",
                        "收入低迁数","收入低迁率","门店有销率","是否框架子活动","处理数","处理率"};
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
                                    content[i][11] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("接单数")){
                                    content[i][12] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("外呼数")){
                                    content[i][13] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("成功数")){
                                    content[i][14] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("接单率")){
                                    content[i][15] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("外呼率")){
                                    content[i][16] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("转化率")){
                                    content[i][17] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("收入低迁数")){
                                    content[i][18] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("收入低迁率")){
                                    content[i][19] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("门店有销率")){
                                    content[i][20] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("是否框架子活动")){
                                    content[i][21] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("处理数")){
                                    content[i][22] = String.valueOf(map3.get("nub"));
                                }else if (name.equals("处理率")){
                                    content[i][23] = String.valueOf(map3.get("nub"));
                                }
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
        //活动状态 2002 已发布 2008 调整中
        if (stringObjectHashMap.get("statusCd").toString().equals("2002")) {
            content[i][1] = String.valueOf("已发布");
        }else {
            content[i][1] = String.valueOf("调整中");
        }
        //活动类型 mktCampaignType
        content[i][2] = String.valueOf(stringObjectHashMap.get("mktCampaignType").toString());
        //活动编码 mktActivityBnr
        content[i][3] = String.valueOf(stringObjectHashMap.get("mktActivityBnr").toString());
        //活动渠道 channel
        content[i][4] = String.valueOf(stringObjectHashMap.get("channel").toString());
        //活动生效时间 beginTime
        content[i][5] = String.valueOf(stringObjectHashMap.get("beginTime").toString());
        //活动失效时间 endTime
        content[i][6] = String.valueOf(stringObjectHashMap.get("endTime").toString());
        content[i][7] = String.valueOf(stringObjectHashMap.get("mktCloseRuleName").toString());
        content[i][8] = String.valueOf(stringObjectHashMap.get("area").toString());
        Object batchNum = stringObjectHashMap.get("batchNum");
        if ( batchNum!= null && "" != batchNum){
            content[i][9] = String.valueOf(stringObjectHashMap.get("batchNum").toString());
        }
        Object dispatchForm = stringObjectHashMap.get("dispatchForm");
        if (dispatchForm!=null && "" != dispatchForm){
            content[i][10] = String.valueOf(stringObjectHashMap.get("dispatchForm").toString());
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

}
