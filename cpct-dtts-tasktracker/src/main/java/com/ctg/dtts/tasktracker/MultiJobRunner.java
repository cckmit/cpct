package com.ctg.dtts.tasktracker;

import com.ctg.dtts.core.domain.Action;
import com.ctg.dtts.core.domain.Job;
import com.ctg.dtts.core.logger.Logger;
import com.ctg.dtts.core.logger.LoggerFactory;
import com.ctg.dtts.tasktracker.logger.BizLogger;
import com.ctg.dtts.tasktracker.runner.DttsLoggerFactory;
import com.zjtelcom.cpct.service.campaign.MktCamCycleTimingService;
import com.zjtelcom.cpct.service.campaign.MktOperatorLogService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class MultiJobRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiJobRunner.class);

    @Autowired TestSpringBean springBean;

    @Autowired
    private MktCamCycleTimingService mktCamCycleTimingService;
    @Autowired
    private MktOperatorLogService mktOperatorLogService;

//    public Result runJob1(Job job) throws Throwable {   //带Job job，返回Result，适用于需要参数、返回结果的任务
//        try {
//            if (job.getExtParams().containsKey("sleep")) {
//                int sleepTime = Integer.parseInt(job.getExtParams().get("sleep"));
//                Thread.sleep(sleepTime);
//            } else {
//                Thread.sleep(1000L);
//            }
//
//            springBean.hello();
//            String msg = "runJob1执行任务taskid：" + job.getTaskId() + "，params：" + job.getExtParams();
//            LOGGER.info(msg);
//            BizLogger bizLogger = DttsLoggerFactory.getBizLogger();
//            //记录过程日志，可通过日志查看（不要过大、过多）
//            bizLogger.info(msg);
//
//        } catch (Exception e) {
//            LOGGER.info("Run job1 failed!", e);
//            return new Result(Action.EXECUTE_LATER, e.getMessage());
//        }
//        return new Result(Action.EXECUTE_SUCCESS, "执行成功了");
//    }

    public void runCampaignCycleMonth() throws Throwable { //不带Job job参数、没有返回，适用于无参数无返回的任务
        try {
            int result = mktOperatorLogService.addMktOperatorLog("测试",1L,"123456","test1","test2",2L,"test3");
//            mktCamCycleTimingService.findCampaignCycleMonth();
            for(int i = 0; i <10 ; i++) {
                System.out.println("*******结果:" + result + "*********");
            }
            springBean.hello();
            String msg = "runCampaignCycleMonth执行任务";
            LOGGER.info(msg);
            BizLogger bizLogger = DttsLoggerFactory.getBizLogger();
            bizLogger.info(msg);
        } catch (Exception e) {
            LOGGER.info("Run job2 failed!", e);
            e.printStackTrace();
        }
    }

    public void runCampaignCycleWeek() throws Throwable { //不带Job job参数、没有返回，适用于无参数无返回的任务
        try {
            mktCamCycleTimingService.findCampaignCycleWeek();
            springBean.hello();
            String msg = "runCampaignCycleWeek执行任务";
            LOGGER.info(msg);
            BizLogger bizLogger = DttsLoggerFactory.getBizLogger();
            bizLogger.info(msg);
        } catch (Exception e) {
            LOGGER.info("Run job2 failed!", e);
        }
    }

//    public Result runJob3() throws Throwable {  //不带Job job参数、有返回，适用于无参数、需要返回结果的任务
//        try {
//            springBean.hello();
//            String msg = "runJob3执行任务";
//            LOGGER.info(msg);
//            BizLogger bizLogger = DttsLoggerFactory.getBizLogger();
//            bizLogger.info(msg);
//            for (int i=0; i<100000; i++) {
//                for (int j=0; j<100000; j++) {
//                    int k = i - j;
//                }
//            }
//        } catch (Exception e) {
//            LOGGER.info("Run job3 failed!", e);
//            return new Result(Action.EXECUTE_LATER, e.getMessage());
//        }
//        LOGGER.info("执行成功了");
//        return new Result(Action.EXECUTE_SUCCESS, "执行成功了");
//    }

//    public Result runJob4(Job job) throws Throwable {   //带Job job，返回Result，适用于需要参数、返回结果的任务
//        try {
//            List<String> childPaths = null;
//            if (job.getExtParams().containsKey("serviceIp")) {
//                String ip = job.getExtParams().get("serviceIp");
//                String nameSpace = "/";
//                if (job.getExtParams().containsKey("nameSpace")) {
//                    nameSpace = job.getExtParams().get("nameSpace");
//                }
//                CuratorFramework curatorFramework = CuratorZkClient(ip, nameSpace);
//                if (job.getExtParams().containsKey("zkPath")){
//                    String zkPath = job.getExtParams().get("zkPath");
//                    childPaths = curatorFramework.getChildren().forPath(zkPath);
//                    LOGGER.info("runJob4 get childPaths:"  + childPaths);
//                }
//
//            } else {
//                Thread.sleep(1000L);
//            }
//
//            springBean.hello();
//            String msg = "runJob4执行任务taskid：" + job.getTaskId() + "，params：" + job.getExtParams()
//                    + "返回zk指定路径下的目录为："+ childPaths;
//            LOGGER.info(msg);
//            BizLogger bizLogger = DttsLoggerFactory.getBizLogger();
//            //记录过程日志，可通过日志查看（不要过大、过多）
//            bizLogger.info(msg);
//
//        } catch (Exception e) {
//            LOGGER.info("Run job4 failed!", e);
//            return new Result(Action.EXECUTE_LATER, e.getMessage());
//        }
//        return new Result(Action.EXECUTE_SUCCESS, "执行成功了");
//    }

//    private CuratorFramework  CuratorZkClient(String zkUrl, String nameSpace) {
//        CuratorFrameworkFactory.Builder build = CuratorFrameworkFactory.builder().connectString(zkUrl)
//                .namespace(nameSpace).retryPolicy(new RetryUntilElapsed(100, 10));
//        CuratorFramework curatorFramework = build.build();
//        curatorFramework.start();
//
//        LOGGER.info("zookeeper started: " + curatorFramework);
//        return curatorFramework;
//    }
}
