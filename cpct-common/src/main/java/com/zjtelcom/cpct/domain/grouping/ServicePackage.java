package com.zjtelcom.cpct.domain.grouping;

import com.zjtelcom.cpct.BaseEntity;

/**
 * @Description:
 * @author: linchao
 * @date: 2019/08/12 10:04
 * @version: V1.0
 */
public class ServicePackage extends BaseEntity {
    /**
     *服务包标识
     */
    private Long servicePackageId;

    /**
     * 服务包名称
     */
    private String servicePackageName;

    public Long getServicePackageId() {
        return servicePackageId;
    }

    public void setServicePackageId(Long servicePackageId) {
        this.servicePackageId = servicePackageId;
    }

    public String getServicePackageName() {
        return servicePackageName;
    }

    public void setServicePackageName(String servicePackageName) {
        this.servicePackageName = servicePackageName;
    }
}