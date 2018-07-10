package com.zjtelcom.cpct.service.impl.channel;

import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.MktVerbalConditionMapper;
import com.zjtelcom.cpct.dao.channel.MktVerbalMapper;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.MktVerbal;
import com.zjtelcom.cpct.domain.channel.MktVerbalCondition;

import com.zjtelcom.cpct.dto.channel.*;
import com.zjtelcom.cpct.enums.ConditionType;
import com.zjtelcom.cpct.enums.Operator;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.VerbalService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
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



    /**
     * 添加痛痒点话术
     */
    @Override
    @Transactional
    public Map<String,Object> addVerbal(Long userId, VerbalAddVO addVO) {
        Map<String,Object> result = new HashMap<>();

        MktVerbal verbal = BeanUtil.create(addVO,new MktVerbal());
        //todo 活动id 通过配置获取 或直接删除
        verbal.setCampaignId(1000L);
        verbal.setCreateDate(new Date());
        verbal.setCreateStaff(userId);
        verbal.setStatusCd("1000");
        verbalMapper.insert(verbal);
        //删除旧的条件
//        List<MktVerbalCondition> historyList = verbalConditionMapper.findConditionListByVerbalId(verbal.getVerbalId());
//        for (MktVerbalCondition condition : historyList){
//            verbalConditionMapper.deleteByPrimaryKey(condition.getConditionId());
//        }
        for (VerbalConditionAddVO  vcAddVO : addVO.getAddVOList()){
            //类型为标签时
                MktVerbalCondition mktVerbalCondition = BeanUtil.create(vcAddVO,new MktVerbalCondition());
                mktVerbalCondition.setVerbalId(verbal.getVerbalId());
                //标签类型
                if (vcAddVO.getLeftParamType().equals("1000")){
                    mktVerbalCondition.setRightParamType("3000"); //固定值
                }else {
                    mktVerbalCondition.setRightParamType("2000");
                }
                mktVerbalCondition.setConditionType(ConditionType.CHANNEL.getValue().toString());
                verbalConditionMapper.insert(mktVerbalCondition);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");
        return result;
    }

    @Override
    public Map<String,Object> editVerbal(Long userId, VerbalEditVO editVO) {
        Map<String,Object> result = new HashMap<>();
        MktVerbal verbal = verbalMapper.selectByPrimaryKey(editVO.getVerbalId());
        if (verbal==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","痛痒点话术不存在");
            return result;
        }
        Long confId = verbal.getContactConfId();
        List<MktVerbalCondition> conditions  = verbalConditionMapper.findChannelConditionListByVerbalId(verbal.getVerbalId());
        for (MktVerbalCondition condition : conditions){
            if (condition==null){
                continue;
            }
            verbalConditionMapper.deleteByPrimaryKey(condition.getConditionId());
        }
        verbalMapper.deleteByPrimaryKey(verbal.getVerbalId());
        VerbalAddVO addVO = BeanUtil.create(editVO,new VerbalAddVO());
        addVO.setContactConfId(confId);
        try {
            addVerbal(userId,addVO);
        }catch (Exception e){
            logger.error("[op:VerbalServiceImpl] fail to editVerbal",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to editVerbal");
            return result;
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","编辑成功");
        return result;
    }

    @Override
    public Map<String,Object> getVerbalDetail(Long userId, Long verbalId) {
        Map<String,Object> result = new HashMap<>();
        MktVerbal verbal = verbalMapper.selectByPrimaryKey(verbalId);
        if (verbal==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","痛痒点话术不存在");
            return result;
        }
        VerbalVO verbalVO = supplementVo(ChannelUtil.map2VerbalVO(verbal),verbal);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",verbalVO);
        return result;
    }

    /**
     * 痛痒点话术返回结果包装
     */
    private VerbalVO supplementVo(VerbalVO verbalVO,MktVerbal verbal){
        List<VerbalConditionVO> conditionVOList = new ArrayList<>();
        List<MktVerbalCondition> conditions = verbalConditionMapper.findChannelConditionListByVerbalId(verbal.getVerbalId());
        for (MktVerbalCondition condition : conditions){
            VerbalConditionVO vo = BeanUtil.create(condition,new VerbalConditionVO());
            vo.setOperName(Operator.getOperator(Integer.valueOf(condition.getOperType())).getDescription());
            if (!condition.getLeftParamType().equals("2000")){
                Label label = labelMapper.selectByPrimaryKey(Long.valueOf(condition.getLeftParam()));
                vo.setConditionType(label.getConditionType());
                vo.setLeftParamName(label.getInjectionLabelName());
                if (label.getRightOperand()!=null){
                    vo.setValueList(ChannelUtil.StringToList(label.getRightOperand()));
                }
                if (label.getOperator()!=null){
                    List<String> opratorList = ChannelUtil.StringToList(label.getOperator());
                    List<OperatorDetail> opStList  = new ArrayList<>();
                    for (String operator : opratorList){
                        Operator op = Operator.getOperator(Integer.valueOf(operator));
                        OperatorDetail detail = new OperatorDetail();
                        if (op!=null){
                            detail.setOperValue(op.getValue());
                            detail.setOperName(op.getDescription());
                        }
                        opStList.add(detail);
                    }
                    vo.setOperatorList(opStList);
                }
            }
            conditionVOList.add(vo);
        }
        verbalVO.setConditionList(conditionVOList);
        return verbalVO;
    }

    @Override
    public Map<String,Object> getVerbalListByConfId(Long userId, Long confId) {
        Map<String,Object> result = new HashMap<>();
        //todo 推送渠道对象
        List<MktVerbal>  verbalList =  verbalMapper.findVerbalListByConfId(confId);
        List<VerbalVO> verbalVOS = new ArrayList<>();
        for (MktVerbal verbal : verbalList){
            if (verbal==null){
                result.put("resultCode",CODE_FAIL);

                result.put("resultMsg","痛痒点话术不存在");
                return result;
            }
            VerbalVO verbalVO = supplementVo(ChannelUtil.map2VerbalVO(verbal),verbal);
            verbalVOS.add(verbalVO);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",verbalVOS);
        return result;
    }


    @Override
    public Map<String, Object> delVerbal(Long userId, Long verbalId) {
        Map<String, Object> result = new HashMap<>();
        //todo 推送渠道对象
        MktVerbal verbal = verbalMapper.selectByPrimaryKey(verbalId);
        if (verbal==null) {

            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "痛痒点话术不存在");
            return result;
        }
        verbalMapper.deleteByPrimaryKey(verbalId);
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", "删除成功");
        return result;
    }
}
