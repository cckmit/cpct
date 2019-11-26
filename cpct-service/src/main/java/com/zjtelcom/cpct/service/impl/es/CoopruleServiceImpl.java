package com.zjtelcom.cpct.service.impl.es;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ctzj.shared.biz.isale.inbound.model.InboundResponseResult;
import com.ctzj.shared.biz.isale.inbound.model.TradeOrderBusinessAcceptanceDto;
import com.ctzj.shared.biz.isale.inbound.service.BizBusTradeOrderInboundService;
import com.zjtelcom.cpct.elastic.util.ElasticsearchUtil;
import com.zjtelcom.cpct.service.es.CoopruleService;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.EsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class CoopruleServiceImpl implements CoopruleService {


    Logger logger = LoggerFactory.getLogger(CoopruleService.class);

    @Autowired(required = false)
    private BizBusTradeOrderInboundService bizBusTradeOrderInboundService;

    private static ExecutorService threadPool =  null;
    static {
        //初始化全局线程池,用来多线程并发处理isale预校验接口
        if (threadPool == null) {
            threadPool = Executors.newCachedThreadPool();
        }
    }

    private void testAddLog(String batchNum,String data,String info,String remark,boolean addLog) {
        if (addLog){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("batchNum", batchNum);
            jsonObject.put("data",data);
            jsonObject.put("time", DateUtil.formatMilliesDate(new Date()));
            jsonObject.put("info",info);
            jsonObject.put("remark",remark);
            String id = ElasticsearchUtil.addData(jsonObject, "triallog_test", "doc", jsonObject.getString("id"));
        }
    }

    /**
     * 对推送列表的销售品做预校验，预校验不通过不推送给渠道方
     * @return void
     * @throws
     */
    @Override
    public void validateProduct(List<Map<String, Object>> taskList,String campaignType,String integrationId,String loginId,String lantId) {
        StringBuffer sbff = new StringBuffer("");
        //预校验的销售品集合
        List<Map<String, String>> validateProductList = new ArrayList<>();
        int index = 0;
        //todo 开关
        if (true) {
            logger.info("isaleValidateFlag:=============" + true);
            Iterator<Map<String, Object>> taskIte = taskList.iterator();
            while (taskIte.hasNext()) {
                Map<String, Object> task = taskIte.next();
            //    String activityType = Optional.ofNullable(task.get("activityType")).orElse("").toString();
            //    String skipCheck = Optional.ofNullable(task.get("skipCheck")).orElse("").toString();
                //活动类型为"营销活动"并且活动没有配置跳过预校验才做预校验操作
                if (true) {
                    //推送销售品列表
                    List<Map<String, String>> productList = (List<Map<String, String>>) task.get("productList");
                    logger.info("productList=====================" + JSON.toJSONString(productList));
                    boolean flag = false;
                    if (productList != null && productList.size() != 0) {
                        logger.info("flag=====================" + flag);
                        //销售品中没有主套餐的话需要做isale预校验
                        for (Map<String, String> product : productList) {
                            index++;
                            //增加销售品条目的唯一标签用来区分多线程并发校验销售品的时候哪些销售品是被过滤掉了
                            product.put("productKey", "productKey" + index);
                            Map<String, String> validateProduct = new HashMap<String, String>();
                            validateProduct.putAll(product);
                            //isale预校验的时候需要集成编码参数
                            validateProduct.put("integrationId", integrationId);
                            validateProductList.add(validateProduct);
                        }
                    }
                    logger.info("validateProductList=====================" + JSON.toJSONString(validateProductList));
                }
            }
        }
        if (validateProductList.size() != 0) {
            int count = validateProductList.size();
            //创建计数器
            //构造参数传入的数量值代表的是latch.countDown()调用的次数
            CountDownLatch latch = new CountDownLatch(count);
            Iterator<Map<String, String>> prodIte = validateProductList.iterator();
            while (prodIte.hasNext()) {
                Map<String, String> validateProduct = prodIte.next();
                TradeOrderBusinessAcceptanceDto tradeOrderBusinessAcceptanceDto = new TradeOrderBusinessAcceptanceDto();
                tradeOrderBusinessAcceptanceDto.setBizType("业务变更");
                tradeOrderBusinessAcceptanceDto.setC3Name(EsUtil.getC3Name(lantId));
                tradeOrderBusinessAcceptanceDto.setLoginId(loginId);
                tradeOrderBusinessAcceptanceDto.setOrderSource("内容策略中心");//订单来源
                TradeOrderBusinessAcceptanceDto.TradeOrderBAProd prod = new TradeOrderBusinessAcceptanceDto.TradeOrderBAProd();
                prod.setAssetIntegId(validateProduct.get("integrationId"));//资产集成编码
//                prod.setAssetIntegId("3-1AYT3V55");//资产集成编码
                tradeOrderBusinessAcceptanceDto.setTradeOrdNum("CPCP"+DateUtil.formatDates(new Date())+ EsUtil.getRandomStr(4));//校验单流水号
                TradeOrderBusinessAcceptanceDto.TradeOrderBAProm tradeProm = new TradeOrderBusinessAcceptanceDto.TradeOrderBAProm();
                tradeProm.setPromNbr(validateProduct.get("productCode"));
                tradeProm.setActionCode("添加");
                prod.addTradeProm(tradeProm);
                TradeOrderBusinessAcceptanceDto.TradeOrderBAItem tradeItem = new TradeOrderBusinessAcceptanceDto.TradeOrderBAItem();
                tradeItem.addTradeProd(prod);
                tradeOrderBusinessAcceptanceDto.addTradItem(tradeItem);
                threadPool.execute(() -> {
                    try {
                        InboundResponseResult<Map<String, Object>> boundResult = verifyItvOrderNew( tradeOrderBusinessAcceptanceDto);
                        if (boundResult!=null && boundResult.isSuccess()!=null && boundResult.isSuccess()) {
                            //校验成功对该销售品打上成功标签
                            validateProduct.put("validateSuccess", "1");
                        } else {

                            //校验失败加上失败标签
                            validateProduct.put("validateSuccess", "0");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //当前计算工作已结束，计数器减一
                        latch.countDown();
                    }
                });

            }
            //阻止当前线程往下执行，知道所有销售品都预校验完成
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //移除接触任务列表中校验不通过的销售品
        removeProductList(taskList, validateProductList, sbff);
        //处理接触任务列表,针对营销活动如果推送渠道的销售品列表为空则该推送渠道删除
        handlerTaskList(taskList,campaignType);
    }

    /**
     * 如果推送任务下的推送渠道列表为空，则改任务也删除
     * @param taskList 推送任务列表
     * @return void
     * @throws
     */
    private void handlerTaskList(List<Map<String, Object>> taskList, String campaignType) {
        String activityType = Optional.ofNullable(campaignType).orElse("").toString();
        if ("0".equals(activityType)) {
            //针对营销活动如果推送渠道的销售品列表为空，则该推送渠道也删除
            taskList.removeIf((taskChl) -> {
                List<Map<String, String>> productList = (List<Map<String, String>>) taskChl.get("productList");
                if (productList == null || productList.size() == 0) {
                    return true;
                }
                return false;
            });
        }
    }

    /**
     * 新的isale可选包校验
     * @param
     * @return
     */
    @SuppressWarnings("static-access")
    private InboundResponseResult<Map<String, Object>> verifyItvOrderNew( TradeOrderBusinessAcceptanceDto tradeOrderBusinessAcceptanceDto) {
        logger.info("tradeOrderBusinessAcceptanceValidate start...");
        Object[] isaleParams = {tradeOrderBusinessAcceptanceDto};
        logger.info("isale预校验入参:" + JSON.toJSONString(isaleParams));
        InboundResponseResult<Map<String, Object>> boundResult = null;
        try {
            boundResult =  bizBusTradeOrderInboundService.beforehandValidate(tradeOrderBusinessAcceptanceDto);
        } catch (Exception e) {
            logger.error("调用预校验接口失败", e);
            e.printStackTrace();
        }
        logger.info("isale预校验返回参数：" + JSON.toJSONString(boundResult));
        return boundResult;
    }

    private void removeProductList(List<Map<String, Object>> taskList,
                                   List<Map<String, String>> validateProductList, StringBuffer sbff) {
        //获取所有校验不通过的销售品的key集合
//        logger.info("taskList===================" + FormatUtil.formatJson(JSON.toJSONString(taskList)));
        List<String> failProductKeys = validateProductList.stream()
                .filter((product) -> "0".equals(product.get("validateSuccess")))
                .map((product) -> product.get("productKey")).collect(Collectors.toList());
        logger.info("failProductKeys==========" + failProductKeys);

            taskList.forEach((taskChl) -> {
                List<Map<String, String>> productList = (List<Map<String, String>>) taskChl.get("productList");
                String contactAccount = Optional.ofNullable(taskChl.get("contactAccount")).orElse("").toString();
                Iterator<Map<String, String>> prodIte = productList.iterator();
                while (prodIte.hasNext()) {
                    Map<String, String> product = prodIte.next();
                    String productKey = Optional.ofNullable(product.get("productKey")).orElse("");
                    if (failProductKeys.contains(productKey)) {
                        //将isale预校验被过滤的销售品移除掉
                        if (sbff.indexOf(contactAccount) == -1) {
                            sbff.append("接触账号为："+contactAccount+"被过滤掉的销售品编码:").append(product.get("productCode")).append(",");
                        }
                        logger.info("被移除的销售品key："+productKey);
                        prodIte.remove();
                    }
                }
            });
    }


}
