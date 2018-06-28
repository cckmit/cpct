/*
 * 文件名：TryCalcService.java
 * 版权：Copyright by 南京星邺汇捷网络科技有限公司
 * 描述：
 * 修改人：taowenwu
 * 修改时间：2017年11月8日
 * 修改内容：
 */

package com.zjtelcom.cpct.service;


import com.zjhcsoft.eagle.main.dubbo.model.policy.CalcReqModel;
import com.zjtelcom.cpct.validator.ValidateResult;


/**
 * 试算服务接口
 * @author taowenwu
 * @version 1.0
 * @see TryCalcService
 * @since JDK1.7
 */

public interface TryCalcService {

    /**
     * 校验标签是否存在于大数据，校验标签与主表之间有没有关系
     * 
     * @param serialNum 流水号
     * @param calcReqModel 试算请求对象
     * @return 校验结果
     * @see
     */
    ValidateResult validate(String serialNum, CalcReqModel calcReqModel);
}
