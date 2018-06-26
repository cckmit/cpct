/**
 * @(#)MktStrategyConfServiceImpl.java, 2018/6/25.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.impl.strategy;

import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dto.campaign.MktStrategyConf;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description:
 * author: linchao
 * date: 2018/06/25 17:23
 * version: V1.0
 */
@Service
public class MktStrategyConfServiceImpl implements MktStrategyConfService {

    @Autowired
    private MktStrategyConfMapper mktStrategyConfMapper;

    @Override
    public Long saveMktStrategyConf(MktStrategyConf mktStrategyConf) {
        mktStrategyConfMapper.insert(mktStrategyConf);
        return null;
    }
}