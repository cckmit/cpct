/**
 * @(#)MktCampaignServiceImpl.java, 2018/7/17.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.dubbo.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.dto.campaign.MktCampaign;
import com.zjtelcom.cpct.dubbo.model.QryMktCampaignListReq;
import com.zjtelcom.cpct.dubbo.service.MktCampaignService;
import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/07/17 11:11
 * version: V1.0
 */
public class MktCampaignServiceImpl implements MktCampaignService {

    private static final Logger logger = LoggerFactory.getLogger(MktCampaignServiceImpl.class);

    @Autowired
    private MktCampaignMapper mktCampaignMapper;

    /**
     * 查询营销活动列表(分页)
     *
     * @param qryMktCampaignListReq
     * @return
     */
    @Override
    public Map<String, Object> qryMktCampaignList(QryMktCampaignListReq qryMktCampaignListReq) {
        Map<String, Object> qryMktCampaignListMap = new HashMap<>();
        // 获取分页信息，默认第1页，10条数据
        Integer page = 1;
        Integer pageSize = 10;
        if (qryMktCampaignListReq.getPageInfo() != null) {
            if (qryMktCampaignListReq.getPageInfo().getPage() != null && qryMktCampaignListReq.getPageInfo().getPage() != 0) {
                page = qryMktCampaignListReq.getPageInfo().getPage();
            }
            if (qryMktCampaignListReq.getPageInfo().getPageSize() != null && qryMktCampaignListReq.getPageInfo().getPageSize() != 0) {
                page = qryMktCampaignListReq.getPageInfo().getPageSize();
            }
        }

        MktCampaignDO mktCampaignDOReq = new MktCampaignDO();
        try {
            CopyPropertiesUtil.copyBean2Bean(mktCampaignDOReq, qryMktCampaignListReq);
        } catch (Exception e) {
            logger.error("[op:MktCampaignServiceImpl] falied to get mktCampaignDO = {}", JSON.toJSON(mktCampaignDOReq), e);
        }

        PageHelper.startPage(page, pageSize);
        List<MktCampaignDO> mktCampaignDOList = mktCampaignMapper.qryMktCampaignListByCondition(mktCampaignDOReq);
        Page pageInfo = new Page(new PageInfo(mktCampaignDOList));
        List<MktCampaign> mktCampaignList = new ArrayList<>();
        try {
            for (MktCampaignDO mktCampaignDOResp : mktCampaignDOList) {
                MktCampaign mktCampaign = new MktCampaign();
                CopyPropertiesUtil.copyBean2Bean(mktCampaign, mktCampaignDOResp);
                mktCampaignList.add(mktCampaign);
            }
        } catch (Exception e) {
            logger.error("[op:MktCampaignServiceImpl] falied to get mktCampaignList = {}", JSON.toJSON(mktCampaignList), e);
        }
        qryMktCampaignListMap.put("resultCode", CommonConstant.CODE_SUCCESS);
        qryMktCampaignListMap.put("resultMsg", "查询活动列表成功！");
        qryMktCampaignListMap.put("mktCampaigns", mktCampaignList);
        qryMktCampaignListMap.put("pageInfo", pageInfo);
        return qryMktCampaignListMap;
    }



}