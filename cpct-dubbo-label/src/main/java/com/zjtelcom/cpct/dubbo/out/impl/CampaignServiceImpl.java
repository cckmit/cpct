package com.zjtelcom.cpct.dubbo.out.impl;

import com.ctzj.smt.bss.sysmgr.model.common.SysmgrResultObject;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemUserDto;
import com.ctzj.smt.bss.sysmgr.privilege.service.dubbo.api.ISystemUserDtoDubboService;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.dubbo.out.CampaignService;
import com.zjtelcom.cpct.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CampaignServiceImpl implements CampaignService {


    @Autowired
    private MktCampaignMapper mktCampaignMapper;
    @Autowired(required = false)
    private ISystemUserDtoDubboService iSystemUserDtoDubboService;

    /**
     * 活动延期短信通知
     * @return
     */
    @Override
    public boolean CampaignDelayNotice() {
        ArrayList<String> list = new ArrayList<>();
        list.add("2002");
        List<MktCampaignDO> mktCampaignDOS = mktCampaignMapper.selectAllMktCampaignDetailsByStatus(list,null);
        for (MktCampaignDO mktCampaignDO : mktCampaignDOS) {
            int i = DateUtil.daysBetween(mktCampaignDO.getPlanEndTime(), new Date());
            if (i == 3) {
                Long staff = mktCampaignDO.getUpdateStaff();
                SysmgrResultObject<SystemUserDto> systemUserDtoSysmgrResultObject = iSystemUserDtoDubboService.qrySystemUserDto(staff, new ArrayList<Long>());
                if (systemUserDtoSysmgrResultObject != null && systemUserDtoSysmgrResultObject.getResultObject() != null) {
                    String sysUserCode = systemUserDtoSysmgrResultObject.getResultObject().getSysUserCode();
                    // TODO  调用发送短信接口

                }
            }
        }
        return false;
    }
}
