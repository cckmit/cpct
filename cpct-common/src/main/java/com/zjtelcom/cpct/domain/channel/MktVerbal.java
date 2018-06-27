package com.zjtelcom.cpct.domain.channel;

import lombok.Data;

import java.util.Date;

@Data
public class MktVerbal {
    private Long verbalId;

    private Long campaignId;

    private Long contactConfId;

    private String scriptDesc;

    private Long channelId;

    private String statusCd;

    private Long createStaff;

    private Date createDate;

    private String remark;

}