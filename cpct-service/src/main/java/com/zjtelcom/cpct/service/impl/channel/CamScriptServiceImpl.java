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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class CamScriptServiceImpl extends BaseService implements CamScriptService {

    @Autowired
    private MktCamScriptMapper camScriptMapper;



    @Override
    public RespInfo addCamScript(Long userId, CamScriptAddVO addVO) {
        CamScript script = BeanUtil.create(addVO,new CamScript());
        script.setCreateDate(new Date());
        script.setUpdateDate(new Date());
        script.setCreateStaff(userId);
        script.setUpdateStaff(userId);
        script.setStatusCd("1000");
        camScriptMapper.insert(script);
        return RespInfo.build(CODE_SUCCESS,"添加成功");
    }

    @Override
    public RespInfo editCamScript(Long userId, CamScriptEditVO editVO) {
        CamScript script = camScriptMapper.selectByPrimaryKey(editVO.getCamScriptId());
        if (script==null){
            return RespInfo.build(CODE_FAIL,"活动关联脚本信息不存在");
        }
        BeanUtil.copy(editVO,script);
        script.setUpdateDate(new Date());
        script.setUpdateStaff(userId);
        camScriptMapper.updateByPrimaryKey(script);
        return RespInfo.build(CODE_SUCCESS,"修改成功");
    }

    @Override
    public RespInfo deleteCamScript(Long userId, List<Long> camScriptIdList) {
        for (Long id : camScriptIdList){
            CamScript script = camScriptMapper.selectByPrimaryKey(id);
            if (script==null){
                return RespInfo.build(CODE_FAIL,"活动关联脚本信息不存在");
            }
            camScriptMapper.deleteByPrimaryKey(id);
        }
        return null;
    }

    @Override
    public List<CamScriptVO> getCamScriptList(Long userId, Long campaignId, Long evtContactConfId) {
        //todo  活动标识确定活动 推送渠道id确定渠道
        List<CamScriptVO> voList = new ArrayList<>();
        List<CamScript> scriptList = new ArrayList<>();
        try {
            scriptList = camScriptMapper.selectAll(campaignId,evtContactConfId);
            for (CamScript script : scriptList){
                CamScriptVO vo = ChannelUtil.map2CamScriptVO(script);
                voList.add(vo);
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[op:ChannelServiceImpl] fail to listChannel ", e);
        }
        return voList;
    }

    @Override
    public CamScriptVO getCamScriptVODetail(Long userId, Long camScriptId) {
        return null;
    }
}
