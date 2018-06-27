package com.zjtelcom.cpct.service.impl.grouping;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCamGrpRulMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamGrpRul;
import com.zjtelcom.cpct.domain.grouping.TarGrpConditionDO;
import com.zjtelcom.cpct.dto.grouping.TarGrp;
import com.zjtelcom.cpct.dto.grouping.TarGrpConditionDTO;
import com.zjtelcom.cpct.dto.grouping.TarGrpDetail;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.grouping.TarGrpService;
import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 目标分群serviceImpl
 * @Author pengy
 * @Date 2018/6/25 10:34
 */
@Service
@Transactional
public class TarGrpServiceImpl extends BaseService implements TarGrpService {

    @Autowired
    private TarGrpMapper tarGrpMapper;
    @Autowired
    private TarGrpConditionMapper tarGrpConditionMapper;
    @Autowired
    private MktCamGrpRulMapper mktCamGrpRulMapper;

    /**
     * 新增目标分群
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> createTarGrp(TarGrpDetail tarGrpDetail) {
        TarGrp tarGrp = new TarGrp();
        Map<String, Object> maps = new HashMap<>();
        try {
            tarGrpMapper.insert(tarGrpDetail);
            CopyPropertiesUtil.copyBean2Bean(tarGrp, tarGrpDetail);
        } catch (Exception e) {
            maps.put("resultCode", CommonConstant.CODE_FAIL);
            maps.put("resultMsg", StringUtils.EMPTY);
            maps.put("tarGrp", StringUtils.EMPTY);
            logger.error("[op:TarGrpServiceImpl] fail to createTarGrp ", e);
            return maps;
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("tarGrp", tarGrp);
        return maps;
    }

    /**
     * 新增客户分群
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> saveTagNumFetch(Long mktCamGrpRulId, List<TarGrpConditionDTO> tarGrpConditionDTOList) {
        Map<String, Object> maps = new HashMap<>();
        TarGrpDetail tarGrpDetail = new TarGrpDetail();
        try {
            //生成客户分群
            tarGrpDetail = new TarGrpDetail();
            tarGrpMapper.insert(tarGrpDetail);
            //添加客户分群条件
            for (int i = 0; i < tarGrpConditionDTOList.size(); i++) {
                tarGrpConditionMapper.insert(tarGrpConditionDTOList.get(i));
            }
            //更新营销活动分群规则表
            MktCamGrpRul mktCamGrpRul = new MktCamGrpRul();
            mktCamGrpRul.setMktCamGrpRulId(mktCamGrpRulId);
            mktCamGrpRul.setTarGrpId(tarGrpDetail.getTarGrpId());
            mktCamGrpRulMapper.updateByPrimaryKey(mktCamGrpRul);

        } catch (Exception e) {
            maps.put("resultCode", CommonConstant.CODE_FAIL);
            maps.put("resultMsg", ErrorCode.SAVE_TAR_GRP_FAILURE.getErrorMsg());
            maps.put("tarGrp", StringUtils.EMPTY);
            logger.error("[op:TarGrpServiceImpl] fail to saveTagNumFetch ", e);
            return maps;
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("tarGrp", tarGrpDetail);
        return maps;
    }

    /**
     * 删除目标分群条件
     */
    @Override
    public Map<String, Object> delTarGrpCondition(Long conditionId) {
        Map<String, Object> mapsT = new HashMap<>();
        try {
            tarGrpConditionMapper.deleteByPrimaryKey(conditionId);
        } catch (Exception e) {
            mapsT.put("resultCode", CommonConstant.CODE_FAIL);
            mapsT.put("resultMsg", ErrorCode.DEL_TAR_GRP_CONDITION_FAILURE.getErrorMsg());
            mapsT.put("resultObject", StringUtils.EMPTY);
            logger.error("[op:TarGrpServiceImpl] fail to delTarGrpCondition ", e);
            return mapsT;
        }
        mapsT.put("resultCode", CommonConstant.CODE_SUCCESS);
        mapsT.put("resultMsg", StringUtils.EMPTY);
        mapsT.put("resultObject", StringUtils.EMPTY);
        return mapsT;
    }

    /**
     * 编辑目标分群条件
     */
    @Override
    public Map<String, Object> editTarGrpConditionDO(Long conditionId) {
        Map<String, Object> maps = new HashMap<>();
        TarGrpConditionDO tarGrpConditionDO = new TarGrpConditionDO();
        try {
            tarGrpConditionDO = tarGrpConditionMapper.getTarGrpCondition(conditionId);
        } catch (Exception e) {
            maps.put("resultCode", CommonConstant.CODE_FAIL);
            maps.put("resultMsg", ErrorCode.EDIT_TAR_GRP_CONDITION_FAILURE.getErrorMsg());
            maps.put("resultObject", StringUtils.EMPTY);
            logger.error("[op:TarGrpServiceImpl] fail to editTarGrpConditionDO ", e);
            return maps;
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("resultObject", tarGrpConditionDO);
        return maps;
    }

    /**
     * 更新目标分群条件
     */
    @Override
    public Map<String, Object> updateTarGrpConditionDO(TarGrpConditionDO tarGrpConditionDO) {
        Map<String, Object> mapsT = new HashMap<>();
        try {
            tarGrpConditionMapper.updateByPrimaryKey(tarGrpConditionDO);
        } catch (Exception e) {
            mapsT.put("resultCode", CommonConstant.CODE_FAIL);
            mapsT.put("resultMsg", ErrorCode.UPDATE_TAR_GRP_CONDITION_FAILURE.getErrorMsg());
            mapsT.put("resultObject", StringUtils.EMPTY);
            logger.error("[op:TarGrpServiceImpl] fail to updateTarGrpConditionDO ", e);
            return mapsT;
        }
        mapsT.put("resultCode", CommonConstant.CODE_SUCCESS);
        mapsT.put("resultMsg", StringUtils.EMPTY);
        mapsT.put("resultObject", StringUtils.EMPTY);
        return mapsT;
    }

    /**
     * 新增客户分群
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> saveBigDataModel(Long mktCamGrpRulId) {
        Map<String, Object> maps = new HashMap<>();

        return maps;
    }



}
