package com.zjtelcom.cpct.service.impl.channel;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.dao.channel.MktVerbalConditionMapper;
import com.zjtelcom.cpct.dao.channel.MktVerbalMapper;
import com.zjtelcom.cpct.domain.channel.MktVerbal;
import com.zjtelcom.cpct.domain.channel.MktVerbalCondition;
import com.zjtelcom.cpct.domain.channel.Script;
import com.zjtelcom.cpct.dto.channel.*;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.VerbalService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Condition;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class VerbalServiceImpl extends BaseService implements VerbalService {

    @Autowired
    private MktVerbalMapper verbalMapper;
    @Autowired
    private MktVerbalConditionMapper verbalConditionMapper;


    /**
     * 添加痛痒点话术
     */
    @Override
    @Transactional
    public RespInfo addVerbal(Long userId, VerbalAddVO addVO) {
        MktVerbal verbal = BeanUtil.create(addVO,new MktVerbal());
        verbal.setCreateDate(new Date());
        verbal.setCreateStaff(userId);
        verbal.setStatusCd("1000");
        verbalMapper.insert(verbal);
        //删除旧的条件
        List<MktVerbalCondition> historyList = verbalConditionMapper.findConditionListByVerbalId(verbal.getVerbalId());
        for (MktVerbalCondition condition : historyList){
            verbalConditionMapper.deleteByPrimaryKey(condition.getConditionId());
        }
        for (VerbalConditionAddVO  vcAddVO : addVO.getAddVOList()){
            addCondition(userId,verbal.getVerbalId(),vcAddVO);
        }
        return RespInfo.build(CODE_SUCCESS,"添加成功");
    }

    //弃用
    @Override
    public RespInfo editVerbal(Long userId, VerbalEditVO editVO) {
        return null;
    }

    @Override
    public RespInfo getVerbalDetail(Long userId, Long verbalId) {
        MktVerbal verbal = verbalMapper.selectByPrimaryKey(verbalId);
        if (verbal==null){
            return RespInfo.build(CODE_FAIL,"痛痒点话术不存在");
        }
        VerbalVO verbalVO = supplementVo(ChannelUtil.map2VerbalVO(verbal),verbal);
        return RespInfo.build(CODE_SUCCESS,verbalVO);
    }

    /**
     * 痛痒点话术返回结果包装
     */
    private VerbalVO supplementVo(VerbalVO verbalVO,MktVerbal verbal){
        List<VerbalConditionVO> conditionVOList = new ArrayList<>();
        List<MktVerbalCondition> conditions = verbalConditionMapper.findConditionListByVerbalId(verbal.getVerbalId());
        for (MktVerbalCondition condition : conditions){
            VerbalConditionVO vo = BeanUtil.create(condition,new VerbalConditionVO());
            conditionVOList.add(vo);
        }
        verbalVO.setConditionList(conditionVOList);
        return verbalVO;
    }

    @Override
    public RespInfo getVerbalListByConfId(Long userId, Long confId) {
        //todo 推送渠道对象
        List<Long>  verbalIdList = new ArrayList<>();
        List<VerbalVO> verbalVOS = new ArrayList<>();
        for (Long verbalId : verbalIdList){
            MktVerbal verbal = verbalMapper.selectByPrimaryKey(verbalId);
            if (verbal==null){
                return RespInfo.build(CODE_FAIL,"痛痒点话术不存在");
            }
            VerbalVO verbalVO = supplementVo(ChannelUtil.map2VerbalVO(verbal),verbal);
            verbalVOS.add(verbalVO);
        }
        return RespInfo.build(CODE_SUCCESS,verbalVOS);
    }


    //添加
    private MktVerbalCondition addCondition(Long userId,Long verbalId ,VerbalConditionAddVO addVO){
        MktVerbalCondition verbalCondition = BeanUtil.create(addVO,new MktVerbalCondition());
        verbalCondition.setStatusCd("1000");
        verbalCondition.setCreateDate(new Date());
        verbalCondition.setCreateStaff(userId);
        verbalCondition.setVerbalId(verbalId);
        verbalConditionMapper.insert(verbalCondition);
        return verbalCondition;
    }


}
