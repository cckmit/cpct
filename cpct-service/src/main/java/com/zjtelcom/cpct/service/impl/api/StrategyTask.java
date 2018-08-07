package com.zjtelcom.cpct.service.impl.api;

import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class StrategyTask implements Callable<List<Map<String, Object>>> {

    @Autowired
    private MktStrategyConfMapper mktStrategyConfMapper; //策略基本信息

    @Autowired
    private MktStrategyConfRuleMapper mktStrategyConfRuleMapper;//策略规则


    private Long strategyConfId; //策略配置id

    public StrategyTask(Long strategyConfId) {
        this.strategyConfId = strategyConfId;
    }

    @Override
    public List<Map<String, Object>> call() {

        //获取当前时间
        Date now = new Date();

        //初始化返回结果中的推荐信息列表
        List<Map<String, Object>> recommendList = new ArrayList<>();

        //查询策略基本信息
        MktStrategyConfDO mktStrategyConf = mktStrategyConfMapper.selectByPrimaryKey(strategyConfId);

        //验证策略生效时间
        if (!(now.after(mktStrategyConf.getBeginTime()) && now.before(mktStrategyConf.getEndTime()))) {
            //若当前时间在策略生效时间外
            return null;
        }

        //todo 下发地市

        //todo 判断下发渠道

        //根据策略id获取策略下规则列表
        List<MktStrategyConfRuleDO> mktStrategyConfRuleDOS = mktStrategyConfRuleMapper.selectByMktStrategyConfId(strategyConfId);

        //遍历规则↓↓↓↓↓↓↓↓↓↓

        //初始化结果集
        List<Future<List<Map<String, Object>>>> threadList = new ArrayList<>();
        //初始化线程池
        ExecutorService executorService = Executors.newCachedThreadPool();


        //遍历规则列表
        if (mktStrategyConfRuleDOS != null && mktStrategyConfRuleDOS.size() > 0) {
            for (MktStrategyConfRuleDO mktStrategyConfRuleDO : mktStrategyConfRuleDOS) {

                //获取分群id
                Long tarGrpId = mktStrategyConfRuleDO.getTarGrpId();
                //获取销售品
                String productStr = mktStrategyConfRuleDO.getProductId();
                //过滤规则id
                Long ruleConfId = mktStrategyConfRuleDO.getRuleConfId();
                //协同渠道配置id
                String evtContactConfIdStr = mktStrategyConfRuleDO.getEvtContactConfId();

                //提交线程
                Future<List<Map<String, Object>>> f = executorService.submit(new RuleTask(strategyConfId,tarGrpId,productStr,ruleConfId,evtContactConfIdStr));
                //将线程处理结果添加到结果集
                threadList.add(f);

            }
        }

        //获取结果
        try {
            for (Future<List<Map<String, Object>>> future : threadList) {
                recommendList.addAll(future.get());
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

        return recommendList;
    }

}
