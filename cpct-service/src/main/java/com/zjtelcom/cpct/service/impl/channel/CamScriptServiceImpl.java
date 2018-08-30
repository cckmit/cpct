package com.zjtelcom.cpct.service.impl.channel;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.dao.channel.MktCamScriptMapper;
import com.zjtelcom.cpct.domain.channel.CamScript;
import com.zjtelcom.cpct.dto.channel.CamScriptAddVO;
import com.zjtelcom.cpct.dto.channel.CamScriptEditVO;
import com.zjtelcom.cpct.dto.channel.CamScriptVO;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.CamScriptService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class CamScriptServiceImpl extends BaseService implements CamScriptService {

    @Autowired
    private MktCamScriptMapper camScriptMapper;


    /**
     * 复制活动脚本
     * @param contactConfId
     * @param newConfId
     * @return
     */
    @Override
    public Map<String, Object> copyCamScript(Long contactConfId, Long newConfId) {
        Map<String,Object> result = new HashMap<>();
        CamScript script = camScriptMapper.selectByConfId(contactConfId);
        if (script==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","活动脚本不存在");
            return result;
        }
        CamScript newScript = BeanUtil.create(script,new CamScript());
        newScript.setEvtContactConfId(newConfId);
        camScriptMapper.insert(newScript);
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
            BeanUtil.copy(addVO,script);
            script.setMktCampaignId(123L);
            script.setUpdateDate(new Date());
            script.setUpdateStaff(userId);
            camScriptMapper.updateByPrimaryKey(script);
        }else {
             script = BeanUtil.create(addVO,new CamScript());
            //todo 添加活动id
            script.setMktCampaignId(123L);
            script.setCreateDate(new Date());
            script.setUpdateDate(new Date());
            script.setCreateStaff(userId);
            script.setUpdateStaff(userId);
            script.setStatusCd("1000");
            camScriptMapper.insert(script);
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
            logger.error("[op:ChannelServiceImpl] fail to listChannel ", e);
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
