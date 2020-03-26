package com.zjtelcom.cpct.service.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({ElementType.METHOD})
@Retention(RUNTIME)
public @interface InterfaceTimeoutMonitoring {

    /**
     * 当接口响应超时时,是否发送短信.默认发送
     * @return 返回ture需要发送短信
     */
    boolean shortMessageIfTimeout() default true;

}
