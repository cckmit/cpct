package com.zjtelcom.cpct.service.impl.campaign;

import com.ctzj.smt.bss.sysmgr.model.common.SysmgrResultObject;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemUserDto;
import com.ctzj.smt.bss.sysmgr.privilege.service.dubbo.api.ISystemUserDtoDubboService;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.service.campaign.CampaignService;
import com.zjtelcom.cpct.service.dubbo.UCCPService;
import com.zjtelcom.cpct.util.DateUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.zjtelcom.cpct.enums.StatusCode.STATUS_CODE_PUBLISHED;

@Service
public class CampaignServiceImpl implements CampaignService {

    public static final org.slf4j.Logger logger = LoggerFactory.getLogger(CampaignServiceImpl.class);

    @Autowired
    private MktCampaignMapper mktCampaignMapper;
    @Autowired(required = false)
    private ISystemUserDtoDubboService iSystemUserDtoDubboService;
    @Autowired
    private UCCPService uccpService;

    /**
     * 活动延期短信通知
     * @return
     */
    @Override
    public void campaignDelayNotice() {
        ArrayList<String> list = new ArrayList<>();
        list.add(STATUS_CODE_PUBLISHED.getStatusCode());
        List<MktCampaignDO> mktCampaignDOS = mktCampaignMapper.selectAllMktCampaignDetailsByStatus(list,null);
        int i = 0;
        for (MktCampaignDO mktCampaignDO : mktCampaignDOS) {
            if (mktCampaignDO.getPlanEndTime().after(new Date()) && DateUtil.daysBetween(new Date(), mktCampaignDO.getPlanEndTime()) == 7) {
                System.out.println("campaignDelayNotice=>" + mktCampaignDO.getMktCampaignId() + "1111111111111111111");
                Long staff = mktCampaignDO.getCreateStaff();
                SysmgrResultObject<SystemUserDto> systemUserDtoSysmgrResultObject = iSystemUserDtoDubboService.qrySystemUserDto(staff, new ArrayList<Long>());
                System.out.println("campaignDelayNotice=>" + mktCampaignDO.getMktCampaignId() + "222222222222222222");
                if (systemUserDtoSysmgrResultObject != null && systemUserDtoSysmgrResultObject.getResultObject() != null) {
                    System.out.println("campaignDelayNotice=>" + mktCampaignDO.getMktCampaignId() + "3333333333333333333");
                    String sysUserCode = systemUserDtoSysmgrResultObject.getResultObject().getSysUserCode();
                    Long lanId = systemUserDtoSysmgrResultObject.getResultObject().getLanId();
                    // TODO  调用发送短信接口
                    String sendContent = "您好，您创建的活动（" + mktCampaignDO.getMktCampaignName() + "）马上将要到期，如要延期请登录延期页面进行延期。";
                    System.out.println(sendContent);
                    try {
                        System.out.println("campaignDelayNotice=>" + mktCampaignDO.getMktCampaignId() + "44444444444444444");
                        uccpService.sendShortMessage(sysUserCode, sendContent, lanId.toString());
                        i++;
                        System.out.println("campaignDelayNotice=>" + mktCampaignDO.getMktCampaignId() + "55555555555555555");
                    } catch (Exception e) {
                        logger.error(sysUserCode);
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println("共发送数量=>" + i);
    }

}
