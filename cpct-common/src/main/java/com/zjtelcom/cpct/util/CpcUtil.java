/*
 * 文件名：CpcUtil.java
 * 版权：Copyright by 南京星邺汇捷网络科技有限公司
 * 描述：
 * 修改人：taowenwu
 * 修改时间：2017年10月27日
 * 修改内容：
 */

package com.zjtelcom.cpct.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import com.zjtelcom.cpct.constants.ResponseCode;
import com.zjtelcom.cpct.dto.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedRuntimeException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Date;


/**
 * 集团CPC工具类
 * @author taowenwu
 * @version 1.0
 * @see CpcUtil
 * @since JDK1.7
 */

public final class CpcUtil {
    private CpcUtil() {}

    /**
     * 服务提供者编码
     */
    public static final String DSTSYSID = "1000000038";

    /**
     * appkey
     */
    public static final String APPKEY = "6001040005";
    
    /**
     * SecretKey 
     */
    public static final String SECRETKEY = "6001040005ZJTEST";

    public static Logger log = LoggerFactory.getLogger(CpcUtil.class);


    /**
     * 创建服务应答对象
     *
     * @param resultCode 0 ：成功，其他值：失败
     * @param resultMsg “处理成功”“签名验证不通过..”“……”
     * @param resultObject 响应附加内容
     * @param sign 签名
     * @param transactionID 唯一的交易流水号
     *
     * @return 服务应答对象
     * @see
     */
    public static CpcGroupResponse buildResponse(String resultCode, String resultMsg,
                                                 Object resultObject, String sign,
                                                 String transactionID) {
        CpcGroupResponse cpcGroupResponse = new CpcGroupResponse();
        ContractRespRoot respRoot = new ContractRespRoot();

        SvcRespCont svcRespCont = new SvcRespCont(resultCode, resultMsg, resultObject);
        respRoot.setSvcCont(svcRespCont);

        TcpRespCont tcpRespCont = new TcpRespCont();
        tcpRespCont.setRspTime(new Date());

        //sign根据省份密钥和返回报文做加密重新生成
        sign = generateSign(svcRespCont, transactionID , true);

        tcpRespCont.setSign(sign);
        tcpRespCont.setTransactionId(transactionID);
        respRoot.setTcpCont(tcpRespCont);

        cpcGroupResponse.setContractRoot(respRoot);
        log.info("the response is " + JSONObject.toJSONString(cpcGroupResponse, SerializerFeature.WriteMapNullValue));
        return cpcGroupResponse;
    }

    public static CpcGroupResponse buildSuccessResponse(String sign, String transactionID) {
        return buildResponse(ResponseCode.SUCCESS, "处理成功", null, sign, transactionID);
    }


    /**
     *
     * 生成签名
     *
     * @param svcCont 主体数据
     * @param transactionID 流水号
     * @return 签名
     */
    public static String generateSign(Object svcCont, String transactionID, boolean flagNullKey) {

        String json = null;
        if(flagNullKey){
            json = JSON.toJSONString(svcCont, SerializerFeature.WriteMapNullValue);
        }else {
            json = JSON.toJSONString(svcCont);
        }
//        String randomNum = RandomUtil.generateOfTiming();
//        String secretKey = CipherFactory.getProvider().encrypt(randomNum);

        log.info("transactionId ->" + transactionID + ",svcCont ->" + "\"svcCont\":" +  json);
        //包含svcCont
        String source = transactionID + "\"svcCont\":" + json + SECRETKEY;
        log.info("before md5 source-->" + source);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] data = md5To32size(md.digest(source.getBytes("UTF-8")));
            String sign =  new String(data, "UTF-8");
            log.info("after md5 transactionId ->" + transactionID + ",sign ->" + sign);
            return sign;
        }
        catch (Exception e) {
            return null;
        }
    }

    private static byte[] md5To32size(byte[] data) {
        StringBuffer buf = new StringBuffer("");

        int i;
        for (int offset = 0; offset < data.length; offset++ ) {
            i = data[offset];
            if (i < 0) {
                i += 256;
            }
            if (i < 16) {
                buf.append("0");
            }
            buf.append(Integer.toHexString(i));
        }

        return buf.toString().getBytes();
    }

    public static CpcGroupResponse buildErrorResponse(CpcGroupRequest cpcGroupRequest) {

        ContractReqRoot contractRoot = cpcGroupRequest.getContractRoot();
        String sign = contractRoot.getTcpCont().getSign();
        String transactionID = contractRoot.getTcpCont().getTransactionId();
        return buildResponse(ResponseCode.INTERNAL_ERROR, "处理失败，请联系接口人员！", null, sign,
            transactionID);
    }

    public static CpcGroupResponse buildErrorResponse(CpcGroupRequest cpcGroupRequest, Exception e) {

        ContractReqRoot contractRoot = cpcGroupRequest.getContractRoot();
        String sign = contractRoot.getTcpCont().getSign();
        String transactionID = contractRoot.getTcpCont().getTransactionId();
        return buildErrorResponse(e, sign, transactionID);
    }

    public static CpcGroupResponse buildErrorResponse(Exception exception, String sign,
                                                      String transactionID) {
        if (exception instanceof SQLException) {
            return buildResponse(ResponseCode.DATABASE_ERROR, ResponseCode.DATABASE_ERROR_MSG,
                    null, sign, transactionID);
        }
        String msg = null;
        if (exception instanceof RuntimeException) {

            if (StringUtils.isEmpty(exception.getMessage())) {
                msg = ResponseCode.VALIDATE_ERROR_MSG;
            }
            return buildResponse(ResponseCode.VALIDATE_ERROR, msg, null, sign, transactionID);
        }
        return buildResponse(ResponseCode.INTERNAL_ERROR, "处理失败，请联系接口人员！", null, sign,
                transactionID);
    }

    public static CpcGroupResponse buildSuccessResponse(CpcGroupRequest cpcGroupRequest) {
        ContractReqRoot contractRoot = cpcGroupRequest.getContractRoot();
        String sign = contractRoot.getTcpCont().getSign();
        String transactionID = contractRoot.getTcpCont().getTransactionId();
        return buildResponse(ResponseCode.SUCCESS, "处理成功", null, sign, transactionID);
    }
    
    public static void main(String[] args) {
//    	String source = "1000000038201711090001484143\"svcCont\":{\"resultCode\":\"0\",\"resultMsg\":\"处理成功\",\"resultObject\":null}6001040005ZJTEST";
//    	String source = "6001040005201711107037520107\"svcCont\":{\"resultCode\":\"600104000001\",\"resultMsg\":\"处理失败，请联系接口人员！\",\"resultObject\":null}6001040005ZJTEST";
    	String source =  "6001040005201711103725208682\"svcCont\":{\"authenticationInfo\":null,\"requestObject\":{\"actType\":null,\"beginTime\":null,\"createDate\":null,\"createStaff\":null,\"endTime\":null,\"eventScenes\":null,\"execInvl\":null,\"execNum\":null,\"execType\":null,\"lanId\":null,\"mktActivityNbr\":null,\"mktActivityTarget\":null,\"mktAlgorithms\":[{\"algoCode\":\"A000012\",\"algoDesc\":\"单C家庭客户挖掘模型/9800目标客户/推荐销售品：E169、E199/建议渠道：直销渠道。\",\"algoId\":null,\"algoName\":\"单C转融合模型\",\"createDate\":null,\"createStaff\":null,\"handleClass\":null,\"remark\":null,\"statusCd\":null,\"statusDate\":null,\"updateDate\":null,\"updateStaff\":null}],\"mktCamChlConfDetails\":null,\"mktCamGrpRuls\":null,\"mktCamItems\":null,\"mktCampaignDesc\":\"单C转融合营销活动\",\"mktCampaignEvts\":null,\"mktCampaignId\":1001,\"mktCampaignName\":\"单C转融合营销活动\",\"mktCampaignRels\":null,\"mktCampaignStrategyDetails\":null,\"mktCampaignType\":\"1000\",\"mktCpcAlgorithmsRulDetails\":null,\"planBeginTime\":1483200000000,\"planEndTime\":1514649600000,\"remark\":null,\"statusCd\":\"1200\",\"statusDate\":null,\"strategyId\":null,\"tiggerType\":null,\"updateDate\":null,\"updateStaff\":null}}6001040005ZJTEST";
    	MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        byte[] data = md5To32size(md.digest(source.getBytes()));
        String sign =  new String(data);
        System.out.println(sign);
	}

}
