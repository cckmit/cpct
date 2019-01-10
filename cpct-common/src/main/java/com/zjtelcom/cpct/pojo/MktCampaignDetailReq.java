/*
 * 文件名：MktCampaignDetailReq.java
 * 版权：Copyright by 南京星邺汇捷网络科技有限公司
 * 描述：
 * 修改人：taowenwu
 * 修改时间：2017年11月10日
 * 修改内容：
 */

package com.zjtelcom.cpct.pojo;


import com.zjtelcom.cpct.dto.pojo.MktCampaignPO;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * 营销活动请求体
 * @author taowenwu
 * @version 1.0
 * @see MktCampaignDetailReq
 * @since JDK1.7
 */
public class MktCampaignDetailReq implements Serializable {
    private List<MktCampaignPO> mktCampaignDetails;

    private String transactionId;

    public List<MktCampaignPO> getMktCampaignDetails() {
        return mktCampaignDetails;
    }

    public void setMktCampaignDetails(List<MktCampaignPO> mktCampaignDetails) {
        this.mktCampaignDetails = mktCampaignDetails;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
