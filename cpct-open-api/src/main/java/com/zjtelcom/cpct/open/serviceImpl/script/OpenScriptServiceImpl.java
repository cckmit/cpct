package com.zjtelcom.cpct.open.serviceImpl.script;
import java.util.Date;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.dao.channel.ContactChannelMapper;
import com.zjtelcom.cpct.dao.channel.MktScriptMapper;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.domain.channel.Script;
import com.zjtelcom.cpct.dto.channel.MktScript;
import com.zjtelcom.cpct.dto.channel.QryMktScriptReq;
import com.zjtelcom.cpct.dto.channel.ScriptVO;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.common.CommonUtil;
import com.zjtelcom.cpct.open.base.service.BaseService;
import com.zjtelcom.cpct.open.service.script.OpenScriptService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

/**
 * @Auther: anson
 * @Date: 2018/10/30
 * @Description:营销脚本
 */
@Service
@Transactional
public class OpenScriptServiceImpl extends BaseService implements OpenScriptService {


    @Autowired
    private MktScriptMapper scriptMapper;
    @Autowired
    private ContactChannelMapper channelMapper;


    /**
     * 根据特定字段信息搜索
     *
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> selectScriptList(Map<String,Object> params) {
        Map<String,Object> result = new HashMap<>();
        Script script=new Script();
        if(StringUtils.isNotBlank((String) params.get("scriptId"))){
            script.setScriptId(Long.valueOf((String) params.get("scriptId")));
        }
        script.setScriptName((String) params.get("scriptName"));
        script.setScriptType((String) params.get("scriptType"));
        script.setSuitChannelType((String) params.get("suitChannelType"));
        script.setExecChannel((String) params.get("execChannel"));
        script.setStatusCd((String) params.get("statusCd"));
        if(StringUtils.isNotBlank((String) params.get("createStaff"))){
            script.setCreateStaff(Long.valueOf((String) params.get("createStaff")));
        }
        CommonUtil.setPage(params);
        List<Script> scripts = scriptMapper.selectScriptList(script);
        Page pageInfo = new Page(new PageInfo(scripts));
        result.put("params",scripts);
        result.put("size", String.valueOf(pageInfo.getTotal()));
        return result;

    }



    @Override
    public Map<String, Object> selectByPrimaryKey(Long scriptId) {
        Map<String, Object> map = new HashMap<>();
        Script script = scriptMapper.selectByPrimaryKey(scriptId);
        if(null!=script){
            map.put("params",script);
        }else{
            throw new SystemException("对应营销脚本不存在！");
        }
        return map;
    }

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
        result.put("params",script);
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
        result.put("resultMsg","修改成功");
        return result;
    }

    @Override
    public Map<String,Object> delMktScript(Long scriptId) {
        Script script = scriptMapper.selectByPrimaryKey(scriptId);
        if (script==null){
           throw new SystemException("对应营销脚本信息不存在!");
        }
        scriptMapper.deleteByPrimaryKey(scriptId);
        return new HashMap<>();
    }

    @Override
    public Map<String,Object> qryMktScriptList(Long userId, QryMktScriptReq req) {
        Map<String,Object> result = new HashMap<>();
        List<ScriptVO> voList = new ArrayList<>();
        List<Script> scriptList = new ArrayList<>();
        String scriptName = null;
        Date createTime = null;
        Date updateTime = null;
        String scriptType = null;
        if (req.getParams().get("scriptName")!=null){
            scriptName = req.getParams().get("scriptName").toString();
        }
        if (req.getParams().get("createTime")!=null){
            createTime = new Date(Long.valueOf(req.getParams().get("createTime").toString()));
        }
        if (req.getParams().get("updateTime")!=null){
            updateTime = new Date(Long.valueOf(req.getParams().get("updateTime").toString()));
        }
        if (req.getParams().get("scriptType")!=null && !req.getParams().get("scriptType").equals("-1")){
            scriptType = req.getParams().get("scriptType").toString();
        }
        PageHelper.startPage(req.getPage(),req.getPageSize());
        scriptList = scriptMapper.selectAll(scriptName,createTime,updateTime,scriptType);
        Page info = new Page(new PageInfo(scriptList));
        for (Script script : scriptList){
            ScriptVO vo = ChannelUtil.map2ScriptVO(script);
            if (script.getExecChannel()!=null){
                Channel channel = channelMapper.selectByPrimaryKey(Long.valueOf(script.getExecChannel()));
                if (channel!=null){
                    vo.setChannelName(channel.getContactChlName());
                }
            }
            voList.add(vo);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",voList);
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
            Channel channel = channelMapper.selectByPrimaryKey(Long.valueOf(script.getExecChannel()));
            if (channel!=null){
                vo.setChannelName(channel.getContactChlName());
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[op:ChannelServiceImpl] fail to listChannel ", e);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",vo);
        return result;
    }
}
