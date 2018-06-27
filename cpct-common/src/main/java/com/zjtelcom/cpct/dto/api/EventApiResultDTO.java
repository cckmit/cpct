package com.zjtelcom.cpct.dto.api;

import lombok.Data;

/**
 * 接口调用结果
 */
@Data
public class EventApiResultDTO {

    /**
     * 事件流水号
     */
    private String ISI;

    /**
     * 接口调用结果编码：
     * 1 接口调用成功；
     * 1000 调用异常；
     */
    private String result_code;

    /**
     * 接口调用结果描述
     */
    private String result_msg;


}
