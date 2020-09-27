package com.zjtelcom.cpct.dubbo.service.impl;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.dubbo.service.MktCampaignSyncApiService;
import com.zjtelcom.cpct.service.campaign.MktCampaignService;
import com.zjtelcom.cpct_offer.dao.inst.RequestInstRelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@Service
@Transactional
public class MktCampaignSyncApiServiceImpl implements MktCampaignSyncApiService {

    private static final Logger logger = LoggerFactory.getLogger(MktCampaignApiServiceImpl.class);
    @Autowired(required = false)
    private RequestInstRelMapper requestInstRelMapper;

    @Autowired(required = false)
    private MktCampaignService mktCampaignService;



    /**
     * 发布并下发活动
     *
     * @param requestId
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> publishMktCampaign(final Long requestId) {
        Map<String, Object> result = new HashMap<>();
        List<RequestInstRel> mktCampaignRels = requestInstRelMapper.selectByRequestId(requestId, "mkt");
        List<Map<String, Object>> res = new ArrayList<>();
        try {
            //初始化结果集
            List<Future<Map<String, Object>>> threadList = new ArrayList<>();
            for (RequestInstRel rel : mktCampaignRels) {
                Future<Map<String, Object>> future = null;
                ExecutorService executorService = Executors.newCachedThreadPool();
                future = executorService.submit(new issureCampaignTask(rel.getRequestObjId()));
                threadList.add(future);
                Map<String, Object> retr = new HashMap<>();
                String source = (String) future.get().get("resultMsg");
                retr.put("resultCode", (String) future.get().get("resultCode"));
                retr.put("活动ID:" + rel.getRequestObjId().toString(), source);
                res.add(retr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("resultCode", "200");
        result.put("resultMsg", "下发成功");
        result.put("resultData", res);
        return result;
    }

    class issureCampaignTask implements Callable<Map<String, Object>> {
        private Long mktCampaignId;

        public issureCampaignTask(Long campaignId) {
            this.mktCampaignId = campaignId;
        }

        @Override
        public Map<String, Object> call() {
            Map<String, Object> mktCampaignMap = new HashMap<>();
            try {
                mktCampaignMap = mktCampaignService.publishMktCampaign(mktCampaignId);
                mktCampaignService.changeMktCampaignStatus(mktCampaignId, "2002");
            } catch (Exception e) {
                e.printStackTrace();
                mktCampaignMap.put("resultCode", CommonConstant.CODE_FAIL);
                mktCampaignMap.put("resultMsg", "发布活动失败！");
            }
            return mktCampaignMap;
        }
    }
}
