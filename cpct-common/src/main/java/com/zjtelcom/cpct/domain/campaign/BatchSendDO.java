package com.zjtelcom.cpct.domain.campaign;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BatchSendDO extends BaseEntity implements Serializable {
    private Integer batchId;
    private String batchNum;
    private String fileName;
    private String total;
    private String state;
    private Date createDate;
    private Date updateDate;
    private String littleBatch;
}
