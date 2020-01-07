package com.zjtelcom.cpct.dubbo.service;

import java.util.Map;

/**
 * @Description: 根据事件编码进行特殊业务处理代码
 * @author: linchao
 * @date: 2020/01/03 11:25
 * @version: V1.0
 */
public interface SpecialService {

    Map<String, Object> deal(Map<String, Object> params);

}