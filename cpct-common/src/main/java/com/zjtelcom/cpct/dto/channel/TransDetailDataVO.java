package com.zjtelcom.cpct.dto.channel;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransDetailDataVO implements Serializable {
    private String settleOrderNo;	//批次号
    private Map<String,Object> map;
    private int currentRowNumber;
    private int paymentCount = 0;
    private  int refundCount = 0;
    private int readRowTitleIndex = 1;
    private int readDetailRowIndex = 2;

    public  List<String> contentList = new ArrayList<>();

    public void processTransTotalData(String rowStrs, int currentRowNumber) {
        String[] cellStrs = rowStrs.split("\\|@\\|");
        // 读取第二行汇总行
        if (currentRowNumber == readRowTitleIndex) {
            for (String labelName : cellStrs){
                this.map.put(labelName,"info");
            }
        }
        if (currentRowNumber == readDetailRowIndex) {
            int i =0;
            for (Map.Entry<String,Object> entry : this.map.entrySet()){
                entry.setValue(cellStrs[i]);
                i++;
            }
        }
    }


    private static  BigDecimal stringToBigDecimal(String str) {
        if (StringUtils.isBlank(str)) {
            return BigDecimal.ZERO;
        }
        BigDecimal bd = new BigDecimal(str);

        return bd;
    }



    public String getSettleOrderNo() {
        return settleOrderNo;
    }

    public void setSettleOrderNo(String settleOrderNo) {
        this.settleOrderNo = settleOrderNo;
    }

    public int getCurrentRowNumber() {
        return currentRowNumber;
    }

    public void setCurrentRowNumber(int currentRowNumber) {
        this.currentRowNumber = currentRowNumber;
    }

    public int getPaymentCount() {
        return paymentCount;
    }

    public void setPaymentCount(int paymentCount) {
        this.paymentCount = paymentCount;
    }


    public int getRefundCount() {
        return refundCount;
    }

    public void setRefundCount(int refundCount) {
        this.refundCount = refundCount;
    }

    public List<String> getContentList() {
        return contentList;
    }

    public void setContentList(List<String> contentList) {
        this.contentList = contentList;
    }

}
