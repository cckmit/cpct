package com.zjtelcom.cpct.service.impl.channel;

import com.zjtelcom.cpct.dao.campaign.MktCamChlConfAttrMapper;
import com.zjtelcom.cpct.service.channel.ServicePackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.enums.ConfAttrEnum.ISEE_CUSTOMER;
import static com.zjtelcom.cpct.enums.ConfAttrEnum.ISEE_LABEL_CUSTOMER;
import static com.zjtelcom.cpct.enums.ConfAttrEnum.SERVICE_PACK;

@Service
public class ServicePackServiceImpl implements ServicePackService {

    @Autowired
    private MktCamChlConfAttrMapper mktCamChlConfAttrMapper;


    public void getServicePack(Long campaignId) {





    }

}
