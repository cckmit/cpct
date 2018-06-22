package com.zjtelcom.cpct.domain.event;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

/**
 * @Description InterfaceCfg
 * @Author pengy
 * @Date 2018/6/22 9:31
 */
@Data
public class InterfaceCfg extends BaseEntity{

    private Long interfaceCfgId;//接口配置标识，主键标识
    private Long evtSrcId;//触点事件源标识
    private String interfaceName;//记录接口名称
    private String interfaceDesc;//接口描述
    private String interfaceNbr;//记录接口编码
    private String interfaceType;//接口的交互类，1000服务接口 2000文件接口 3000数据同步接口 4000APP探针 5000页面探针
    private String provider;//接口的服务端的渠道名称
    private String caller;//接口的调用端的渠道名称
    private String protocolType;//协议类型，1000HTTP 2000FTP

}