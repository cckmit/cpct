package com.zjtelcom.cpct.service.impl.dubbo;

import com.ctzj.smt.bss.sysmgr.model.common.SysmgrResultObject;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemUserDto;
import com.ctzj.smt.bss.sysmgr.privilege.service.dubbo.api.ISystemUserDtoDubboService;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.service.dubbo.UCCPService;
import com.zjtelcom.cpct.util.DateUtil;
import com.ztesoft.uccp.dubbo.interfaces.UCCPSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UCCPServiceImpl implements UCCPService {

    @Autowired(required = false)
    private UCCPSendService uCCPSendService;
    @Autowired(required = false)
    private ISystemUserDtoDubboService iSystemUserDtoDubboService;

    @Value("${uccp.userAcct}")
    private String userAcct;
    @Value("${uccp.password}")
    private String password;
    @Value("${uccp.sceneId}")
    private String sceneId;

    // 通过活动获取创建人，给活动创建人发送通知短信
    @Override
    public void sendShortMessage4CampaignStaff(MktCampaignDO mktCampaignDO, String sendContent) {
        try {
            Long staff = mktCampaignDO.getCreateStaff();
            SysmgrResultObject<SystemUserDto> systemUserDtoSysmgrResultObject = iSystemUserDtoDubboService.qrySystemUserDto(staff, new ArrayList<Long>());
            if (systemUserDtoSysmgrResultObject != null && systemUserDtoSysmgrResultObject.getResultObject() != null) {
                String sysUserCode = systemUserDtoSysmgrResultObject.getResultObject().getSysUserCode();
                Long lanId = mktCampaignDO.getLanId();
                sendShortMessage(sysUserCode, sendContent, lanId.toString());
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String sendShortMessage(String targPhone, String sendContent, String lanId) throws Exception {
        HashMap params = new HashMap();
        //请求消息流水，格式：系统编码（6位）+yyyymmddhhmiss+10位序列号
        params.put("TransactionId", userAcct + DateUtil.date2St4Trial(new Date()) + getRandom(10));
        //UCCP分配的系统编码
        params.put("SystemCode", userAcct);
        //UCCP分配的帐号
        params.put("UserAcct", userAcct);
        //UCCP分配的认证密码
        params.put("Password", password);
        //场景标识
        params.put("SceneId", sceneId);
        //请求的时间,请求发起的时间,必须为下边的格式
        params.put("RequestTime",DateUtil.date2StringDate(new Date()));
        //接收消息推送的手机号码
        params.put("AccNbr",targPhone);
        //params.put("AccNbr","18957181789");
        //消息内容
        params.put("OrderContent",sendContent);
        //本地网/辖区
        params.put("LanId",lanId);
        //params.put("LanId", "571");
        //定时发送的时间设置
        //params.put("SendDate","");
        //如果使用场景模板来发送短信,必须填值
        //params.put("ContentParam","");
        //外系统流水ID,查询发送结构用,可填
        //params.put("ExtOrderId", "");

        //System.out.println("-----------------------请求开始-------------------");
        //long beginTime = System.currentTimeMillis();
        //Map reqMap = uCCPSendService.sendShortMessage(params);
        //System.out.println("接口返回结果:"+reqMap);
        //System.out.println("-----------------------请求总耗时:"+(System.currentTimeMillis()-beginTime)+"-------------------");
        //System.exit(0);
        Map map = uCCPSendService.sendShortMessage(params);
        if (map == null) return "调用sendShortMessage返回结果异常！";
        if (!map.get("code").equals("0000")) {
            return map.get("msg").toString();
        } else {
            return "";
        }
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
