package com.zjtelcom.cpct.controller.test;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.dao.system.SysRoleMapper;
import com.zjtelcom.cpct.domain.system.SysRole;

import com.zjtelcom.cpct.service.system.SysAreaService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Te {
    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysAreaService sysAreaService;

    @Test
    public void aa(){
        List<SysRole> listRole = sysRoleMapper.selectByStaffId(new Long(41));
        System.out.println("------------------"+listRole.size());
    }


    @Test
    public void a2(){
        Map<String, Object> map = sysAreaService.listSysArea();
        System.out.println(JSON.toJSONString(map));
        return ;
    }


    public static void main(String[] args) {

    }
}
