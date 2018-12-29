package com.zjtelcom.cpct.count.serviceImpl.api;

import com.zjtelcom.cpct.count.base.ResultEnum;
import com.zjtelcom.cpct.count.controller.GroupApiController;
import com.zjtelcom.cpct.count.service.api.GroupApiService;
import com.zjtelcom.cpct.dao.grouping.TarGrpMapper;
import com.zjtelcom.cpct.dto.grouping.TarGrp;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/12/28
 * @Description:分群id试算标签
 */

@Service
@Transactional
public class GroupApiServiceImpl implements GroupApiService {

    private Logger log = LoggerFactory.getLogger(GroupApiServiceImpl.class);

    @Autowired
    private TarGrpMapper tarGrpMapper;


    /**
     * 通过分群id
     * @param paramMap
     * @return
     */
    @Override
    public Map<String, Object> groupTrial(Map<String, Object> paramMap) {
        Map<String, Object> map=new HashMap<>();
        map.put("resultCode",ResultEnum.SUCCESS.getStatus());
        map.put("resultMsg",ResultEnum.SUCCESS);
        log.info("分群请求参数："+paramMap);
        String groupId = (String) paramMap.get("groupId");
        if(StringUtils.isBlank(groupId)){
            map.put("resultCode",ResultEnum.FAILED.getStatus());
            map.put("resultMsg","分群id信息不能为空");
            return  map;
        }
        //分割字符串判断分群信息是否存在
        List<String> groupList=getGroupList(groupId);
        //依次搜索分群id信息是否存在
        for (String str:groupList){
            TarGrp tarGrp = tarGrpMapper.selectByPrimaryKey(Long.valueOf(str));
            if(tarGrp==null){
                map.put("resultCode",ResultEnum.FAILED.getStatus());
                map.put("resultMsg","分群id "+str+" 信息不存在");
                return  map;
            }
        }
        //调用es的试算服务





        log.info("试算返回信息");
        return map;
    }





    /**
     * 得到分群id集合
     * @param groupId
     * @return
     */
    public List<String> getGroupList(String groupId){
        List<String> groupList=new ArrayList<>();
        if(groupId.contains(";")){
            String[] split = groupId.split(";");
            for (String s:split){
                groupList.add(s);
            }
        }else if(groupId.contains(",")){
            String[] split = groupId.split(",");
            for (String s:split){
                groupList.add(s);
            }
        }else{
            groupList.add(groupId);
        }



        return groupList;

    }


    /**
     * 验证判断身份权限
     * @param paramMap
     * @return
     */
    public Map<String,String> verification(Map<String, Object> paramMap){
        Map<String,String> map=new HashMap<>();
        String channel = (String) paramMap.get("channel");
        String channelToken = (String) paramMap.get("channelToken");


        return map;

    }


    public static void main(String[] args) {
        System.out.println(ResultEnum.SUCCESS);
    }



}
