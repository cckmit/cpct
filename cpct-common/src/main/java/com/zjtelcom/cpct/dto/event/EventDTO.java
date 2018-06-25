package com.zjtelcom.cpct.dto.event;

import com.zjtelcom.cpct.domain.campaign.DO.MktCampaignDO;
import com.zjtelcom.cpct.domain.event.EventDO;
import com.zjtelcom.cpct.domain.event.EventItemDO;
import com.zjtelcom.cpct.domain.event.EventTypeDO;
import com.zjtelcom.cpct.domain.event.EventMatchRulDO;
import lombok.Data;

import java.util.List;

/**
 * @Description EventDTO
 * @Author pengy
 * @Date 2018/6/22 9:31
 */
@Data
public class EventDTO extends EventDO {

    private Long eventId; //事件主键标识
    private Long interfaceCfgId;//接口配置标识，主键标识
    private String eventNbr;//记录事件的编码信息
    private String eventName;//记录事件的名称
    private String evtMappedAddr;//记录事件的映射地址，事件识别时可通过这个映身地址来适配触点事件，可以是URL地址，APP的类包名或其它识别编码
    private String evtMappedIp;//记录事件的映射匹配IP地址，事件识别可通过匹配IP地址进行匹配触点事件
    private String evtProcotolType;//记录接口协议类型,1000	HTTP 2000FTP
    private String evtMappedFunName;//事件匹配映射的方法名，用于事件识别
    private String eventDesc;//记录事件的描述说明
    private Long evtTypeId;//记录事件的所属事件类型标识
    private String eventTrigType;//记录事件的触发类型,1000实时触发事件 2000定期触发事件 3000人工触发事件
    private Long extEventId;//记录集团下发的事件标识
    private List<MktCampaignDO> mktCampaignDTOList;//活动实体类
    private List<EventMatchRulDO> eventMatchRulDOList;//事件匹配规则实体类
    private List<EventItemDO> eventItemDOList;//事件采集项
    private List<EventTypeDO> eventTypeDOList;//事件目录
    private List<ScenceCampaignDTO> scenceCampaignDTOS;//场景与活动

}
