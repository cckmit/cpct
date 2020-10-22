package com.zjtelcom.cpct.dto.channel;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class MktResource4RuleVO implements Serializable {

    private Long resourceApplyNum;

    private Long days;

    private Date startTime;

    private Date endTime;

    private Date getStartTime;

    private Date getEndTime;

    private List<String> dealShops;

}
