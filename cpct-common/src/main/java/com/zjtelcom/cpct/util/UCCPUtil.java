package com.zjtelcom.cpct.util;

import com.ztesoft.uccp.dubbo.interfaces.UCCPSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 调用UCCP短信dubbo接口，发送短信工具类
 */
@Component
public class UCCPUtil {

    /**
     * 查询消息网关发送结果
     * @param
     * @return
     */
    /*public void queryMessage() throws Exception{
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:dubbo-consumer.xml");
        context.start();
        long beginTime = System.currentTimeMillis();

        //通过spring获取实例
        UCCPSendService service = (UCCPSendService) context.getBean("UCCPSendService");

        Map param = new HashMap();
        //查询推送的号码
        param.put("acc_nbr", "18909120999");
        //本地网ID
        param.put("lan_id", "318");
        //每页多少条,为空或者不传默认10条
        param.put("pageSize", "5");
        //页数,为空或者不传默认第1页
        param.put("pageIndex", "1");
        //查询开始时间
        param.put("begin_date", "2015-01-23 14:30:20");
        //查询结束时间
        param.put("end_date", "2015-01-23 14:30:20");

        Map reqMap = service.queryMessage(param);
        System.out.println("接口返回结果:"+reqMap);
        System.out.println("-----------------------请求总耗时:"+(System.currentTimeMillis()-beginTime)+"-------------------");
        System.exit(0);
    }*/

    /**
     * 实时推送消息
     * @param
     * @return
     */
    public static void sendShortMessage(String targPhone, String sendContent, String lanId)throws Exception{
        HashMap params = new HashMap();
        //请求消息流水，格式：系统编码（6位）+yyyymmddhhmiss+10位序列号
        params.put("TransactionId","CPCPYX"+ DateUtil.date2St4Trial(new Date()) + getRandom(10));
        //UCCP分配的系统编码
        params.put("SystemCode","CPCPYX");
        //UCCP分配的帐号
        params.put("UserAcct","CPCPYX");
        //UCCP分配的认证密码
        params.put("Password","908234");
        //场景标识
        params.put("SceneId","7149");
        //请求的时间,请求发起的时间,必须为下边的格式
        params.put("RequestTime",DateUtil.date2StringDate(new Date()));
        //接收消息推送的手机号码
        params.put("AccNbr",targPhone);
        //消息内容
        params.put("OrderContent",sendContent);
        //本地网/辖区
        params.put("LanId",lanId);
        //定时发送的时间设置
        //params.put("SendDate","");
        //如果使用场景模板来发送短信,必须填值
        //params.put("ContentParam","");
        //外系统流水ID,查询发送结构用,可填
        //params.put("ExtOrderId", "");

        System.out.println("-----------------------请求开始-------------------");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:dubbo/dubbo-${spring.profiles.active}.xml");
        context.start();
        long beginTime = System.currentTimeMillis();
        //通过spring获取实例
        UCCPSendService service = (UCCPSendService) context.getBean("UCCPSendService");
        Map reqMap = service.sendShortMessage(params);
        System.out.println("接口返回结果:"+reqMap);
        System.out.println("-----------------------请求总耗时:"+(System.currentTimeMillis()-beginTime)+"-------------------");
        System.exit(0);
    }

    public static String getRandom(int length){
        String val = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            val += String.valueOf(random.nextInt(10));
        }
        return val;
    }
}
