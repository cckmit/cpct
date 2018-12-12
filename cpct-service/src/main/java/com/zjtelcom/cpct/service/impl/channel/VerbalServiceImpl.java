package com.zjtelcom.cpct.service.impl.channel;

import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.domain.campaign.MktCamChlConfAttrDO;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfAttr;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail;
import com.zjtelcom.cpct.dto.channel.*;
import com.zjtelcom.cpct.enums.ConditionType;
import com.zjtelcom.cpct.enums.Operator;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.VerbalService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class VerbalServiceImpl extends BaseService implements VerbalService {

    @Autowired
    private MktVerbalMapper verbalMapper;
    @Autowired
    private MktVerbalConditionMapper verbalConditionMapper;
    @Autowired
    private InjectionLabelMapper labelMapper;
    @Autowired
    private ContactChannelMapper channelMapper;
    @Autowired
    private InjectionLabelValueMapper labelValueMapper;
    @Autowired
    private RedisUtils redisUtils;

    /**
     * 复制痛痒点Redis获取数据
     * @param contactConfId
     * @return
     */
    @Override
    public Map<String, Object> copyVerbalFromRedis(Long contactConfId, Long newConfId) {
        return null;
    }

    /**
     * 复制痛痒点
     * @param contactConfId
     * @return
     */
    @Override
    public Map<String, Object> copyVerbal(Long contactConfId,Long newConfId) {
        Map<String,Object> map = new HashMap<>();
        MktCamChlConfDetail detail = (MktCamChlConfDetail) redisUtils.get("MktCamChlConfDetail_"+contactConfId);
        List<VerbalVO> verbalVOList = new ArrayList<>();
        if (detail==null){
            verbalVOList = ( List<VerbalVO>)getVerbalListByConfId(1L,contactConfId).get("resultMsg");
        }else {
            verbalVOList = detail.getVerbalVOList();
        }
        if(verbalVOList!=null &&verbalVOList.size()>0){
            for (VerbalVO verbalVO : verbalVOList){
                VerbalAddVO addVO = BeanUtil.create(verbalVO,new VerbalAddVO());
                addVO.setContactConfId(newConfId);
                List<VerbalConditionAddVO> conditionAddVOList = new ArrayList<>();
                for (VerbalConditionVO conditionVO : verbalVO.getConditionList()){
                    VerbalConditionAddVO conditionAddVO = BeanUtil.create(conditionVO,new VerbalConditionAddVO());
                    conditionAddVOList.add(conditionAddVO);
                }
                addVO.setAddVOList(conditionAddVOList);
                Map<String, Object> addMap = addVerbal(1L,addVO);
                if (!addMap.get("resultCode").equals(CODE_SUCCESS)){
                    return addMap;
                }
            }
        }
        map.put("resultCode", CODE_SUCCESS);
        map.put("resultMsg", "添加成功");
        return map;
    }

    /**
     * 添加痛痒点话术
     */
    @Override
    @Transactional
    public Map<String, Object> addVerbal(Long userId, VerbalAddVO addVO) {
        Map<String, Object> result = new HashMap<>();

        MktVerbal verbal = BeanUtil.create(addVO, new MktVerbal());
        //todo 活动id 通过配置获取 或直接删除
        verbal.setCampaignId(1000L);
        verbal.setCreateDate(new Date());
        verbal.setCreateStaff(userId);
        verbal.setStatusCd("1000");
        verbalMapper.insert(verbal);
        //删除旧的条件
        List<MktVerbalCondition> conditions = new ArrayList<>();
        for (VerbalConditionAddVO vcAddVO : addVO.getAddVOList()) {
            if (vcAddVO.getOperType()==null){

            }
            //类型为标签时
            MktVerbalCondition mktVerbalCondition = BeanUtil.create(vcAddVO, new MktVerbalCondition());
            mktVerbalCondition.setVerbalId(verbal.getVerbalId());
            //标签类型
            if (vcAddVO.getLeftParamType().equals("1000")) {
                mktVerbalCondition.setRightParamType("3000"); //固定值
            } else {
                mktVerbalCondition.setRightParamType("2000");
            }
            mktVerbalCondition.setConditionType(ConditionType.CHANNEL.getValue().toString());
            conditions.add(mktVerbalCondition);
        }
        if (conditions.size()>0){
            verbalConditionMapper.insertByBatch(conditions);
        }

        //更新redis分群数据,先查出来再更新
        MktCamChlConfDetail detail = (MktCamChlConfDetail)redisUtils.get("MktCamChlConfDetail_"+addVO.getContactConfId());
        if (detail!=null){
            VerbalVO verbalVO = BeanUtil.create(verbal,new VerbalVO());
            List<VerbalConditionVO> conditionVOList = new ArrayList<>();
            for (MktVerbalCondition condition : conditions){
                VerbalConditionVO vo = BeanUtil.create(condition,new VerbalConditionVO());
                conditionVOList.add(vo);
            }
            verbalVO.setConditionList(conditionVOList);
            List<VerbalVO> voList = new ArrayList<>();
            voList.add(verbalVO);
            detail.setVerbalVOList(voList);
            redisUtils.set("MktCamChlConfDetail_"+addVO.getContactConfId(),detail);
        }
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", "添加成功");
        return result;
    }


    @Override
    public Map<String, Object> editVerbal(Long userId, VerbalEditVO editVO) {
        Map<String, Object> result = new HashMap<>();
        MktVerbal verbal = verbalMapper.selectByPrimaryKey(editVO.getVerbalId());
        if (verbal == null) {
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "痛痒点话术不存在");
            return result;
        }
        Long confId = verbal.getContactConfId();
        List<MktVerbalCondition> conditions = verbalConditionMapper.findChannelConditionListByVerbalId(verbal.getVerbalId());
        for (MktVerbalCondition condition : conditions) {
            if (condition == null) {
                continue;
            }
            verbalConditionMapper.deleteByPrimaryKey(condition.getConditionId());
        }
        verbalMapper.deleteByPrimaryKey(verbal.getVerbalId());
        VerbalAddVO addVO = BeanUtil.create(editVO, new VerbalAddVO());
        addVO.setContactConfId(confId);
        try {
            addVerbal(userId, addVO);
        } catch (Exception e) {
            logger.error("[op:VerbalServiceImpl] fail to editVerbal", e);
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", " fail to editVerbal");
            return result;
        }
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", "编辑成功");
        return result;
    }

    @Override
    public Map<String, Object> getVerbalDetail(Long userId, Long verbalId) {
        Map<String, Object> result = new HashMap<>();
        MktVerbal verbal = verbalMapper.selectByPrimaryKey(verbalId);
        if (verbal == null) {
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "痛痒点话术不存在");
            return result;
        }
        VerbalVO verbalVO = supplementVo(ChannelUtil.map2VerbalVO(verbal), verbal);
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", verbalVO);
        return result;
    }

    /**
     * 痛痒点话术返回结果包装
     */
    private VerbalVO supplementVo(VerbalVO verbalVO, MktVerbal verbal) {
        List<VerbalConditionVO> conditionVOList = new ArrayList<>();
        List<MktVerbalCondition> conditions = verbalConditionMapper.findChannelConditionListByVerbalId(verbal.getVerbalId());
        for (MktVerbalCondition condition : conditions) {
            VerbalConditionVO vo = BeanUtil.create(condition, new VerbalConditionVO());
            vo.setOperName(Operator.getOperator(Integer.valueOf(condition.getOperType())).getDescription());
            if (condition.getLeftParam()!=null && !condition.getLeftParamType().equals("2000")) {
                Label label = labelMapper.selectByPrimaryKey(Long.valueOf(condition.getLeftParam()));
                if (label!=null){
                    if (label.getConditionType()!=null && !label.getConditionType().equals("")){
                        vo.setConditionType(label.getConditionType());
                    }
                    vo.setLeftParamName(label.getInjectionLabelName());
                    List<LabelValue> valueList = labelValueMapper.selectByLabelId(label.getInjectionLabelId());
                    if (!valueList.isEmpty()) {
                        vo.setValueList(ChannelUtil.valueList2VOList(valueList));
                    }
                    setOperator(vo, label);
                }
            }
            conditionVOList.add(vo);
        }
        verbalVO.setConditionList(conditionVOList);
        Channel channel = channelMapper.selectByPrimaryKey(verbalVO.getChannelId());
        if (channel!=null){
            verbalVO.setChannelName(channel.getContactChlName());
            verbalVO.setChannelParentId(channel.getParentId());
            Channel parent = channelMapper.selectByPrimaryKey(channel.getParentId());
            if (parent!=null){
                verbalVO.setChannelParentName(parent.getContactChlName());
            }
        }
        return verbalVO;
    }

    private void setOperator(VerbalConditionVO vo, Label label) {
        if (label.getOperator() != null && !label.getOperator().equals("")) {
            List<String> opratorList = ChannelUtil.StringToList(label.getOperator());
            List<OperatorDetail> opStList = new ArrayList<>();
            for (String operator : opratorList) {
                Operator op = Operator.getOperator(Integer.valueOf(operator));
                OperatorDetail detail = new OperatorDetail();
                if (op != null) {
                    detail.setOperValue(op.getValue());
                    detail.setOperName(op.getDescription());
                }
                opStList.add(detail);
            }
            vo.setOperatorList(opStList);
        }
    }

    @Override
    public Map<String, Object> getVerbalListByConfId(Long userId, Long confId) {
        Map<String, Object> result = new HashMap<>();
        //todo 推送渠道对象
        List<MktVerbal> verbalList = verbalMapper.findVerbalListByConfId(confId);
        List<VerbalVO> verbalVOS = new ArrayList<>();
        for (MktVerbal verbal : verbalList) {
            if (verbal == null) {
                result.put("resultCode", CODE_FAIL);
                result.put("resultMsg", "痛痒点话术不存在");
                return result;
            }
            VerbalVO verbalVO = supplementVo(ChannelUtil.map2VerbalVO(verbal), verbal);
            verbalVOS.add(verbalVO);
        }
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", verbalVOS);
        return result;
    }


    @Override
    public Map<String, Object> delVerbal(Long userId, Long verbalId) {
        Map<String, Object> result = new HashMap<>();
        //todo 推送渠道对象
        MktVerbal mktVerbal = verbalMapper.selectByPrimaryKey(verbalId);
        if (mktVerbal == null) {
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "痛痒点话术不存在");
            return result;
        }
        Long confId = mktVerbal.getContactConfId();
        verbalMapper.deleteByPrimaryKey(verbalId);
        List<MktVerbal> verbalList = verbalMapper.findVerbalListByConfId(confId);
        List<VerbalVO> voList = new ArrayList<>();
        MktCamChlConfDetail detail = (MktCamChlConfDetail) redisUtils.get("MktCamChlConfDetail_"+mktVerbal.getContactConfId());
        for (MktVerbal verbal : verbalList){
            List<MktVerbalCondition> conditions = verbalConditionMapper.findChannelConditionListByVerbalId(verbal.getVerbalId());
            VerbalVO verbalVO = BeanUtil.create(verbal,new VerbalVO());
            List<VerbalConditionVO> conditionVOList = new ArrayList<>();
            for (MktVerbalCondition condition : conditions){
                VerbalConditionVO vo = BeanUtil.create(condition,new VerbalConditionVO());
                conditionVOList.add(vo);
            }
            verbalVO.setConditionList(conditionVOList);
            voList.add(verbalVO);
        }
        detail.setVerbalVOList(voList);
        redisUtils.set("MktCamChlConfDetail_"+mktVerbal.getContactConfId(),detail);
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", "删除成功");
        return result;
    }
}
