package com.zjtelcom.cpct.service.grouping;

import com.zjhcsoft.eagle.main.dubbo.model.policy.CalcReqModel;
import com.zjtelcom.cpct.domain.grouping.TarGrpConditionDO;
import com.zjtelcom.cpct.dto.grouping.TarGrpConditionDTO;
import com.zjtelcom.cpct.dto.grouping.TarGrpDetail;
import java.util.List;
import java.util.Map;

/**
 * @Description 目标分群service
 * @Author pengy
 * @Date 2018/6/25 10:34
 */
public interface TarGrpService {

    Map<String, Object> createTarGrp(TarGrpDetail tarGrpDetail);

    Map<String, Object> saveTagNumFetch(Long mktCamGrpRulId, List<TarGrpConditionDTO> tarGrpConditionDTOList);

    Map<String,Object> delTarGrpCondition(Long conditionId);

    Map<String,Object> editTarGrpConditionDO(Long conditionId);

    Map<String,Object> updateTarGrpConditionDO(TarGrpConditionDO tarGrpConditionDO);

    Map<String, Object> saveBigDataModel(Long mktCamGrpRulId);

    Map<String,Object> listTarGrpCondition(Long mktCamGrpRulId);

    Map<String,Object> listBigDataModel(Long mktCamGrpRulId);

    Map<String,Object> strategyTrial(CalcReqModel req, String serialNum);

}
