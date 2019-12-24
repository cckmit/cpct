package com.zjtelcom.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SysLog {

    /**
     * 服务id
     * @return
     */
    String serviceId();

    /**
     * 模块名
     * @return
     */
    String moduleName();

    /**
     * 操作名
     * @return
     */
    String actionName();

    /**
     * 索引名
     * @return
     */
    String indexName();



}
