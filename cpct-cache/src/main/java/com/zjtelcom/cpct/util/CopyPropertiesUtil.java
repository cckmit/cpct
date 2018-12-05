package com.zjtelcom.cpct.util;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;

/**
 * Description: 通过反射复制不同实体类的相同属性
 * author: linchao
 * date: 2018/04/08 14:50
 * version: V1.0
 */
public class CopyPropertiesUtil extends PropertyUtilsBean {

    /**
     * 把orig和dest相同属性的value复制到dest中
     * @param dest
     * @param orig
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void copyBean2Bean(Object dest, Object orig) throws Exception {
        convert(dest, orig);
    }

    private static void convert(Object dest, Object orig) throws IllegalAccessException, InvocationTargetException {
        if (dest == null) {
            throw new IllegalArgumentException
                    ("No destination bean specified");
        }
        if (orig == null) {
            throw new IllegalArgumentException("No origin bean specified");
        }
        if (orig instanceof DynaBean) {
            DynaProperty origDescriptors[] =
                    ( (DynaBean) orig).getDynaClass().getDynaProperties();
            for (int i = 0; i < origDescriptors.length; i++) {
                String name = origDescriptors[i].getName();
                if (PropertyUtils.isWriteable(dest, name)) {
                    Object value = ( (DynaBean) orig).get(name);
                    try {
                        getInstance().setSimpleProperty(dest, name, value);
                    }
                    catch (Exception e) {
                        ; // Should not happen
                    }
                }
            }
        }
        else if (orig instanceof Map) {
            Iterator names = ( (Map) orig).keySet().iterator();
            while (names.hasNext()) {
                String name = (String) names.next();
                if (PropertyUtils.isWriteable(dest, name)) {
                    Object value = ( (Map) orig).get(name);
                    try {
                        getInstance().setSimpleProperty(dest, name, value);
                    }
                    catch (Exception e) {
                        ; // Should not happen
                    }
                }
            }
        }
        else
        {
            PropertyDescriptor origDescriptors[] =
                    PropertyUtils.getPropertyDescriptors(orig);
            for (int i = 0; i < origDescriptors.length; i++) {
                String name = origDescriptors[i].getName();
                if ("class".equals(name)) {
                    continue; // No point in trying to set an object's class
                }
                if (PropertyUtils.isReadable(orig, name) &&
                        PropertyUtils.isWriteable(dest, name)) {
                    try {
                        Object value = PropertyUtils.getSimpleProperty(orig, name);
                        getInstance().setSimpleProperty(dest, name, value);
                    }
                    catch (IllegalArgumentException ie) {
                        ; // Should not happen
                    }
                    catch (Exception e) {
                        ; // Should not happen
                    }
                }
            }
        }
    }
}
