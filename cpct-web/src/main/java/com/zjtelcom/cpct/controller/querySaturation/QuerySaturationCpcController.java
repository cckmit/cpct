package com.zjtelcom.cpct.controller.querySaturation;

import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.service.querySaturation.QuerySaturationCpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @author: linchao
 * @date: 2019/04/10 17:31
 * @version: V1.0
 */
@RestController
@RequestMapping("${adminPath}/saturation")
public class QuerySaturationCpcController extends BaseController {

    @Autowired
    private QuerySaturationCpcService querySaturationCpcService;

    /**
     * 标签饱和度查询接口
     * @param queryDate
     * @param lanId
     */
    @CrossOrigin
    @RequestMapping("query")
    public boolean querySaturation(String queryDate, String lanId){
         return querySaturationCpcService.querySaturationCpc(queryDate, lanId);
    }
}