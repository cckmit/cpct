package com.zjtelcom.cpct.util;

import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.core.Converter;

public class BeanUtil {
    public BeanUtil() {
    }

    public static <T> T create(Object source, T target) {
        BeanCopier.create(source.getClass(), target.getClass(), false).copy(source, target, (Converter)null);
        return target;
    }

    public static void copy(Object source, Object target) {
        BeanCopier.create(source.getClass(), target.getClass(), false).copy(source, target, (Converter)null);
    }
}