package com.zjtelcom.cpct.service.impl.campaign;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktAlgorithmsMapper;
import com.zjtelcom.cpct.domain.campaign.MktAlgorithms;
import com.zjtelcom.cpct.service.campaign.MktAlgorithmsService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.MapUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
@Transactional
public class MktAlgorithmsServiceImpl implements MktAlgorithmsService {

    @Autowired
    private MktAlgorithmsMapper mktAlgorithmsMapper;

    @Override
    public Map<String, Object> getMktAlgorithms(Long userId, Long algoId) {
        Map<String, Object> resultMap = new HashMap<>();
        MktAlgorithms mktAlgorithms = mktAlgorithmsMapper.selectByPrimaryKey(algoId);
        if(mktAlgorithms == null){
            resultMap.put("resultCode",CODE_FAIL);
            resultMap.put("resultMsg","算法定义不存在");
            return resultMap;
        }
        resultMap.put("resultCode",CODE_SUCCESS);
        resultMap.put("resultMsg",mktAlgorithms);
        return resultMap;
    }

    @Override
    public Map<String, Object> saveMktAlgorithms(Long userId, MktAlgorithms addVO) {
        Map<String, Object> resultMap = new HashMap<>();

        MktAlgorithms mktAlgorithms = BeanUtil.create(addVO, new MktAlgorithms());
        mktAlgorithms.setCreateDate(DateUtil.getCurrentTime());
        mktAlgorithms.setUpdateDate(DateUtil.getCurrentTime());
        mktAlgorithms.setStatusDate(DateUtil.getCurrentTime());
        mktAlgorithms.setUpdateStaff(userId);
        mktAlgorithms.setCreateStaff(userId);
        mktAlgorithms.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
        mktAlgorithmsMapper.saveMktAlgorithms(mktAlgorithms);

        resultMap.put("resultCode",CODE_SUCCESS);
        resultMap.put("resultMsg","添加成功");
        return resultMap;
    }

    @Override
    public Map<String, Object> updateMktAlgorithms(Long userId, MktAlgorithms editVO) {
        Map<String, Object> resultMap = new HashMap<>();
        MktAlgorithms mktAlgorithms = mktAlgorithmsMapper.selectByPrimaryKey(editVO.getAlgoId());
        if(mktAlgorithms == null){
            resultMap.put("resultCode",CODE_FAIL);
            resultMap.put("resultMsg","算法定义不存在");
            return resultMap;
        }
        BeanUtil.copy(editVO, mktAlgorithms);
        mktAlgorithms.setUpdateDate(DateUtil.getCurrentTime());
        mktAlgorithms.setUpdateStaff(userId);
        mktAlgorithmsMapper.updateMktAlgorithms(mktAlgorithms);

        resultMap.put("resultCode",CODE_SUCCESS);
        resultMap.put("resultMsg","修改成功");
        return resultMap;
    }

    @Override
    public Map<String, Object> deleteMktAlgorithms(Long userId, MktAlgorithms delVO){
        Map<String, Object> resultMap = new HashMap<>();
        MktAlgorithms mktAlgorithms = mktAlgorithmsMapper.selectByPrimaryKey(delVO.getAlgoId());
        if(mktAlgorithms == null){
            resultMap.put("resultCode",CODE_FAIL);
            resultMap.put("resultMsg","算法定义不存在");
            return resultMap;
        }
        mktAlgorithmsMapper.deleteByPrimaryKey(delVO.getAlgoId());
        resultMap.put("resultCode",CODE_SUCCESS);
        resultMap.put("resultMsg","删除成功");
        return resultMap;
    }

    @Override
    public Map<String, Object> listMktAlgorithms(Long userId, Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        MktAlgorithms algorithms = new MktAlgorithms();
        String algoName = MapUtil.getString(params.get("algoName"));;
        if(StringUtils.isNotBlank(algoName)){
            algorithms.setAlgoName(algoName);
        }
        Integer page = MapUtil.getIntNum(params.get("page"));
        Integer pageSize = MapUtil.getIntNum(params.get("pageSize"));
        PageHelper.startPage(page, pageSize);
        List<MktAlgorithms> mktAlgorithmsList = mktAlgorithmsMapper.selectByMktAlgorithms(algorithms);
        Page pageInfo = new Page(new PageInfo(mktAlgorithmsList));

        resultMap.put("resultCode", CODE_SUCCESS);
        resultMap.put("resultMsg", mktAlgorithmsList);
        resultMap.put("page",pageInfo);
        return resultMap;

    }
}
