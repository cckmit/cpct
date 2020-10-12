package com.zjtelcom.cpct.service.impl.campaign;

import com.ctzj.service.outbound.QrCodeService;
import com.zjtelcom.cpct.bean.ResponseVO;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCamChlConfAttrMapper;
import com.zjtelcom.cpct.dao.campaign.MktCamResourceQRCodeMapper;
import com.zjtelcom.cpct.domain.channel.MktCamResource;
import com.zjtelcom.cpct.enums.ConfAttrEnum;
import com.zjtelcom.cpct.service.campaign.MktCamResourceQRCodeService;
import com.zjtelcom.cpct.util.DateUtil;
import javafx.beans.binding.ObjectBinding;
import net.sf.ehcache.transaction.xa.EhcacheXAException;
import org.bouncycastle.asn1.cms.PasswordRecipientInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class MktCamResourceQRCodeImpl implements MktCamResourceQRCodeService {
    @Autowired
    private QrCodeService qrCodeService;
    @Autowired
    private MktCamResourceQRCodeMapper mktCamResourceQRCodeMapper;
    @Autowired
    private MktCamChlConfAttrMapper mktCamChlConfAttrMapper;
    @Autowired
    private   ResponseVO responseVO;

    private static final Logger logger = LoggerFactory.getLogger(MktCamResourceQRCodeImpl.class);

    private String getQRUrlByMktResourceId(String requestId,String h5Url,String mktResourceId){
        Map<String,Object> paraMap = new HashMap<>();
        Map<String,Object> urlParams = new HashMap<>();
        urlParams.put("wd",mktResourceId);
        paraMap.put("requestId",requestId);
        paraMap.put("url",h5Url);
        paraMap.put("urlParams",urlParams);
        logger.info("获取二维码url  paramMap:" + paraMap);
        Map<String,Object> qrResultMap = qrCodeService.generate(paraMap);
        String code =(String)qrResultMap.get("code");
        String qrUrl = "";
        String digitalCouponsId =(String) paraMap.get("digitalCouponsId");
        if(code.equals(CommonConstant.CODE_SUCCESS)){
             Map<String, Object> body =(Map<String, Object>)qrResultMap.get("body");
            qrUrl = (String)body.get("qrCode");
        }
        return qrUrl;
    }
    //提供海报元素查询
    @Override
    public Map<String, Object> generatePoster(Map<String,Object> params) {
        String requestId = DateUtil.Date2String(new Date());
        String h5Url = "https://www.baidu.com/s";
        Integer ruleId =(Integer)params.get("ruleId");
        String channelId = (String)params.get("channelId");
        Map<String,Object> resultMap = new HashMap<>();
        MktCamResource mktCamResource = mktCamResourceQRCodeMapper.selectRecordByRuleId(ruleId.longValue());
        Long mktCamResourceId = mktCamResource.getMktCamResourceId();
        //获取电子券id
        Long resourceId = mktCamResource.getResourceId();
        //查看二维码是否存在
        String qrUrl = mktCamResource.getQcCodeUrl();
        String qrUrlToUse = "";
        if( qrUrl != null && qrUrl.equals("")){
            //直接返回QRurl
           qrUrlToUse = qrUrl;

        }else {
            qrUrlToUse = this.getQRUrlByMktResourceId(requestId,h5Url,mktCamResourceId.toString());
            logger.info("二维码url" + qrUrlToUse);
            //保存电子券综合表中
            mktCamResourceQRCodeMapper.updateQRUrlbyMktResourceId(qrUrlToUse,mktCamResourceId);
        }

        resultMap.put("resourceId",resourceId);
        resultMap.put("qrUrl",qrUrlToUse);
        resultMap.put("channelId",channelId);
        return  resultMap;
    }

//保存海报url到渠道属性表
    @Override
    public Map<String, Object> savePostToChanlAttr(Map<String, Object> params) {
        Map<String,Object> resultMap = new HashMap<>();
        Long channelId =(Long) params.get("channelId");
        String postUrl =(String) params.get("postUrl");
        int isSuccess = mktCamChlConfAttrMapper.updateAttridByChannelId(ConfAttrEnum.POST_TYPE.getArrId(),
                0L,
                postUrl,channelId);
        if(isSuccess == 1){
            return  responseVO.response("200","海报保存成功");
        }else {
            return responseVO.response("500","海报保存失败");
        }
    }

}
