package com.zjtelcom.cpct.service.impl.system;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.domain.system.SysRole;
import com.zjtelcom.cpct.enums.ParamKeyEnum;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.synchronize.sys.SynSysParamsService;
import com.zjtelcom.cpct.service.system.SysParamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class SysParamsServiceImpl extends BaseService implements SysParamsService {

    @Autowired
    private SysParamsMapper sysParamsMapper;
    @Autowired
    private SynSysParamsService synSysParamsService;

    @Value("${sync.value}")
    private String value;

    @Override
    public Map<String,Object> listParams(String paramName, String paramKey,Integer page,Integer pageSize) {
        Map<String,Object> result = new HashMap<>();
        PageHelper.startPage(page,pageSize);
        List<SysParams> list = sysParamsMapper.selectAll(paramName, paramKey);
        Page pageInfo = new Page(new PageInfo(list));
        result.put("resultCode",CommonConstant.CODE_SUCCESS);
        result.put("resultMsg","");
        result.put("data",list);
        result.put("pageInfo",pageInfo);

        return result;
    }

    @Override
    public Map<String,Object> saveParams(final SysParams sysParams) {
        Map<String,Object> result = new HashMap<>();
        //todo 判断字段是否为空

        //todo 判断参数名是否重复

        //todo 获取当前登录用户id
        Long loginId = 1L;
        sysParams.setCreateStaff(loginId);
        sysParams.setCreateDate(new Date());

        int flag = sysParamsMapper.insert(sysParams);
        result.put("resultCode",CommonConstant.CODE_SUCCESS);

        if (value.equals("1")){
            new Thread(){
                public void run(){
                    try {
                        synSysParamsService.synchronizeSingleParam(sysParams.getParamId(),"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.run();
        }

        return result;
    }

    @Override
    public Map<String,Object> updateParams(final SysParams sysParams) {
        Map<String,Object> result = new HashMap<>();
        //todo 判断字段是否为空

        //todo 判断参数名是否重复

        //todo 获取当前登录用户id
        Long loginId = 1L;
        sysParams.setUpdateStaff(loginId);
        sysParams.setUpdateDate(new Date());
        int flag = sysParamsMapper.updateByPrimaryKey(sysParams);
        result.put("resultCode",CommonConstant.CODE_SUCCESS);

        if (value.equals("1")){
            new Thread(){
                public void run(){
                    try {
                        synSysParamsService.synchronizeSingleParam(sysParams.getParamId(),"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        return result;
    }

    @Override
    public Map<String,Object> getParams(Long id) {
        Map<String,Object> result = new HashMap<>();
        if(id == null) {
            //todo 为空异常
        }
        List<SysParams> sysParams = sysParamsMapper.selectByPrimaryKey(id);
        result.put("resultCode",CommonConstant.CODE_SUCCESS);
        result.put("data",sysParams);

        return result;
    }

    @Override
    public Map<String,Object> delParams(final Long id) {
        Map<String,Object> result = new HashMap<>();

        //todo 验证是否可以删除

        sysParamsMapper.deleteByPrimaryKey(id);
        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("resultMsg","保存成功");

        if (value.equals("1")){
            new Thread(){
                public void run(){
                    try {
                        synSysParamsService.deleteSingleParam(id,"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        return result;
    }

    @Override
    public Map<String, Object> listParamsByKey(String key) {
        Map<String,Object> result = new HashMap<>();

        List<Map<String,String>> list = new ArrayList<>();

        list = sysParamsMapper.listParamsByKey(key);

        result.put("data",list);
        result.put("resultCode",CommonConstant.CODE_SUCCESS);

        return result;
    }

    @Override
    public Map<String, String> getParamsByKey(String keyWord, String key) {
        Map<String,String> result = new HashMap<>();

        result = sysParamsMapper.getParamsByKey(keyWord,key);

        return result;
    }

    @Override
    public Map<String, String> getParamsByValue(String keyWord, String value) {
        Map<String,String> result = new HashMap<>();

        result = sysParamsMapper.getParamsByValue(keyWord,value);

        return result;
    }

    /**
     * 获取活动总览页面的筛选条件列表
     * created by linchao
     * @return
     */
    @Override
    public Map<String, Object> listParamsByKeyForCampaign() {
        Map<String,Object> result = new HashMap<>();
        List<SysParams> statusParams = sysParamsMapper.listParamsByKeyForCampaign(ParamKeyEnum.STATUS_CD.getParamKey());
        result.put(ParamKeyEnum.STATUS_CD.getParamName(), statusParams);

        List<SysParams> tiggerParams = sysParamsMapper.listParamsByKeyForCampaign(ParamKeyEnum.TIGGER_TYPE.getParamKey());
        result.put(ParamKeyEnum.TIGGER_TYPE.getParamName(), tiggerParams);

        List<SysParams> campaignParams = sysParamsMapper.listParamsByKeyForCampaign(ParamKeyEnum.MKT_CAMPAIGN_TYPE.getParamKey());
        result.put(ParamKeyEnum.MKT_CAMPAIGN_TYPE.getParamName(), campaignParams);

        List<SysParams> execParams = sysParamsMapper.listParamsByKeyForCampaign(ParamKeyEnum.EXEC_TYPE.getParamKey());
        result.put(ParamKeyEnum.EXEC_TYPE.getParamName(), execParams);

        List<SysParams> mktCampaignCategoryParams = sysParamsMapper.listParamsByKeyForCampaign(ParamKeyEnum.MKT_CAMPAIGN_CATEGORY.getParamKey());
        result.put(ParamKeyEnum.MKT_CAMPAIGN_CATEGORY.getParamName(), mktCampaignCategoryParams);

        List<SysParams> timeTypeParams = sysParamsMapper.listParamsByKeyForCampaign(ParamKeyEnum.TIME_TYPE.getParamKey());
        result.put(ParamKeyEnum.TIME_TYPE.getParamName(), timeTypeParams);
        return result;
    }

}
