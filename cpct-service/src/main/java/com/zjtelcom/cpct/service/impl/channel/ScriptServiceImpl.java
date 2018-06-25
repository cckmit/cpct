package com.zjtelcom.cpct.service.impl.channel;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.dao.channel.MktScriptMapper;
import com.zjtelcom.cpct.domain.channel.Script;
import com.zjtelcom.cpct.dto.ScriptAddVO;
import com.zjtelcom.cpct.dto.ScriptEditVO;
import com.zjtelcom.cpct.dto.ScriptVO;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.ScriptService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class ScriptServiceImpl extends BaseService  implements ScriptService {

    @Autowired
    private MktScriptMapper scriptMapper;


    @Override
    public RespInfo addScript(Long userId, ScriptAddVO addVO) {
        Script script = BeanUtil.create(addVO,new Script());
        script.setCreateDate(new Date());
        script.setUpdateDate(new Date());
        script.setCreateStaff(userId);
        script.setUpdateStaff(userId);
        script.setStatusCd("1000");
        scriptMapper.insert(script);
        return RespInfo.build(CODE_SUCCESS,"添加成功");
    }

    @Override
    public RespInfo editScript(Long userId, ScriptEditVO editVO) {
        Script script = scriptMapper.selectByPrimaryKey(editVO.getScriptId());
        if (script==null){
            return RespInfo.build(CODE_FAIL,"脚本信息不存在");
        }
        BeanUtil.copy(editVO,script);
        script.setUpdateDate(new Date());
        script.setUpdateStaff(userId);
        scriptMapper.updateByPrimaryKey(script);
        return RespInfo.build(CODE_SUCCESS,"修改成功");
    }

    @Override
    public RespInfo deleteScript(Long userId, Long scriptId) {
        Script script = scriptMapper.selectByPrimaryKey(scriptId);
        if (script==null){
            return RespInfo.build(CODE_FAIL,"脚本信息不存在");
        }
        scriptMapper.deleteByPrimaryKey(scriptId);
        return RespInfo.build(CODE_SUCCESS,"删除成功");
    }

    @Override
    public List<ScriptVO> getScriptList(Long userId, Map<String, Object> params, Integer page, Integer pageSize) {
        List<ScriptVO> voList = new ArrayList<>();
        List<Script> scriptList = new ArrayList<>();
        try {
            String scriptName = null;
            Date createTime = null;
            Date updateTime = null;
            if (params.get("scriptName")!=null){
                scriptName = params.get("scriptName").toString();
            }
            if (params.get("createTime")!=null){
                createTime = new Date(Long.valueOf(params.get("createTime").toString()));
            }
            if (params.get("updateTime")!=null){
                updateTime = new Date(Long.valueOf(params.get("updateTime").toString()));
            }
            scriptList = scriptMapper.selectAll(scriptName,createTime,updateTime);
            for (Script script : scriptList){
                ScriptVO vo = ChannelUtil.map2ScriptVO(script);
                voList.add(vo);
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[op:ChannelServiceImpl] fail to listChannel ", e);
        }
        return voList;
    }

    @Override
    public ScriptVO getScriptVODetail(Long userId, Long scriptId) {
        ScriptVO vo = new ScriptVO();
        try {
            Script script = scriptMapper.selectByPrimaryKey(scriptId);
            vo = ChannelUtil.map2ScriptVO(script);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[op:ChannelServiceImpl] fail to listChannel ", e);
        }
        return vo;
    }
}
