package com.zjtelcom.cpct.service.impl.channel;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.dao.channel.ContactChannelMapper;
import com.zjtelcom.cpct.dao.channel.MktScriptMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.domain.channel.Script;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dto.channel.MktScript;
import com.zjtelcom.cpct.dto.channel.QryMktScriptReq;
import com.zjtelcom.cpct.dto.channel.ScriptVO;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.ScriptService;
import com.zjtelcom.cpct.service.synchronize.script.SynScriptService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class ScriptServiceImpl extends BaseService  implements ScriptService {

    @Autowired
    private MktScriptMapper scriptMapper;
    @Autowired
    private ContactChannelMapper channelMapper;
    @Autowired
    private SynScriptService synScriptService;
    @Autowired
    private SysParamsMapper sysParamsMapper;

    @Value("${sync.value}")
    private String value;

    @Override
    public Map<String, Object> getScriptList(Long userId, String scriptName,String scriptType) {
        Map<String,Object> result = new HashMap<>();
        List<ScriptVO> voList = new ArrayList<>();
        List<Script> scriptList = new ArrayList<>();
        scriptList = scriptMapper.findByScriptName(scriptName,scriptType);
        for (Script script : scriptList){
            ScriptVO vo = ChannelUtil.map2ScriptVO(script);
            voList.add(vo);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",voList);
        return result;

    }

    @Override
    public Map<String,Object> createMktScript(Long userId, MktScript addVO) {
        Map<String,Object> result = new HashMap<>();
        final Script script = BeanUtil.create(addVO,new Script());
        script.setCreateDate(new Date());
        script.setUpdateDate(new Date());
        script.setCreateStaff(userId);
        script.setUpdateStaff(userId);
        script.setStatusCd("1000");
        script.setSuitChannelType("100000");
        scriptMapper.insert(script);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");

        if (value.equals("1")){
            new Thread(){
                public void run(){
                    try {
                        synScriptService.synchronizeScript(script.getScriptId(),"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        return result;
    }

    @Override
    public Map<String,Object> modMktScript(Long userId, MktScript editVO) {
        Map<String,Object> result = new HashMap<>();
        final Script script = scriptMapper.selectByPrimaryKey(editVO.getScriptId());
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

        if (value.equals("1")){
            new Thread(){
                public void run(){
                    try {
                        synScriptService.synchronizeScript(script.getScriptId(),"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        return result;
    }

    @Override
    public Map<String,Object> delMktScript(Long userId, MktScript mktScript) {
        Map<String,Object> result = new HashMap<>();
        final Script script = scriptMapper.selectByPrimaryKey(mktScript.getScriptId());
        if (script==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","脚本信息不存在");
            return result;
        }
        scriptMapper.deleteByPrimaryKey(mktScript.getScriptId());
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","删除成功");

        if (value.equals("1")){
            new Thread(){
                public void run(){
                    try {
                        synScriptService.delelteSynchronizeScript(script.getScriptId(),"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

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
                SysParams sysParams = sysParamsMapper.findParamsByValue("CAM-0002",script.getScriptType());
                vo.setScriptTypeName(sysParams==null ? "" : sysParams.getParamName());
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
