/*
 * 文件名：GroovyUtil.java
 * 版权：Copyright by 南京星邺汇捷网络科技有限公司
 * 描述：
 * 修改人：taowenwu
 * 修改时间：2017年11月20日
 * 修改内容：
 */

package com.zjtelcom.cpct.util;


import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.apache.log4j.Logger;


/**
 * groovy工具类
 * @author taowenwu
 * @version 1.0
 * @see GroovyUtil
 * @since JDK1.7
 */

public class GroovyUtil {
    private static final Logger LOG = Logger.getLogger(GroovyUtil.class);

    public static Object invokeMethod(String script, String methodName, Object... args) {

        Class clazz = new GroovyClassLoader().parseClass(script);
        GroovyObject groovyObject;
        try {
            groovyObject = (GroovyObject)clazz.newInstance();
            return groovyObject.invokeMethod(methodName, args);
        }
        catch (Exception e) {
            LOG.error("(GroovyObject)clazz.newInstance() error", e);
        }
        return null;
    }

    public static GroovyObject getObject(String script) {

        Class clazz = new GroovyClassLoader().parseClass(script);
        GroovyObject groovyObject;
        try {
            groovyObject = (GroovyObject)clazz.newInstance();
            return groovyObject;
        }
        catch (Exception e) {
            LOG.error("(GroovyObject)clazz.newInstance() error", e);
        }
        return null;
    }
}
