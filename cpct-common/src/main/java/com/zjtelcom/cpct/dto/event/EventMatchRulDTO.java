package com.zjtelcom.cpct.dto.event;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description EventMatchRulDTO
 * @Author pengy
 * @Date 2018/6/22 9:31
 */
@Data
public class EventMatchRulDTO implements Serializable {

    private Long evtMatchRulId;//记录事件的规则标识主键
    private String evtRulName;//事件主键标识

}