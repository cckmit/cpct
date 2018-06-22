package com.zjtelcom.cpct.domain.channel;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
public class CamScript extends BaseEntity {
    private Long mktCampaignScptId;//'营销活动脚本标识，主键',
    private Long mktCampaignId;//'营销活动标识',
    private Long evtContactConfId;//'事件推送策略标识',
    private String scriptDesc;//'记录营销活动该渠道执行环节的具体脚本内容
    private Long lanId;//'记录本地网标识，数据来源于公共管理区域。'
}
