package com.zjtelcom.cpct.open.entity.event;

import lombok.Data;

import java.util.Date;

/**
 * @Auther: anson
 * @Date: 2018/10/31
 * @Description:事件源接口
 */
@Data
public class EventSourceInterface {

    private Long interfaceCfgId;

    private Long evtSrcId;//事件源id

    private String interfaceName;

    private String interfaceDesc;

    private String interfaceCode;//和interfaceNbr一致

    private String interfaceType;//1000	服务接口;2000	文件接口;3000	数据同步接口;4000	APP探针;5000	页面探针;

    private String provider;

    private String caller;

    private String protocolType;//1000	HTTP;2000	FTP 协议类型

    private String statusDate;//状态时间

    private String remark;
}
