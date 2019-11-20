package com.zjtelcom.cpct.service.report;

import java.util.Map;

public interface ServiceCamReportService {

    Map<String,Object> serviceCamInfo(Map<String,Object> param);

    Map<String,Object> selectOrgIdByStaffId(Map<String,Object> param);


}
