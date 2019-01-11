package com.zjtelcom.cpct.count.serviceImpl.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zjtelcom.cpct.count.base.enums.ResultEnum;
import com.zjtelcom.cpct.count.base.enums.StatusEnum;
import com.zjtelcom.cpct.count.controller.ActivityController;
import com.zjtelcom.cpct.count.service.api.ActivityService;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.campaign.MktOperatorLogMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.campaign.MktOperatorLogDO;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dto.campaign.MktCampaign;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.util.MD5Util;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2019/1/9
 * @Description:提供dubbo接口给cpc调用修改活动状态    只提供已发布的改为暂停
 */
@Service
@Transactional
public class ActivityServiceImpl implements ActivityService {


    private Logger log = LoggerFactory.getLogger(ActivityServiceImpl.class);

    @Autowired
    private MktCampaignMapper mktCampaignMapper;

    @Autowired
    private MktOperatorLogMapper mktOperatorLogMapper;

    @Autowired
    private SysParamsMapper sysParamsMapper;

    private static String DUBBO_CONFIG="DUBBO_CONFIG";    //系统参数表 权限相关key

    private static String PASSWORD_TIP="2019";            //加密字段


    /**
     * 提供： 审核    发布    暂停    下线    4种活动状态的修改
     * 草稿状态可以审核
     * 审核之后可以发布
     * 只有已发布的活动才能 暂停或下线
     * 下线以后的活动不可以删除和修改了
     * @param paramMap
     * @return
     */
    @Override
    public Map<String, Object> changeActivityStatus(Map<String, Object> paramMap) {
        log.info("changeActivityStatus请求入参："+paramMap);
        Map<String, Object> map=new HashMap<>();
        map.put("resultCode", ResultEnum.SUCCESS.getStatus());
        map.put("resultMsg", "操作成功");
        if(paramMap.isEmpty()){
            map.put("resultCode", ResultEnum.FAILED.getStatus());
            map.put("resultMsg", "传参不能为空");
            return map;
        }
        //调用方身份验证
        Map<String, Object> authority = authority(paramMap);
        if(!ResultEnum.SUCCESS.getStatus().equals(authority.get("resultCode"))){
            return authority;
        }
        String name= (String) paramMap.get("channel");



        //获取活动id和状态
        String mktCampaignId =String.valueOf(paramMap.get("mktCampaignId"));
        String statusCd = String.valueOf(paramMap.get("type"));    //需要修改活动的状态 详细见StatusEnum
        if(StringUtils.isBlank(mktCampaignId)||StringUtils.isBlank(statusCd)){
            map.put("resultCode", ResultEnum.FAILED.getStatus());
            map.put("resultMsg", "活动id或者活动状态不能为空");
            return map;
        }
        MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(Long.valueOf(mktCampaignId));
        if(null==mktCampaignDO){
            map.put("resultCode", ResultEnum.FAILED.getStatus());
            map.put("resultMsg", "活动id："+mktCampaignId+"对应的活动信息不存在");
            return map;


        }else if(StatusCode.STATUS_CODE_ROLL.getStatusCode().equals(mktCampaignDO.getStatusCd())){
            //已下线的活动不能进行任何操作了
            map.put("resultCode", ResultEnum.FAILED.getStatus());
            map.put("resultMsg", "该活动已下线,活动状态不能编辑");
            return map;


        }else if(StatusEnum.CHECK.getStatusCode().equals(statusCd)){
            //1.1 审核  ------草稿和不通过状态可审核   该操作后活动状态变为审核中   是否通过  需要去策略中心门户手动点击
            if(StatusCode.STATUS_CODE_DRAFT.getStatusCode().equals(mktCampaignDO.getStatusCd())||
                    StatusCode.STATUS_CODE_UNPASS.getStatusCode().equals(mktCampaignDO.getStatusCd())){
                mktCampaignMapper.changeMktCampaignStatus(Long.valueOf(mktCampaignId), StatusCode.STATUS_CODE_CHECKING.getStatusCode(),new Date(),2L);
                //保存活动修改记录
                addOperatorLog(mktCampaignDO,StatusEnum.CHECK.getStatusCode(),name,StatusCode.STATUS_CODE_CHECKING.getStatusCode());
            }else{
                map.put("resultCode", ResultEnum.FAILED.getStatus());
                map.put("resultMsg", "该活动目前不满足审核条件");
                return map;
            }


        }else if(StatusEnum.PUBLISH.getStatusCode().equals(statusCd)){
            //1.2发布   ------状态变为已发布
            if(StatusCode.STATUS_CODE_PASS.getStatusCode().equals(mktCampaignDO.getStatusCd())){
                mktCampaignMapper.changeMktCampaignStatus(Long.valueOf(mktCampaignId), StatusCode.STATUS_CODE_PUBLISHED.getStatusCode(),new Date(),2L);
                addOperatorLog(mktCampaignDO,StatusEnum.PUBLISH.getStatusCode(),name,StatusCode.STATUS_CODE_PUBLISHED.getStatusCode());
            }else{
                map.put("resultCode", ResultEnum.FAILED.getStatus());
                map.put("resultMsg", "该活动目前不满足发布条件");
                return map;
            }



        }else if(StatusEnum.PAUSE.getStatusCode().equals(statusCd)){
            //1.3暂停  -------状态变为已暂停
            if(StatusCode.STATUS_CODE_PUBLISHED.getStatusCode().equals(mktCampaignDO.getStatusCd())){
                mktCampaignMapper.changeMktCampaignStatus(Long.valueOf(mktCampaignId), StatusCode.STATUS_CODE_STOP.getStatusCode(),new Date(),2L);
                addOperatorLog(mktCampaignDO,StatusEnum.PAUSE.getStatusCode(),name,StatusCode.STATUS_CODE_STOP.getStatusCode());
            }else{
                map.put("resultCode", ResultEnum.FAILED.getStatus());
                map.put("resultMsg", "该活动目前不满足暂停条件");
                return map;
            }



        }else if(StatusEnum.CANCEL_PAUSE.getStatusCode().equals(statusCd)){
            //1.4取消暂停  ----状态变为已发布
            if(StatusCode.STATUS_CODE_STOP.getStatusCode().equals(mktCampaignDO.getStatusCd())){
                mktCampaignMapper.changeMktCampaignStatus(Long.valueOf(mktCampaignId), StatusCode.STATUS_CODE_PUBLISHED.getStatusCode(),new Date(),2L);
                addOperatorLog(mktCampaignDO,StatusEnum.CANCEL_PAUSE.getStatusCode(),name,StatusCode.STATUS_CODE_PUBLISHED.getStatusCode());
            }else{
                map.put("resultCode", ResultEnum.FAILED.getStatus());
                map.put("resultMsg", "该活动目前不满足取消暂停条件");
                return map;
            }


        }else if(StatusEnum.TAPE_OUT.getStatusCode().equals(statusCd)){
            //1.5下线     -----状态变为已下线
            if(StatusCode.STATUS_CODE_PUBLISHED.getStatusCode().equals(mktCampaignDO.getStatusCd())){
                mktCampaignMapper.changeMktCampaignStatus(Long.valueOf(mktCampaignId), StatusCode.STATUS_CODE_ROLL.getStatusCode(),new Date(),2L);
                addOperatorLog(mktCampaignDO,StatusEnum.TAPE_OUT.getStatusCode(),name,StatusCode.STATUS_CODE_ROLL.getStatusCode());
            }else{
                map.put("resultCode", ResultEnum.FAILED.getStatus());
                map.put("resultMsg", "该活动目前不满足下线条件");
                return map;
            }


        }else{
            map.put("resultCode", ResultEnum.FAILED.getStatus());
            map.put("resultMsg", "请输入正确的活动操作类型");
            return map;
        }

        return map;
    }






    /**
     * 权限判断
     * @param map
     * @return
     */
    public Map<String, Object> authority(Map<String, Object> map){
        boolean tip=false;
        Map<String, Object> result=new HashMap<>();
        result.put("resultCode",ResultEnum.SUCCESS.getStatus());
        //渠道编码和密钥
        String channel= (String) map.get("channel");
        String token= (String) map.get("channelToken");
        if(StringUtils.isBlank(channel)||StringUtils.isBlank(token)){
            result.put("resultCode",ResultEnum.FAILED.getStatus());
            result.put("resultMsg","身份验证错误");
            return result;
        }
        List<SysParams> sysParams = sysParamsMapper.listParamsByKeyForCampaign(DUBBO_CONFIG);
        if(!sysParams.isEmpty()){
            SysParams sysParams1 = sysParams.get(0);
            String paramValue = sysParams1.getParamValue();
            try {
                JSONArray jsonArray = JSONArray.parseArray(paramValue);
                for (int i = 0; i <jsonArray.size() ; i++) {
                    JSONObject o = JSONObject.parseObject(JSON.toJSONString(jsonArray.get(i)));
                    if(channel.equals(o.getString("channel"))){
                        //判断是否开启了权限
                        if(!ResultEnum.SUCCESS.getStatus().equals(o.getString("isOpen"))){
                            result.put("resultCode",ResultEnum.FAILED.getStatus());
                            result.put("resultMsg","该渠道的调用权限已关闭,可联系策略中心人员重新开通");
                            return  result;
                        }
                        String saveToken=o.getString("channelToken")+o.getString("channel")+PASSWORD_TIP;
                        String md5Token=MD5Util.encodePassword(saveToken).toUpperCase();
                        log.info("加密后密钥："+md5Token);
                        if (!token.equals(md5Token)){
                            result.put("resultCode",ResultEnum.FAILED.getStatus());
                            result.put("resultMsg","身份配置信息验证失败");
                            return  result;
                        }
                        tip=true;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                result.put("resultCode",ResultEnum.FAILED.getStatus());
                result.put("resultMsg","配置信息解析错误");
                return result;
            }
        }
        if (!tip){
            result.put("resultCode",ResultEnum.FAILED.getStatus());
            result.put("resultMsg","身份配置信息验证失败");
        }
        return result;
    }


    /**
     * 增加活动操作记录
     * @param mktCampaign   操作的活动
     * @param type          操作类型
     * @param name          操作人
     */
    public void addOperatorLog(MktCampaignDO mktCampaign,String type,String name,String after){
        MktOperatorLogDO mktOperatorLogDO = new MktOperatorLogDO();
        mktOperatorLogDO.setMktCampaignName(mktCampaign.getMktCampaignName());
        mktOperatorLogDO.setMktCampaignId(mktCampaign.getMktCampaignId());
        mktOperatorLogDO.setMktActivityNbr(mktCampaign.getMktActivityNbr());
        mktOperatorLogDO.setOperatorType("调用00.1083.changeActivityStatus服务 操作类型："+StatusEnum.getNameByCode(type));
        if(StringUtils.isNotBlank(mktCampaign.getStatusCd())){
            //修改前
            mktOperatorLogDO.setMktCampaignStateBefore(mktCampaign.getStatusCd());
        }
        if(StringUtils.isNotBlank(mktCampaign.getStatusCd())){
            //修改后
            mktOperatorLogDO.setMktCampaignStateAfter(after);
        }

        mktOperatorLogDO.setOperatorAccount(name);   //操作人工号
        mktOperatorLogDO.setOperatorDate(new Date());
        mktOperatorLogMapper.insertOperation(mktOperatorLogDO);

    }


    public static void main(String[] args) {
        String name="cpc2019";
        String password="cpc-password-2019";
        String tip="2019";


        String code=password+name+tip;
        String s = MD5Util.encodePassword(code).toUpperCase();
        System.out.println(s.length());
        System.out.println(s);
    }


}
