package com.zjtelcom.cpct.enums;

public enum DttsMsgEnum {


    DELAY_NOTICE_MESSAGE(10001L),
    OFFER_OUT_TIME_MESSAGE(10002L),
    TRIAL_FAIL(10003L),
    CAMPAIGN(10004L),
    EVENT(10005L);

    private Long id;

    DttsMsgEnum(Long id){
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
