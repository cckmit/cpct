package com.zjtelcom.cpct.service.impl.channel;

import com.zjtelcom.cpct.dao.channel.MktCamScriptMapper;
import com.zjtelcom.cpct.domain.channel.CamScript;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail;
import com.zjtelcom.cpct.dto.channel.CamScriptAddVO;
import com.zjtelcom.cpct.dto.channel.CamScriptEditVO;
import com.zjtelcom.cpct.dto.channel.CamScriptVO;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.CamScriptService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class CamScriptServiceImpl extends BaseService implements CamScriptService {

    @Autowired
    private MktCamScriptMapper camScriptMapper;
    @Autowired
    private RedisUtils redisUtils;


    /**
     * 复制活动脚本
     * @param contactConfId
     * @param newConfId
     * @return
     */
    @Override
    public Map<String, Object> copyCamScript(Long contactConfId, String scriptDesc, Long newConfId) {
        Map<String,Object> result = new HashMap<>();
/*
        CamScript script = camScriptMapper.selectByConfId(contactConfId);
        if (script==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","活动脚本不存在");
            return result;
        }*/
        CamScript newScript = new CamScript();
        CamScript camScript = new CamScript();
        MktCamChlConfDetail detail = (MktCamChlConfDetail) redisUtils.get("MktCamChlConfDetail_" + contactConfId);
        if (detail == null) {
            camScript = camScriptMapper.selectByConfId(contactConfId);
        } else {
            camScript = detail.getCamScript();
            // 从缓存中拿不到
            if(camScript ==null){
                camScript = camScriptMapper.selectByConfId(contactConfId);
            }
            // 从数据库中查询不到
            if(camScript == null){
                if(scriptDesc!=null){
                    newScript.setScriptDesc(scriptDesc);
                }
            }
        }

        newScript.setMktCampaignId(0L);
        newScript.setEvtContactConfId(newConfId);
        newScript.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
        newScript.setCreateDate(new Date());
        newScript.setCreateStaff(UserUtil.loginId());
        newScript.setUpdateDate(new Date());
        newScript.setUpdateStaff(UserUtil.loginId());
        camScriptMapper.insert(newScript);
        //更新redis推送渠道配置
        MktCamChlConfDetail de = (MktCamChlConfDetail)redisUtils.get("MktCamChlConfDetail_"+newConfId);
        if (detail!=null){
            detail.setCamScript(newScript);
            redisUtils.set("MktCamChlConfDetail_"+newConfId,detail);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",newScript);
        return result;
    }

    @Override
    public Map<String,Object> addCamScript(Long userId, CamScriptAddVO addVO) {
        Map<String,Object> result = new HashMap<>();
        CamScript script = camScriptMapper.selectByConfId(addVO.getEvtContactConfId());
        if (script!=null){
            //todo copy结果为null需要处理
            //BeanUtil.copy(addVO,script);
            if(addVO.getScriptDesc() != null){
                script.setScriptDesc(addVO.getScriptDesc());
            }
            if(addVO.getLanId() != null){
                script.setLanId(addVO.getLanId());
            }
            script.setMktCampaignId(addVO.getMktCampaignId());
            script.setUpdateDate(new Date());
            script.setUpdateStaff(userId);
            camScriptMapper.updateByPrimaryKey(script);
        }else {
             script = BeanUtil.create(addVO,new CamScript());
            //todo 添加活动id
            script.setMktCampaignId(addVO.getMktCampaignId());
            script.setCreateDate(new Date());
            script.setUpdateDate(new Date());
            script.setCreateStaff(userId);
            script.setUpdateStaff(userId);
            script.setStatusCd("1000");
            camScriptMapper.insert(script);
        }
        //更新redis推送渠道配置
        MktCamChlConfDetail detail = (MktCamChlConfDetail)redisUtils.get("MktCamChlConfDetail_"+addVO.getEvtContactConfId());
        if (detail!=null){
            detail.setCamScript(script);
            redisUtils.set("MktCamChlConfDetail_"+addVO.getEvtContactConfId(),detail);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");
        return result;
    }


    @Override
    public Map<String,Object> editCamScript(Long userId, CamScriptEditVO editVO) {
        Map<String,Object> result = new HashMap<>();
        CamScript script = camScriptMapper.selectByPrimaryKey(editVO.getCamScriptId());
        if (script==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","活动关联脚本信息不存在");
            return result;
        }
        BeanUtil.copy(editVO,script);
        script.setUpdateDate(new Date());
        script.setUpdateStaff(userId);
        camScriptMapper.updateByPrimaryKey(script);
        //更新redis推送渠道配置
        MktCamChlConfDetail detail = (MktCamChlConfDetail)redisUtils.get("MktCamChlConfDetail_"+editVO.getEvtContactConfId());
        if (detail!=null){
            detail.setCamScript(script);
            redisUtils.set("MktCamChlConfDetail_"+editVO.getEvtContactConfId(),detail);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","修改成功");
        return result;
    }

    @Override
    public Map<String,Object> deleteCamScript(Long userId, Long camScriptId) {
        Map<String,Object> result = new HashMap<>();
            CamScript script = camScriptMapper.selectByPrimaryKey(camScriptId);
            if (script==null){
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","活动关联脚本信息不存在");
                return result;
            }
            camScriptMapper.deleteByPrimaryKey(camScriptId);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","删除成功");
        return result;
    }

    @Override
    public Map<String,Object> getCamScriptList(Long userId, Long evtContactConfId) {
        Map<String,Object> result = new HashMap<>();
        //todo  推送渠道id确定渠道
        CamScriptVO vo = new CamScriptVO();
        CamScript script = new CamScript();
        try {
            script = camScriptMapper.selectByConfId(evtContactConfId);
            vo = ChannelUtil.map2CamScriptVO(script);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[op:ChannelServiceImpl] fail to listChannel", e);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",vo);
        return result;
    }

    @Override
    public Map<String,Object> getCamScriptVODetail(Long userId, Long camScriptId) {
        return null;
    }
}
