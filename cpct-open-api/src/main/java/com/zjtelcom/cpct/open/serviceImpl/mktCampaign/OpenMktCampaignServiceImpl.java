package com.zjtelcom.cpct.open.serviceImpl.mktCampaign;

import com.zjtelcom.cpct.dao.campaign.MktCamItemMapper;
import com.zjtelcom.cpct.dao.campaign.MktCamStrategyConfRelMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.channel.MktCamScriptMapper;
import com.zjtelcom.cpct.dao.strategy.MktCamStrategyRelMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleRelMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamItem;
import com.zjtelcom.cpct.domain.campaign.MktCamStrategyConfRelDO;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.channel.CamScript;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;
import com.zjtelcom.cpct.dto.channel.MktScript;
import com.zjtelcom.cpct.dto.strategy.MktStrategy;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.common.CommonUtil;
import com.zjtelcom.cpct.open.base.service.BaseService;
import com.zjtelcom.cpct.open.entity.mktCamItem.OpenMktCamItem;
import com.zjtelcom.cpct.open.entity.mktCampaign.OpenMktCampaign;
import com.zjtelcom.cpct.open.entity.mktStrategy.OpenMktStrategy;
import com.zjtelcom.cpct.open.entity.script.OpenScript;
import com.zjtelcom.cpct.open.service.mktCampaign.OpenMktCampaignService;
import com.zjtelcom.cpct.pojo.MktCamStrategyRel;
import com.zjtelcom.cpct_prd.dao.campaign.MktCamStrategyConfRelPrdMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: anson
 * @CreateDate: 2018-11-05 17:32:51
 * @version: V 1.0
 * 营销活动openapi相关服务
 */
@Service
@Transactional
public class OpenMktCampaignServiceImpl extends BaseService implements OpenMktCampaignService {

    @Autowired
    private MktCampaignMapper mktCampaignMapper;
    @Autowired
    private MktCamItemMapper mktCamItemMapper;
    @Autowired
    private MktCamStrategyConfRelMapper mktCamStrategyConfRelMapper;
    @Autowired
    private MktStrategyConfRuleRelMapper mktStrategyConfRuleRelMapper;
    @Autowired
    private MktStrategyConfRuleMapper  mktStrategyConfRuleMapper;
    @Autowired
    private MktCamStrategyRelMapper mktCamStrategyRelMapper;
    @Autowired
    private MktStrategyMapper mktStrategyMapper;
    @Autowired
    private MktCamScriptMapper mktCamScriptMapper;




    /**
     * 查询营销活动信息
     *
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> queryById(String id) {
        Map<String, Object> resultMap = new HashMap<>();
        long queryId = CommonUtil.stringToLong(id);
        MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(queryId);
        if (null == mktCampaignDO) {
            resultMap.put("params", "对应营销活动信息不存在!");
            return resultMap;
        }
        //将活动信息转换为openapi返回格式
        OpenMktCampaign campaign=getOpenCampaign(mktCampaignDO);
        //活动推荐条目列表  活动脚本列表  维挽策略列表
        List<OpenMktCamItem> mktCamItemList=new ArrayList<>();
        List<OpenScript> mktScriptList=new ArrayList<>();
        List<OpenMktStrategy> mktStrategyList=new ArrayList<>();

        //1.查询营销推荐维挽策略
        List<MktCamStrategyRel> mktCamStrategyRels = mktCamStrategyRelMapper.selectByMktCampaignId(queryId);
        if (!mktCamStrategyRels.isEmpty()){
            for (MktCamStrategyRel rel:mktCamStrategyRels){
                MktStrategy mktStrategy = mktStrategyMapper.selectByPrimaryKey(rel.getStrategyId());
                if(null!=mktStrategy){
                    OpenMktStrategy openMktStrategy = BeanUtil.create(mktStrategy, new OpenMktStrategy());
                    if(null!=mktStrategy.getStatusDate()){
                        openMktStrategy.setStatusDate(DateUtil.getDatetime(mktStrategy.getStatusDate()));
                    }
                    mktStrategyList.add(openMktStrategy);
                }
            }
            campaign.setMktStrategy(mktStrategyList);
        }

        //2. 查询营销活动推荐脚本
        List<CamScript> camScripts = mktCamScriptMapper.selectByCampaignId(queryId);
        if(!camScripts.isEmpty()){
            for (CamScript script:camScripts){
                if(null!=script){
                    OpenScript openScript = BeanUtil.create(script, new OpenScript());
                    openScript.setId(script.getMktCampaignScptId());
                    openScript.setHref("/mktScript/"+script.getMktCampaignScptId().toString());
                    openScript.setMktActivityNbr(mktCampaignDO.getMktActivityNbr());
                    mktScriptList.add(openScript);
                }
            }
            campaign.setMktCamScript(mktScriptList);
        }


        //查询相关营销活动推荐条目列表(属于规则) 活动id  471比较多   468比较少
        //2.查出活动下的策略   500
        List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOS = mktCamStrategyConfRelMapper.selectByMktCampaignId(mktCampaignDO.getMktCampaignId());
        if (mktCamStrategyConfRelDOS.isEmpty()){
            resultMap.put("params", campaign);
            return resultMap;
        }

        //3.查出策略下的规则
        for (MktCamStrategyConfRelDO m:mktCamStrategyConfRelDOS){
            List<MktStrategyConfRuleRelDO> mktStrategyConfRuleRelDOS = mktStrategyConfRuleRelMapper.selectByMktStrategyConfId(m.getStrategyConfId());

            if(mktStrategyConfRuleRelDOS.isEmpty()){
                resultMap.put("params", campaign);
                return resultMap;
            }
            //3.1 通过规则id查出规则
            for (MktStrategyConfRuleRelDO ruleRelDo:mktStrategyConfRuleRelDOS){
                MktStrategyConfRuleDO rule = mktStrategyConfRuleMapper.selectByPrimaryKey(ruleRelDo.getMktStrategyConfRuleId());
                // 规则信息中可以得到 1分群id  2推送条目id集合  3协同渠道配置id集合  4二次协同渠道配置结果id集合
                if(rule!=null){
                      //3.2 获取推荐条目信息   997/998/999/1000格式
                    if(StringUtils.isNotBlank(rule.getProductId())){
                        String[] split = rule.getProductId().split("/");
                        for (int i = 0; i <split.length ; i++) {
                            //得到活动推荐条目
                            MktCamItem mktCamItem = mktCamItemMapper.selectByPrimaryKey(Long.valueOf(split[i]));
                            if(null!=mktCamItem){
                                OpenMktCamItem openMktCamItem = BeanUtil.create(mktCamItem, new OpenMktCamItem());
                                openMktCamItem.setId(mktCamItem.getItemId());
                                openMktCamItem.setHref("/mktCamItem/"+mktCamItem.getItemId().toString());
                                openMktCamItem.setMktActivityNbr(mktCampaignDO.getMktActivityNbr());
                                mktCamItemList.add(openMktCamItem);
                            }
                        }

                    }
                }

            }
            //返回活动推荐条目列表
            campaign.setMktCamItem(mktCamItemList);
        }

        //设置id  和href  转换时间为对应格式
        campaign.setId(Long.valueOf(id));
        campaign.setHref("/mktCampaign/" + id);

        resultMap.put("params", campaign);
        return resultMap;
    }


    /**
     * 通过活动id获取策略列表
     * @param campaignId
     */
    public static List<MktCamStrategyConfRelDO> getStrategyList(Long campaignId){
        List<MktCamStrategyConfRelDO> list=new ArrayList<>();


        return list;
    }


    /**
     * 通过策略id获取规则列表
     * @param strategyId
     * @return
     */
    public static List<MktStrategyConfRuleDO> getRuleList(Long strategyId){
        List<MktStrategyConfRuleDO> list=new ArrayList<>();


        return list;
    }


    /**
     * 通过规则获取活动推荐条目列表
     * @param ruleId
     * @return
     */
    public static List<MktCamItem> getMktCamItemList(Long ruleId){
        List<MktCamItem> list=new ArrayList<>();

        return list;
    }











    @Override
    public Map<String, Object> addByObject(Object object) {
        return null;
    }

    @Override
    public Map<String, Object> updateByParams(String id, Object object) {
        return null;
    }

    @Override
    public Map<String, Object> deleteById(String id) {
        return null;
    }

    @Override
    public Map<String, Object> queryListByMap(Map<String, Object> map) {
        return null;
    }

    /**
     * 转换为openapi格式
     * @param mktCampaignDO
     * @return
     */
    public OpenMktCampaign getOpenCampaign(MktCampaignDO mktCampaignDO){
        OpenMktCampaign campaign=BeanUtil.create(mktCampaignDO,new OpenMktCampaign());
        //转换时间格式  beginTime endTime  planBeginTime  planEndTime statusDate
        if(null!=mktCampaignDO.getBeginTime()){
            campaign.setBeginTime(DateUtil.getDatetime(mktCampaignDO.getBeginTime()));
        }
        if(null!=mktCampaignDO.getEndTime()){
            campaign.setEndTime(DateUtil.getDatetime(mktCampaignDO.getEndTime()));
        }
        if(null!=mktCampaignDO.getPlanBeginTime()){
            campaign.setPlanBeginTime(DateUtil.getDatetime(mktCampaignDO.getPlanBeginTime()));
        }
        if(null!=mktCampaignDO.getPlanEndTime()){
            campaign.setPlanEndTime(DateUtil.getDatetime(mktCampaignDO.getPlanEndTime()));
        }

        return campaign;
    }


    public static void main(String[] args) {
        String s="997/998/999/1000";
        String[] split = s.split("/");
        for (int i = 0; i <split.length ; i++) {
            System.out.println(split[i]);
        }
        
    }
}
