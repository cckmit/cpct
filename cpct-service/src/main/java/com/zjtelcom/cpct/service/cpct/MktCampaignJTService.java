/*
 * 文件名：MktCampaignService.java
 * 版权：Copyright by 南京星邺汇捷网络科技有限公司
 * 描述：
 * 修改人：taowenwu
 * 修改时间：2017年10月30日
 * 修改内容：
 */

package com.zjtelcom.cpct.service.cpct;


import com.zjtelcom.cpct.dto.pojo.Result;
import com.zjtelcom.cpct.pojo.MktCampaignDetailReq;

/**
 * 集团活动服务接口</br>
 * 主要用于集团活动的新增删除修改
 * @author linchao
 * @version 1.0
 * @see MktCampaignJTService
 * @since
 */

public interface MktCampaignJTService {

    /**
     * 保存批量活动
     * 
     * @param req 请求对象
     * @return 处理结果
     */
    Result saveBatch(MktCampaignDetailReq req);

    /**
     * 修改批量活动
     *
     * @param req 请求对象
     * @return 处理结果
     */
    Result updateBatch(MktCampaignDetailReq req);

}
