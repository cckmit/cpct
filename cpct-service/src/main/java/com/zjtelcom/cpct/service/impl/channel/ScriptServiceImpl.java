package com.zjtelcom.cpct.service.impl.channel;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.dao.channel.MktScriptMapper;
import com.zjtelcom.cpct.domain.channel.Script;
import com.zjtelcom.cpct.dto.channel.ScriptAddVO;
import com.zjtelcom.cpct.dto.channel.ScriptEditVO;
import com.zjtelcom.cpct.dto.channel.ScriptVO;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.ScriptService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class ScriptServiceImpl extends BaseService  implements ScriptService {

    @Autowired
    private MktScriptMapper scriptMapper;


    @Override
    public Map<String,Object> addScript(Long userId, ScriptAddVO addVO) {
        Map<String,Object> result = new HashMap<>();
        Script script = BeanUtil.create(addVO,new Script());
        script.setCreateDate(new Date());
        script.setUpdateDate(new Date());
        script.setCreateStaff(userId);
        script.setUpdateStaff(userId);
        script.setStatusCd("1000");
        scriptMapper.insert(script);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultData","添加成功");
        return result;
    }

    @Override
    public Map<String,Object> editScript(Long userId, ScriptEditVO editVO) {
        Map<String,Object> result = new HashMap<>();
        Script script = scriptMapper.selectByPrimaryKey(editVO.getScriptId());
        if (script==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","脚本信息不存在");
            return result;
        }
        BeanUtil.copy(editVO,script);
        script.setUpdateDate(new Date());
        script.setUpdateStaff(userId);
        scriptMapper.updateByPrimaryKey(script);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultData","添加成功");
        return result;
    }

    @Override
    public Map<String,Object> deleteScript(Long userId, Long scriptId) {
        Map<String,Object> result = new HashMap<>();
        Script script = scriptMapper.selectByPrimaryKey(scriptId);
        if (script==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","脚本信息不存在");
            return result;
        }
        scriptMapper.deleteByPrimaryKey(scriptId);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultData","添加成功");
        return result;
    }

    @Override
    public Map<String,Object> getScriptList(Long userId, Map<String, Object> params, Integer page, Integer pageSize) {
        Map<String,Object> result = new HashMap<>();
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
            PageHelper.startPage(page,pageSize);
            scriptList = scriptMapper.selectAll(scriptName,createTime,updateTime);
            PageInfo info = new PageInfo(scriptList);
            for (Script script : scriptList){
                ScriptVO vo = ChannelUtil.map2ScriptVO(script);
                voList.add(vo);
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[op:ChannelServiceImpl] fail to listChannel ", e);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultData",voList);
        return result;
    }

    @Override
    public Map<String,Object> getScriptVODetail(Long userId, Long scriptId) {
        Map<String,Object> result = new HashMap<>();
        ScriptVO vo = new ScriptVO();
        try {
            Script script = scriptMapper.selectByPrimaryKey(scriptId);
            vo = ChannelUtil.map2ScriptVO(script);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[op:ChannelServiceImpl] fail to listChannel ", e);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultData",vo);
        return result;
    }
}
