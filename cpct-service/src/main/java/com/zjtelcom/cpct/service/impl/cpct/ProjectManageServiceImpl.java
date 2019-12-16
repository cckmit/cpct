package com.zjtelcom.cpct.service.impl.cpct;

import com.alibaba.fastjson.JSON;
import com.ctzj.smt.bss.cooperate.service.dubbo.ICpcAPIService;
import com.ctzj.smt.bss.sysmgr.model.common.SysmgrResultObject;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemUserDto;
import com.ctzj.smt.bss.sysmgr.privilege.service.dubbo.api.ISystemUserDtoDubboService;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.grouping.TrialOperationMapper;
import com.zjtelcom.cpct.dao.strategy.MktCamStrategyRelMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.service.cpct.ProjectManageService;
import com.zjtelcom.cpct.util.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: linchao
 * @date: 2019/12/09 15:15
 * @version: V1.0
 */
@Service
@Transactional
public class ProjectManageServiceImpl implements ProjectManageService{

    private Logger logger = LoggerFactory.getLogger(ProjectManageServiceImpl.class);

    @Autowired(required = false)
    private ISystemUserDtoDubboService iSystemUserDtoDubboService;

    @Autowired
    private TrialOperationMapper trialOperationMapper;

    @Autowired
    private MktStrategyConfMapper mktStrategyConfMapper;

    @Autowired(required = false)
    private ICpcAPIService iCpcAPIService;


    /**
     * 活动生失效时间修改接口
     *
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> updateProjectStateTime(Map<String, Object> params) {
        Long id = (Long) params.get("id");
        if (id != null) {
            TrialOperation trialOperation = trialOperationMapper.selectByPrimaryKey(id);
            if (trialOperation != null) {
                params.put("workFlowId", trialOperation.getBatchNum().toString());
            }
        }
        getCreater(params);
        logger.info("updateProjectStateTime接口入参：" + JSON.toJSONString(params));
        // 调用系统中心dubbo接口 - 4.18活动生失效时间修改接口
        Map<String, Object> resultMap = iCpcAPIService.updateProjectStateTime(params);
        logger.info("updateProjectStateTime接口出参：" + JSON.toJSONString(resultMap));
        if (CommonConstant.DUBBO_SUCCESS.equals(resultMap.get("resultCode"))){
            resultMap.put("resultCode", CommonConstant.CODE_SUCCESS);
        } else {
            resultMap.put("resultCode", CommonConstant.CODE_FAIL);
        }
        return resultMap;
    }


    /**
     * 4.19	派单活动状态修改接口
     *
     * @param mktCampaginId
     * @return
     */
    @Override
    public Map<String, Object> updateProjectPcState(Long mktCampaginId) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        List<MktStrategyConfDO> mktStrategyConfDOS = mktStrategyConfMapper.selectByCampaignId(mktCampaginId);
        params.put("state", "0");
        getCreater(params);
        for (MktStrategyConfDO mktStrategyConfDO : mktStrategyConfDOS) {
            params.put("policyId", mktStrategyConfDO.getInitId());
            logger.info("updateProjectPcState接口入参：" + JSON.toJSONString(params));
            // 调用系统中心dubbo接口 - 4.19派单活动状态修改接口
            resultMap = iCpcAPIService.updateProjectPcState(params);
            logger.info("updateProjectPcState接口出参：" + JSON.toJSONString(resultMap));
        }
        return resultMap;
    }


    private void getCreater(Map<String, Object> params) {
        Long createStaff = UserUtil.loginId();
        try {
            SysmgrResultObject<SystemUserDto> systemUserDtoSysmgrResultObject = iSystemUserDtoDubboService.qrySystemUserDto(createStaff, new ArrayList<Long>());
            if (systemUserDtoSysmgrResultObject != null) {
                if (systemUserDtoSysmgrResultObject.getResultObject() != null) {
                    params.put("updateLoginname", systemUserDtoSysmgrResultObject.getResultObject().getStaffCode());     //修改人号码
                    params.put("updateLoginWorkNo", systemUserDtoSysmgrResultObject.getResultObject().getSysUserCode()); // 修改人工号
                    params.put("updateUsername", systemUserDtoSysmgrResultObject.getResultObject().getStaffName());      // 修改人姓名
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("创建人查询失败：" + createStaff, e );
        }
    }

}