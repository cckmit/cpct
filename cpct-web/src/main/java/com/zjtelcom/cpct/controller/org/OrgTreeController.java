package com.zjtelcom.cpct.controller.org;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.org.OrgTree;
import com.zjtelcom.cpct.service.org.OrgTreeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/9/30
 * @Description:营销组织树
 */
@RestController
@RequestMapping("${adminPath}/orgTree")
public class OrgTreeController extends BaseController {

    @Autowired
    private OrgTreeService orgTreeService;


    /**
     * 测试营销组织树插入
     * @return
     */
    @RequestMapping("fileToSql")
    @CrossOrigin
    public String batchEvent(@RequestBody Map<String, Object> params){
        Map<String, Object> map=new HashMap<>();
        long l = System.currentTimeMillis();
        String name= (String) params.get("name");
        String path="D:\\code\\"+name;
            orgTreeService.getDataByFtp(path);
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", "success");
        System.out.println("插入完毕 耗时:"+(System.currentTimeMillis()-l));
        return  JSON.toJSONString(map);
    }


    /**
     * 通过菜单id查询其子菜单
     * @param params
     * @return
     */
    @RequestMapping("selectOrgTree")
    @CrossOrigin
    public String selectOrg(@RequestBody Map<String, Object> params){
        Map<String, Object> maps = new HashMap<>();
        String areaId= (String) params.get("areaId");
        String page= (String) params.get("page");
        String pageSize= (String) params.get("pageSize");
        Integer id=null;
        Integer pageId=0;
        Integer pageSizeId=0;
        //如果page 和pageSize为空时 传回所有数据
        if(StringUtils.isNotBlank(page)&&StringUtils.isNotBlank(pageSize)){
            pageId=Integer.parseInt(page);
            pageSizeId=Integer.parseInt(pageSize);
        }
        if(StringUtils.isNotBlank(areaId)){
            id=Integer.parseInt(areaId);
        }
        maps = orgTreeService.selectBySumAreaId(id,pageId,pageSizeId);
        return JSON.toJSONString(maps);
    }
}
