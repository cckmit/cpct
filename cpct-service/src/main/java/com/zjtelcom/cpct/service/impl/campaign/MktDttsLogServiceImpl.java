package com.zjtelcom.cpct.service.impl.campaign;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.dao.campaign.MktDttsLogMapper;
import com.zjtelcom.cpct.domain.campaign.MktDttsLog;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.campaign.MktDttsLogService;
import com.zjtelcom.cpct.util.MapUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
@Transactional
public class MktDttsLogServiceImpl extends BaseService implements MktDttsLogService {

    @Autowired
    private MktDttsLogMapper mktDttsLogMapper;

    /**
     * 新增定时任务日志
     * @param mktDttsLog
     * @return
     */
    @Override
    public Map<String,Object> saveMktDttsLog(MktDttsLog mktDttsLog) {
        Map<String,Object> result = new HashMap<>();
        mktDttsLog.setStatusDate(new Date());
        mktDttsLog.setCreateDate(new Date());
        mktDttsLog.setCreateStaff(UserUtil.loginId());
        mktDttsLogMapper.insert(mktDttsLog);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");
        return result;
    }

    /**
     * 编辑定时任务日志
     * @param mktDttsLog
     * @return
     */
    @Override
    public Map<String,Object> updateMktDttsLog(MktDttsLog mktDttsLog) {
        Map<String,Object> result = new HashMap<>();
        mktDttsLog.setStatusDate(new Date());
        mktDttsLog.setUpdateDate(new Date());
        mktDttsLog.setUpdateStaff(UserUtil.loginId());
        mktDttsLogMapper.updateByPrimaryKey(mktDttsLog);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","更新成功");
        return result;
    }

    /**
     * 定时任务详情
     * @param dttsLogId
     * @return
     */
    @Override
    public Map<String,Object> getMktDttsLog(Long dttsLogId) {
        Map<String,Object> result = new HashMap<>();
        MktDttsLog mktDttsLog = mktDttsLogMapper.selectByPrimaryKey(dttsLogId);
        if(mktDttsLog == null) {
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","定时任务日志不存在");
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",mktDttsLog);
        return result;
    }

    /**
     * 定时任务列表
     * @param params
     * @return
     */
    @Override
    public Map<String,Object> getMktDttsLogList(Map<String, Object> params) {
        Map<String,Object> result = new HashMap<>();
        MktDttsLog mktDttsLog = new MktDttsLog();
        String dttsType = MapUtil.getString(params.get("dttsType"));
        if(StringUtils.isNotBlank(dttsType)){
            mktDttsLog.setDttsType(dttsType);
        }
        Integer page = MapUtil.getIntNum(params.get("page"));
        Integer pageSize = MapUtil.getIntNum(params.get("pageSize"));
        PageHelper.startPage(page, pageSize);
        List<MktDttsLog> mktDttsLogList = mktDttsLogMapper.selectByCondition(mktDttsLog);
        Page pageInfo = new Page(new PageInfo(mktDttsLogList));
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",mktDttsLogList);
        result.put("page",pageInfo);
        return result;
    }
}
