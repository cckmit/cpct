package com.zjtelcom.cpct.service.impl.channel;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.dao.channel.MktScriptMapper;
import com.zjtelcom.cpct.domain.channel.Script;
import com.zjtelcom.cpct.dto.channel.MktScript;
import com.zjtelcom.cpct.dto.channel.QryMktScriptReq;
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
    public Map<String,Object> createMktScript(Long userId, MktScript addVO) {
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
    public Map<String,Object> modMktScript(Long userId, MktScript editVO) {
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
    public Map<String,Object> delMktScript(Long userId, MktScript mktScript) {
        Map<String,Object> result = new HashMap<>();
        Script script = scriptMapper.selectByPrimaryKey(mktScript.getScriptId());
        if (script==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","脚本信息不存在");
            return result;
        }
        scriptMapper.deleteByPrimaryKey(mktScript.getScriptId());
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultData","添加成功");
        return result;
    }

    @Override
    public Map<String,Object> qryMktScriptList(Long userId, QryMktScriptReq req) {
        Map<String,Object> result = new HashMap<>();
        List<ScriptVO> voList = new ArrayList<>();
        List<Script> scriptList = new ArrayList<>();
            String scriptName = null;
            Date createTime = null;
            Date updateTime = null;
            if (req.getParams().get("scriptName")!=null){
                scriptName = req.getParams().get("scriptName").toString();
            }
            if (req.getParams().get("createTime")!=null){
                createTime = new Date(Long.valueOf(req.getParams().get("createTime").toString()));
            }
            if (req.getParams().get("updateTime")!=null){
                updateTime = new Date(Long.valueOf(req.getParams().get("updateTime").toString()));
            }
            PageHelper.startPage(req.getPage(),req.getPageSize());
            scriptList = scriptMapper.selectAll(scriptName,createTime,updateTime);
            PageInfo info = new PageInfo(scriptList);
            for (Script script : scriptList){
                ScriptVO vo = ChannelUtil.map2ScriptVO(script);
                voList.add(vo);
            }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultData",voList);
        result.put("page",info);
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
