package com.zjtelcom.cpct.util;

import java.util.HashMap;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

public class AcitvityParams {

    public static Map<String,Object> ActivityParamsByMap(Map<String,Object> params) {
        HashMap<String, Object> paramMap = new HashMap<>();
        Object startDate = params.get("startDate");
        Object endDate = params.get("endDate");
        if (endDate != null && endDate != ""&& startDate !=null && startDate!="") {
            //起始统计日期(YYYYMMDD)必填 dubbo接口用
            paramMap.put("startDate", startDate.toString().replaceAll("-", ""));
            //结束时间取当前时间减一天的时间
            paramMap.put("endDate", endDate.toString().replaceAll("-", ""));
//            String preDay = DateUtil.getPreDay(1);
//            paramMap.put("endDate", preDay.replaceAll("-", ""));
        } else {
            paramMap.put("resultCode", CODE_FAIL);
            paramMap.put("resultMsg", "时间是必填字段");
            return paramMap;
        }
        //活动类型
        Object mktCampaignType = params.get("mktCampaignType");
        if (mktCampaignType!=null && ""!=mktCampaignType){
            paramMap.put("mktCampaignType",mktCampaignType.toString());
        }else {
            paramMap.put("resultCode", CODE_FAIL);
            paramMap.put("resultMsg", "活动类型是必须的");
            return paramMap;
        }

        //省公司(必填)  800000000004
        Object orglevel1 = params.get("orglevel1");
        if (orglevel1!=null && orglevel1!=""){
            if (orglevel1.toString().equals("800000000004")){
                paramMap.put("orglevel1",orglevel1);
//                paramMap.put("orglevel2","all");
            }else {
                paramMap.put("orglevel1","800000000004");
                paramMap.put("orglevel2",orglevel1);
//                paramMap.put("orglevel3","all");
            }
        }else {
            paramMap.put("resultCode", CODE_FAIL);
            paramMap.put("resultMsg", "地市信息是必须的");
            return paramMap;
        }

        //渠道编码(必填,ALL表示所有,多个用逗号隔开)
        Object channelCode = params.get("channelCode");
        if (channelCode!=null && channelCode!=""){
            paramMap.put("channelCode",channelCode);
        }else {
            paramMap.put("channelCode","all");
        }
        //活动ID(必填,ALL表示所有,多个用逗号隔开)
        Object mktCampaignId = params.get("mktCampaignId");
        if (mktCampaignId!=null && mktCampaignId!=""){
            paramMap.put("mktCampaignId",mktCampaignId);
        }else {
            paramMap.put("mktCampaignId","all");
        }
        //统计维度(0:按渠道，1按地市,2按活动) 不用就不传
        Object rptType = params.get("rptType");
        if (rptType!=null && rptType!=""){
            paramMap.put("rptType",rptType);
        }else {
            paramMap.put("rptType","");
        }
        //是否返回历史活动(0:否,1:是)
        Object isHis = params.get("isHis");
//        String isHis = "1";
        if (isHis!=null && isHis!=""){
            paramMap.put("isHis","1");
        }else {
            paramMap.put("isHis","0");
        }
        //排序字段 contactRate 转化率默认
        Object sortColumn = params.get("sortColumn");
        if (sortColumn!=null && sortColumn!=""){
            paramMap.put("sortColumn",sortColumn);
        }else {
            paramMap.put("sortColumn","contactRate");
        }
        //排序类型(升序:asc,降序desc)
        Object sortType = params.get("sortType");
        if (sortType!=null && sortType!=""){
            paramMap.put("sortType",sortType);
        }else {
            paramMap.put("sortType","desc");
        }
        //每页记录条数
        Object pageSize = params.get("pageSize");
        if (pageSize!=null && pageSize!=""){
            paramMap.put("pageSize",pageSize);
        }else {
            paramMap.put("pageSize","10");
        }
        //当前页码
        Object currenPage = params.get("currenPage");
        if (currenPage!=null && currenPage!=""){
            paramMap.put("currenPage",currenPage);
        }else {
            paramMap.put("currenPage","1");
        }
        paramMap.put("resultCode", CODE_SUCCESS);
        return paramMap;
    }
}
