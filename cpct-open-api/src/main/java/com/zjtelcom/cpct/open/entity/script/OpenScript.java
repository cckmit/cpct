package com.zjtelcom.cpct.open.entity.script;

import com.zjtelcom.cpct.open.base.entity.BaseEntity;

/**
 * @Auther: anson
 * @Date: 2018/11/7
 * @Description:营销活动脚本
 */
public class OpenScript extends BaseEntity {

    private Long evtContactConfId;//'事件推送策略标识',
    private String execChannel;   //执行渠道
    private String remark;
    private String scriptDesc;//'记录营销活动该渠道执行环节的具体脚本内容
    private String scriptName;
    private String scriptType;
    private String statusCd;
    private Long mktCampaignScptId;//'营销活动脚本标识，主键',
    private Long mktCampaignId;//'营销活动标识',



    private Long lanId;//'记录本地网标识，数据来源于公共管理区域。'








}
