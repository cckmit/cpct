package com.zjtelcom.cpct.service.grouping;

import com.zjtelcom.cpct.domain.grouping.ServicePackage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: linchao
 * @date: 2019/08/08 17:09
 * @version: V1.0
 */
public interface ServicePackageService {

    Map<String, Object> saveServicePackage(String name, MultipartFile multipartFile);

    Map<String, Object> getServicePackageList(Map<String, Object> params);

    Map<String, Object> selectByName(String servicePackageName);

    Map<String, Object> deleteServicePackage(Long servicePackageId);

}