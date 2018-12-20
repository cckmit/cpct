package com.zjtelcom.cpct.open.entity.mktQuestion;

import lombok.Data;

import java.util.List;

@Data
public class MktQstQuestRel {

    private Long relId;
    private Long naireId;
    private Long questionId;
    private Integer questionOrder;
    private Integer questionWeight;
    private Short isMark;
    private Short isMust;
    private String statusCd;
    private String statusDate;
    private String remark;
    List<MktQstQuestDetailRel> mktQstQuestDetailRel;
    private MktQuestionRef mktQuestionRef;
}
