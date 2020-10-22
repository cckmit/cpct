package com.zjtelcom.cpct.domain.campaign;

import lombok.Data;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.util.Date;

@Data
public class MktRequestDO {
    private Long requestId;
    private String requestType;
    private String nodeId;
    private Long catelogId;
    private Long lanId;
    private String staff;
    private Date createDate;
    private Date updateDate;
    private String comment;

}
