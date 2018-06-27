package com.zjtelcom.cpct.dto.api;

import java.util.List;
import java.util.Map;

/**
 * 事件上报dto
 */
public class EventReportDTO {

    /**
     * 事件流水号
     */
    private String ISI;

    /**
     * 事件code
     */
    private String eventId;

    /**
     * C4标识
     */
    private String C4;

    /**
     * 渠道code
     */
    private String channelId;

    /**
     * 标签列表
     */
    private List<Map<String,Object>> triggers;
//    private Array triggers;

//    private Object trigger;
//
//    private String key;
//
//    private String value;

    /**
     * 本地网标识
     */
    private Long lanId;

    public String getISI() {
        return ISI;
    }

    public void setISI(String ISI) {
        this.ISI = ISI;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getC4() {
        return C4;
    }

    public void setC4(String c4) {
        C4 = c4;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public List<Map<String, Object>> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<Map<String, Object>> triggers) {
        this.triggers = triggers;
    }

    public Long getLanId() {
        return lanId;
    }

    public void setLanId(Long lanId) {
        this.lanId = lanId;
    }
}
