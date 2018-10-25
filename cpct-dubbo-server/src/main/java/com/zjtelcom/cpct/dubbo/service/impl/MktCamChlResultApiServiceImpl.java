/**
 * @(#)MktCamChlResultApiServiceImpl.java, 2018/9/21.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.dubbo.service.impl;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.channel.ContactChannelMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamChlConfAttrDO;
import com.zjtelcom.cpct.domain.campaign.MktCamChlConfDO;
import com.zjtelcom.cpct.domain.campaign.MktCamChlResultConfRelDO;
import com.zjtelcom.cpct.domain.campaign.MktCamChlResultDO;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfAttr;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail;
import com.zjtelcom.cpct.dto.campaign.MktCamChlResult;
import com.zjtelcom.cpct.dto.campaign.MktCamResultRelDeatil;
import com.zjtelcom.cpct.dubbo.model.Ret;
import com.zjtelcom.cpct.dubbo.model.RetCamResult;
import com.zjtelcom.cpct.dubbo.model.RetChannel;
import com.zjtelcom.cpct.dubbo.service.MktCamChlResultApiService;
import com.zjtelcom.cpct.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

/**
 * @Description:
 * @author: linchao
 * @date: 2018/09/21 11:28
 * @version: V1.0
 */

@Service
@Transactional
public class MktCamChlResultApiServiceImpl implements MktCamChlResultApiService {

    @Autowired
    private MktCamChlResultMapper mktCamChlResultMapper;

    @Autowired
    private MktCamChlResultConfRelMapper mktCamChlResultConfRelMapper;

    @Autowired
    private MktCamChlConfMapper mktCamChlConfMapper;

    @Autowired
    MktCamResultRelMapper mktCamResultRelMapper;

    @Autowired
    ContactChannelMapper contactChannelMapper;

    @Autowired
    private MktCamChlConfAttrMapper mktCamChlConfAttrMapper;

    /**
     * 查询所有 有二次协同 且二次协同为工单，且有效的
     *
     * @return
     */
    @Override
    public RetCamResult selectResultList() {
        RetCamResult ret = new RetCamResult();
        List<Long> mktCampaignIdList = mktCamResultRelMapper.selectAllGroupByMktCampaignId();
        List<MktCamResultRelDeatil> mktCamResultRelDeatilList = new ArrayList<>();
        for (Long mktCampaignId : mktCampaignIdList) {
            MktCamResultRelDeatil mktCamResultRelDeatil = new MktCamResultRelDeatil();
            List<MktCamChlResultDO> mktCamChlResultDOList = mktCamChlResultMapper.selectResultByMktCampaignId(mktCampaignId);
            List<MktCamChlResult> mktCamChlResultList = new ArrayList<>();
            for (MktCamChlResultDO mktCamChlResultDO : mktCamChlResultDOList) {
                MktCamChlResult mktCamChlResult = BeanUtil.create(mktCamChlResultDO, new MktCamChlResult());
                List<MktCamChlResultConfRelDO> mktCamChlResultConfRelDOList = mktCamChlResultConfRelMapper.selectByMktCamChlResultId(mktCamChlResultDO.getMktCamChlResultId());
                List<MktCamChlConfDetail> mktCamChlConfDetailList = new ArrayList<>();
                for (MktCamChlResultConfRelDO mktCamChlResultConfRelDO : mktCamChlResultConfRelDOList) {
                    MktCamChlConfDO mktCamChlConfDO = mktCamChlConfMapper.selectByPrimaryKey(mktCamChlResultConfRelDO.getEvtContactConfId());
                    MktCamChlConfDetail mktCamChlConfDetail = BeanUtil.create(mktCamChlConfDO, new MktCamChlConfDetail());
                    // 获取触点渠道编码
                    Channel channel = contactChannelMapper.selectByPrimaryKey(mktCamChlConfDetail.getContactChlId());
                    if (channel != null) {
                        mktCamChlConfDetail.setContactChlCode(channel.getContactChlCode());
                    }
                    mktCamChlConfDetailList.add(mktCamChlConfDetail);
                    // 获取属性
                    List<MktCamChlConfAttrDO> mktCamChlConfAttrDOList = mktCamChlConfAttrMapper.selectByEvtContactConfId(mktCamChlConfDetail.getEvtContactConfId());
                    List<MktCamChlConfAttr> mktCamChlConfAttrList = new ArrayList<>();
                    for (MktCamChlConfAttrDO mktCamChlConfAttrDO : mktCamChlConfAttrDOList) {
                        MktCamChlConfAttr mktCamChlConfAttr = BeanUtil.create(mktCamChlConfAttrDO, new MktCamChlConfAttr());
                        mktCamChlConfAttrList.add(mktCamChlConfAttr);
                    }
                    mktCamChlConfDetail.setMktCamChlConfAttrList(mktCamChlConfAttrList);
                }
                mktCamChlResult.setMktCamChlConfDetailList(mktCamChlConfDetailList);
                mktCamChlResultList.add(mktCamChlResult);
            }
            mktCamResultRelDeatil.setMktCampaignId(mktCampaignId);
            mktCamResultRelDeatil.setMktCamChlResultList(mktCamChlResultList);
            mktCamResultRelDeatilList.add(mktCamResultRelDeatil);
        }
        ret.setResultCode(CODE_SUCCESS);
        ret.setResultMsg("success");
        ret.setData(mktCamResultRelDeatilList);
        return ret;
    }
}