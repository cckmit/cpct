package com.zjtelcom.cpct.dubbo.out.impl;

import com.ctzj.smt.bss.sysmgr.model.common.SysmgrResultObject;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemUserDto;
import com.ctzj.smt.bss.sysmgr.privilege.service.dubbo.api.ISystemUserDtoDubboService;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.dubbo.out.CampaignOutService;
import com.zjtelcom.cpct.util.DateUtil;

import com.zjtelcom.cpct.util.UCCPUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.zjtelcom.cpct.enums.StatusCode.STATUS_CODE_PUBLISHED;

@Service
public class CampaignOutServiceImpl implements CampaignOutService {

    public static final org.slf4j.Logger logger = LoggerFactory.getLogger(CampaignOutServiceImpl.class);

    @Autowired
    private MktCampaignMapper mktCampaignMapper;
    @Autowired(required = false)
    private ISystemUserDtoDubboService iSystemUserDtoDubboService;

    /**
     * 活动延期短信通知
     * @return
     */
    @Override
    public void campaignDelayNotice() {
        ArrayList<String> list = new ArrayList<>();
        list.add(STATUS_CODE_PUBLISHED.getStatusCode());
        List<MktCampaignDO> mktCampaignDOS = mktCampaignMapper.selectAllMktCampaignDetailsByStatus(list,null);
        for (MktCampaignDO mktCampaignDO : mktCampaignDOS) {
            if (mktCampaignDO.getPlanEndTime().after(new Date()) && DateUtil.daysBetween(new Date(), mktCampaignDO.getPlanEndTime()) == 7) {
                Long staff = mktCampaignDO.getUpdateStaff();
                SysmgrResultObject<SystemUserDto> systemUserDtoSysmgrResultObject = iSystemUserDtoDubboService.qrySystemUserDto(staff, new ArrayList<Long>());
                if (systemUserDtoSysmgrResultObject != null && systemUserDtoSysmgrResultObject.getResultObject() != null) {
                    String sysUserCode = systemUserDtoSysmgrResultObject.getResultObject().getSysUserCode();
                    Long lanId = systemUserDtoSysmgrResultObject.getResultObject().getLanId();
                    // TODO  调用发送短信接口
                    String sendContent = "你好，你创建的活动（" + mktCampaignDO.getMktCampaignName() + "）马上将要到期，如要延期请登录延期页面进行延期。";
                    try {
                        UCCPUtil.sendShortMessage(sysUserCode, sendContent, lanId.toString());
                    } catch (Exception e) {
                        logger.error(sysUserCode + e.toString());
                    }
                }
            }
        }
    }
}
