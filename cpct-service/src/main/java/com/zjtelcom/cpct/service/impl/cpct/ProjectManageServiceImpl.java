package com.zjtelcom.cpct.service.impl.cpct;

import com.ctzj.smt.bss.sysmgr.model.common.SysmgrResultObject;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemUserDto;
import com.ctzj.smt.bss.sysmgr.privilege.service.dubbo.api.ISystemUserDtoDubboService;
import com.zjtelcom.cpct.dao.grouping.TrialOperationMapper;
import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import com.zjtelcom.cpct.service.cpct.ProjectManageService;
import com.zjtelcom.cpct.util.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @author: linchao
 * @date: 2019/12/09 15:15
 * @version: V1.0
 */
public class ProjectManageServiceImpl implements ProjectManageService{

    private Logger logger = LoggerFactory.getLogger(ProjectManageServiceImpl.class);

    @Autowired(required = false)
    private ISystemUserDtoDubboService iSystemUserDtoDubboService;

    @Autowired
    private TrialOperationMapper trialOperationMapper;

    @Override
    public Map<String, Object> updateProjectStateTime(Map<String, String> params) {
        Long id = Long.valueOf((String) params.get("id"));
        if (id != null) {
             TrialOperation trialOperation = trialOperationMapper.selectByPrimaryKey(id);
            if (trialOperation != null) {
                params.put("workFlowId", trialOperation.getBatchNum().toString());
            }
        }
        getCreater(params);
        // 调用dubbo接口
        return null;
    }


    @Override
    public Map<String, Object> updateProjectPcState(Long mktCampaginId) {
        Map<String, String> params = new HashMap<>();
        params.put("policyId", mktCampaginId.toString());
        params.put("state", "0");
        getCreater(params);

        // 调用dubbo接口
        return null;
    }


    private void getCreater(Map<String, String> params) {
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