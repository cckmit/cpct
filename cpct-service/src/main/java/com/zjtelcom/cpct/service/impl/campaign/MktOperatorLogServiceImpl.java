package com.zjtelcom.cpct.service.impl.campaign;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktOperatorLogMapper;
import com.zjtelcom.cpct.domain.campaign.MktOperatorLogDO;
import com.zjtelcom.cpct.service.campaign.MktOperatorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        if(params.containsKey("mktActivityNbr")) {
            String mktActivityNbr = params.get("mktActivityNbr");
            mktOperatorLogDO.setMktActivityNbr(mktActivityNbr);
        }
        if(params.containsKey("mktActivityNbr")) {
            String mktCampaignName = params.get("mktCampaignName");
            mktOperatorLogDO.setMktCampaignName(mktCampaignName);
        }
        if(params.containsKey("operatorAccount")) {
            String operatorAccount = params.get("operatorAccount");
            mktOperatorLogDO.setOperatorAccount(operatorAccount);
        }

        //分页
        PageHelper.startPage(page, pageSize);
        List<MktOperatorLogDO> operation = mktOperatorLogMapper.selectByPrimaryKey(mktOperatorLogDO);
        Page pageInfo = new Page(new PageInfo(operation));

        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("data",operation);
        result.put("pageInfo",pageInfo);

        return result;
    }
}
