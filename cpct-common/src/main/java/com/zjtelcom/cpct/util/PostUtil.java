package com.zjtelcom.cpct.util;

import com.ctzj.smt.bss.sysmgr.model.common.Page;
import com.ctzj.smt.bss.sysmgr.model.common.SysmgrResultObject;
import com.ctzj.smt.bss.sysmgr.model.dataobject.SystemPost;
import com.ctzj.smt.bss.sysmgr.model.query.QrySystemPostReq;
import com.ctzj.smt.bss.sysmgr.privilege.service.dubbo.api.ISystemPostDubboService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @Description:
 * @author: linchao
 * @date: 2019/01/07 16:25
 * @version: V1.0
 */
@Component
public class PostUtil {

//    private ISystemPostDubboService iSystemPostDubboService;
//
//    private static PostUtil postUtil;
//
//    @PostConstruct
//    public void init() {
//        postUtil= this;
//        postUtil.iSystemPostDubboService = iSystemPostDubboService;
//    }
//
//
//    public static String getPostNameByCode(String postCode){
//        String postName = "";
//        SystemPost systemPost = new SystemPost();
//        systemPost.setSysPostCode(postCode);
//        QrySystemPostReq qrySystemPostReq = new QrySystemPostReq();
//        qrySystemPostReq.setSystemPost(systemPost);
//        SysmgrResultObject<Page> pageSysmgrResultObject = postUtil.iSystemPostDubboService.qrySystemPostPage(new Page(), qrySystemPostReq);
//        if(pageSysmgrResultObject!=null){
//            if( pageSysmgrResultObject.getResultObject()!=null){
//                List<SystemPost> dataList = (List<SystemPost>) pageSysmgrResultObject.getResultObject().getDataList();
//                if(dataList!=null){
//                   if(dataList.get(0)!=null){
//                       postName = dataList.get(0).getSysPostName();
//                   }
//                }
//            }
//        }
//        return postName;
//    }
}