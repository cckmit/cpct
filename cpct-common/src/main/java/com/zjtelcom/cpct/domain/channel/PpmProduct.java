package com.zjtelcom.cpct.domain.channel;

import lombok.Data;

import java.util.Date;

@Data
public class PpmProduct {
    private Long productId;

    private String productCode;

    private String productName;

    private String productDesc;

    private Date startTime;

    private Date endTime;

    private String productType;

    private String status;

    private Integer ppmProductId;

}