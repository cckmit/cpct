package com.zjtelcom.cpct.service.impl.campaign;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktOperatorLogMapper;
import com.zjtelcom.cpct.domain.campaign.MktOperatorLogDO;
import com.zjtelcom.cpct.service.campaign.MktOperatorLogService;
import com.zjtelcom.cpct.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class MktOperatorLogServiceImpl implements MktOperatorLogService{
    @Autowired
    MktOperatorLogMapper mktOperatorLogMapper;

    @Override
    public Map<String, Object> selectByPrimaryKey(Map<String,String> params) {
        Map<String,Object> result = new HashMap<>();

        MktOperatorLogDO mktOperatorLogDO = new MktOperatorLogDO();
        //获取分页参数
        Integer page = Integer.parseInt(params.get("page"));
        Integer pageSize = Integer.parseInt(params.get("pageSize"));
        Date startTime = null;
        Date endTime = null;
        if(params.get("startTime") != null) {
            try{
                startTime = DateUtil.string2DateTime4Day(params.get("startTime"));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(params.get("endTime") != null) {
            try{
                endTime = DateUtil.string2DateTime4Day(params.get("endTime"));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(params.containsKey("mktActivityNbr")) {
            String mktActivityNbr = params.get("mktActivityNbr");
            mktOperatorLogDO.setMktActivityNbr(mktActivityNbr);
        }
        if(params.containsKey("mktCampaignName")) {
            String mktCampaignName = params.get("mktCampaignName");
            mktOperatorLogDO.setMktCampaignName(mktCampaignName);
        }
        if(params.containsKey("operatorAccount")) {
            String operatorAccount = params.get("operatorAccount");
            mktOperatorLogDO.setOperatorAccount(operatorAccount);
        }
        if(params.containsKey("type")) {
            String operatorType = params.get("type");
            mktOperatorLogDO.setOperatorType(operatorType);
        }

        //分页
        PageHelper.startPage(page, pageSize);
        List<MktOperatorLogDO> operation = mktOperatorLogMapper.selectByPrimaryKey(mktOperatorLogDO);
        List<MktOperatorLogDO> operations =new ArrayList<>();
        for(int i=0;i<operation.size();i++){
            Date time = operation.get(i).getOperatorDate();
            if(startTime==null || endTime==null) {
                operations.add(operation.get(i));
            } else if((time.after(startTime) && time.before(endTime)) || time.getTime() == startTime.getTime() || time.getTime() == endTime.getTime()) {
                operations.add(operation.get(i));
            }
        }
        Page pageInfo = new Page(new PageInfo(operation));

        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("data",operations);
        result.put("pageInfo",pageInfo);

        return result;
    }
}
