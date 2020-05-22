package com.zjtelcom.cpct.service.impl.campaign;

import com.ctzj.smt.bss.cooperate.service.dubbo.IReportService;
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpMapper;
import com.zjtelcom.cpct.dao.strategy.*;
import com.zjtelcom.cpct.dao.system.SysAreaMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.campaign.OpenCampaignScheEntity;
import com.zjtelcom.cpct.service.campaign.MktDttsLogService;
import com.zjtelcom.cpct.service.campaign.OpenCampaignScheService;
import com.zjtelcom.cpct.service.dubbo.UCCPService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import com.zjtelcom.cpct.util.RedisUtils_prd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OpenCampaignScheServiceImpl  implements OpenCampaignScheService {
    @Autowired
    private MktCampaignMapper mktCampaignMapper;
    @Autowired
    private MktCamItemMapper mktCamItemMapper;
    @Autowired
    private MktCamStrategyConfRelMapper mktCamStrategyConfRelMapper;
    @Autowired
    private MktStrategyConfRuleRelMapper mktStrategyConfRuleRelMapper;
    @Autowired
    private MktStrategyConfRuleMapper mktStrategyConfRuleMapper;
    @Autowired
    private MktCamStrategyRelMapper mktCamStrategyRelMapper;
    @Autowired
    private MktStrategyMapper mktStrategyMapper;
    @Autowired
    private MktStrategyConfMapper mktStrategyConfMapper;
    @Autowired
    private MktCamScriptMapper mktCamScriptMapper;
    @Autowired
    private MktCamChlConfMapper mktCamChlConfMapper;
    @Autowired
    private MktCamChlConfAttrMapper mktCamChlConfAttrMapper;
    @Autowired
    private MktCamGrpRulMapper mktCamGrpRulMapper;
    @Autowired
    private TarGrpMapper tarGrpMapper;
    @Autowired
    private TarGrpConditionMapper tarGrpConditionMapper;
    @Autowired
    private MktCamEvtRelMapper mktCamEvtRelMapper;
    @Autowired
    private MktAlgorithmsMapper mktAlgorithmsMapper;
    @Autowired
    private MktCpcAlgorithmsRulMapper mktCpcAlgorithmsRulMapper;
    @Autowired
    private MktCamRecomCalcRelMapper mktCamRecomCalcRelMapper;
    @Autowired
    private MktCamCityRelMapper mktCamCityRelMapper;
    @Autowired
    private InjectionLabelMapper injectionLabelMapper;
    @Autowired
    private FilterRuleMapper filterRuleMapper;
    @Autowired
    private ContactChannelMapper contactChannelMapper;
    @Autowired
    private MktStrategyFilterRuleRelMapper mktStrategyFilterRuleRelMapper;
    @Autowired
    private OfferMapper offerMapper;
    @Autowired
    private MktResourceMapper mktResourceMapper;
    @Autowired
    private SysAreaMapper sysAreaMapper;
    @Autowired
    private ContactEvtMapper contactEvtMapper;
    @Autowired
    private SysParamsMapper sysParamsMapper;
    @Autowired
    private MktCampaignCompleteMapper mktCampaignCompleteMapper;
    @Autowired(required = false)
    private IReportService iReportService;
    @Autowired
    private MktDttsLogService mktDttsLogService;
    @Autowired
    private UCCPService uccpService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private RedisUtils_prd redisUtils_prd;


    /**
     * 集团活动定时任务
     * 每日凌晨1点查询前一日所有新建活动生成文件放到对应ftp文件服务器
     * @return
     */
    @Override
    public Map<String, Object> openCampaignScheForDay(Long mktCampaignId) {
        Map<String,Object> result = new HashMap<>();
        MktCampaignDO campaignDO = mktCampaignMapper.selectByPrimaryKey(mktCampaignId);
        if (campaignDO==null){
            result.put("code","500");
            result.put("data","不存在");
            return result;
        }
        OpenCampaignScheEntity campaignScheEntity = BeanUtil.create(campaignDO, new OpenCampaignScheEntity());





        result.put("code","200");
        result.put("data",campaignScheEntity);
        return result;
    }

    /**
     * 集团活动定时任务
     * 每月一号凌晨1点查询当月所有新建活动生成文件放到对应ftp文件服务器
     * @return
     */
    @Override
    public Map<String, Object> openCampaignScheForMonth() {
        Map<String,Object> result = new HashMap<>();





        return result;
    }
}
