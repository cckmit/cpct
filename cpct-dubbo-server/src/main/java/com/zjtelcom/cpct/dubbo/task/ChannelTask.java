package com.zjtelcom.cpct.dubbo.task;

import com.zjtelcom.cpct.dao.campaign.MktCamChlConfAttrMapper;
import com.zjtelcom.cpct.dao.campaign.MktCamChlConfMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.MktCamScriptMapper;
import com.zjtelcom.cpct.dao.channel.MktVerbalConditionMapper;
import com.zjtelcom.cpct.dao.channel.MktVerbalMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamChlConfAttrDO;
import com.zjtelcom.cpct.domain.campaign.MktCamChlConfDO;
import com.zjtelcom.cpct.domain.channel.CamScript;
import com.zjtelcom.cpct.domain.channel.MktVerbal;
import com.zjtelcom.cpct.domain.channel.MktVerbalCondition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class ChannelTask implements Callable<Map<String, Object>> {

    @Autowired
    private MktCamChlConfAttrMapper mktCamChlConfAttrMapper; //协同渠道配置基本信息

    @Autowired
    private MktCamChlConfMapper mktCamChlConfMapper; //协同渠道配置的渠道

    @Autowired
    private MktVerbalConditionMapper mktVerbalConditionMapper; //规则存储公共表（此处查询协同渠道子策略规则和话术规则）

    @Autowired
    private MktCamScriptMapper mktCamScriptMapper; //营销脚本

    @Autowired
    private MktVerbalMapper mktVerbalMapper; //话术

    @Autowired
    private InjectionLabelMapper injectionLabelMapper; //标签因子

    //策略配置id
    private Long evtContactConfId;

    private List<Map<String, String>> productList;

    public ChannelTask(Long evtContactConfId, List<Map<String, String>> productList) {
        this.evtContactConfId = evtContactConfId;
        this.productList = productList;
    }

    @Override
    public Map<String, Object> call() {

        //获取当前时间
        Date now = new Date();

        //初始化返回结果推荐信息
        Map<String, Object> recommend = new HashMap<>();

        //查询渠道属性
        List<MktCamChlConfAttrDO> mktCamChlConfAttrs = mktCamChlConfAttrMapper.selectByEvtContactConfId(evtContactConfId);

        boolean checkTime = true;
        for (MktCamChlConfAttrDO mktCamChlConfAttrDO : mktCamChlConfAttrs) {
            //判断渠道生失效时间
            if (mktCamChlConfAttrDO.getAttrId() == 1000L) {
                if (!now.after(new Date(mktCamChlConfAttrDO.getAttrValue()))) {
                    checkTime = false;
                }
            }
            if (mktCamChlConfAttrDO.getAttrId() == 1001L) {
                if (!now.after(new Date(mktCamChlConfAttrDO.getAttrValue()))) {
                    checkTime = false;
                }
            }
        }

        if (!checkTime) {
            return null;
        }

        //查询渠道信息基本信息
        MktCamChlConfDO mktCamChlConf = mktCamChlConfMapper.selectByPrimaryKey(evtContactConfId);

        //返回渠道基本信息
        recommend.put("channelId", mktCamChlConf.getContactChlId());
        recommend.put("pushType", mktCamChlConf.getPushType());
        recommend.put("pushContent", ""); //todo 不明

        //返回结果中添加销售品信息
        recommend.put("productList", productList);


        //查询渠道子策略 这里老系统暂时不返回
//              List<MktVerbalCondition> mktVerbalConditions = mktVerbalConditionMapper.findConditionListByVerbalId(evtContactConfId);

        //查询脚本
        CamScript camScript = mktCamScriptMapper.selectByConfId(evtContactConfId);
        if (camScript != null) {
            recommend.put("reason", camScript.getScriptDesc());
        }

        //查询话术
        List<MktVerbal> mktVerbals = mktVerbalMapper.findVerbalListByConfId(evtContactConfId);
        if (mktVerbals != null && mktVerbals.size() > 0) {
            //todo  多个如何返回
            recommend.put("keyNote", mktVerbals.get(0).getScriptDesc());
        }

        if (mktVerbals != null && mktVerbals.size() > 0) {
            for (MktVerbal mktVerbal : mktVerbals) {
                //查询话术规则
                List<MktVerbalCondition> channelConditionList = mktVerbalConditionMapper.findChannelConditionListByVerbalId(mktVerbal.getVerbalId());
                //todo 格式化话术规则 如何返回  可能需要判断规则

            }
        }
        recommend.put("verbal", ""); //todo 待定

        return recommend;
    }
}
