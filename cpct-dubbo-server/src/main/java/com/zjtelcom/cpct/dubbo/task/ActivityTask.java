package com.zjtelcom.cpct.dubbo.task;

import com.zjtelcom.cpct.dao.campaign.MktCamStrategyConfRelMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamStrategyConfRelDO;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ActivityTask implements Callable<Map<String, Object>> {

    private String ISI;

    private Long activityId;

    @Autowired
    private MktCampaignMapper mktCampaignMapper; //活动基本信息

    @Autowired
    private MktCamStrategyConfRelMapper mktCamStrategyConfRelMapper; //活动策略关联

    public ActivityTask(String ISI,Long activityId) {
        this.ISI = ISI;
        this.activityId = activityId;

    }

    @Override
    public Map<String, Object> call() {

        Map<String, Object> order = new HashMap<>();

        //查询活动基本信息
        MktCampaignDO mktCampaign = mktCampaignMapper.selectByPrimaryKey(activityId);

        //返回参数中添加活动信息
        order.put("orderISI", ISI);
        order.put("activityId", mktCampaign.getMktCampaignId().toString());
        order.put("activityName", mktCampaign.getMktCampaignName());
        order.put("activityCode", mktCampaign.getMktActivityNbr());
        order.put("skipCheck", "0");  //todo 不明 案例上有 文档上没有
        order.put("orderPriority", "100");  //todo 不明 案例上有 文档上没有

        //根据活动id获取策略列表
        List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOs = mktCamStrategyConfRelMapper.selectByMktCampaignId(activityId);

        //初始化结果集
        List<Future<List<Map<String, Object>>>> threadList = new ArrayList<>();
        //初始化线程池
        ExecutorService executorService = Executors.newCachedThreadPool();

        //遍历策略列表
        for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOs) {

            //提交线程
            Future<List<Map<String, Object>>> f = executorService.submit(new StrategyTask(mktCamStrategyConfRelDO.getStrategyConfId()));
            //将线程处理结果添加到结果集
            threadList.add(f);
        }

        //获取结果
        try{
            for(Future<List<Map<String, Object>>> future : threadList) {
                order.put("recommendList", future.get());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            //发生异常关闭线程池
            executorService.shutdown();
        } catch (ExecutionException e) {
            e.printStackTrace();
            //发生异常关闭线程池
            executorService.shutdown();
            return null;
        }

        //关闭线程池
        executorService.shutdown();

        return order;
    }
}
