package com.zjtelcom.cpct.open.entity.mktQuestion;

import lombok.Data;

@Data
public class MktQstQuestDetailRel {

    private Long relConfId;
    private Long aNaireId;
    private Long aQuestionId;
    private Long aQstDetailId;
    private String rstrType;
    private Long zNaireId;
    private Long zQuestionId;
    private Long zQstDetailId;
    private String statusCd;
    private String statusDate;
    private String remark;
}
