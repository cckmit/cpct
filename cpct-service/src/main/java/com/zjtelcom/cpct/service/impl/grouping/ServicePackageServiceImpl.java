package com.zjtelcom.cpct.service.impl.grouping;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.grouping.ServicePackageMapper;
import com.zjtelcom.cpct.domain.grouping.ServicePackage;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.service.grouping.ServicePackageService;
import com.zjtelcom.cpct.util.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * @Description:
 * @author: linchao
 * @date: 2019/08/08 17:17
 * @version: V1.0
 */
@Service
public class ServicePackageServiceImpl implements ServicePackageService {

    public static final Logger logger = LoggerFactory.getLogger(ServicePackageServiceImpl.class);


    @Autowired
    private ServicePackageMapper servicePackageMapper;

    @Override
    public Map<String, Object> saveServicePackage(String name, MultipartFile multipartFile) {
        Map<String, Object> maps = new HashMap<>();
        try {
            ServicePackage servicePackage = new ServicePackage();
            servicePackage.setServicePackageName(name);
            servicePackage.setCreateDate(new Date());
            servicePackage.setCreateStaff(UserUtil.loginId());
            servicePackage.setUpdateDate(new Date());
            servicePackage.setUpdateStaff(UserUtil.loginId());
            servicePackage.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
            servicePackage.setStatusDate(new Date());
            servicePackageMapper.insert(servicePackage);
            maps.put("resultCode", CommonConstant.CODE_SUCCESS);
            maps.put("resultMsg", "添加服务包成功！");
        } catch (Exception e) {
            maps.put("resultCode", CommonConstant.CODE_FAIL);
            maps.put("resultMsg", "添加服务包列表失败！");
        } finally {
            return maps;
        }
    }


    @Override
    public Map<String, Object> deleteServicePackage(Long servicePackageId) {
        Map<String, Object> maps = new HashMap<>();
        try {
            servicePackageMapper.deleteByPrimaryKey(servicePackageId);
            maps.put("resultCode", CommonConstant.CODE_SUCCESS);
            maps.put("resultMsg", "删除服务包成功！");
        } catch (Exception e) {
            maps.put("resultCode", CommonConstant.CODE_FAIL);
            maps.put("resultMsg", "删除服务包列表失败！");
        } finally {
            return maps;
        }
    }



    @Override
    public Map<String, Object> getServicePackageList(Map<String, Object> params) {
        Map<String, Object> maps = new HashMap<>();
        List<ServicePackage> servicePackageList = new ArrayList<>();
        try {
            PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("pageSize").toString()));
            servicePackageList = servicePackageMapper.selectByName((String) params.get("servicePackageName"));
            maps.put("resultCode", CommonConstant.CODE_SUCCESS);
            maps.put("resultMsg", "查询服务包列表成功！");
            maps.put("servicePackageList", servicePackageList);
            maps.put("pageInfo", new Page(new PageInfo(servicePackageList)));
        } catch (Exception e) {
            maps.put("resultCode", CommonConstant.CODE_FAIL);
            maps.put("resultMsg", "查询服务包列表失败！");
        } finally {
            return maps;
        }
    }

    @Override
    public Map<String, Object> selectByName(String servicePackageName) {
        Map<String, Object> maps = new HashMap<>();
        List<ServicePackage> servicePackageList = new ArrayList<>();
        try {
            servicePackageList = servicePackageMapper.selectByName(servicePackageName);
            maps.put("resultCode", CommonConstant.CODE_SUCCESS);
            maps.put("resultMsg", "查询服务包列表成功！");
            maps.put("servicePackageList", servicePackageList);
        } catch (Exception e) {
            maps.put("resultCode", CommonConstant.CODE_FAIL);
            maps.put("resultMsg", "查询服务包列表失败！");
        } finally {
            return maps;
        }
    }


}