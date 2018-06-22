package com.zjtelcom.cpct.domain;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
public class Channel extends BaseEntity {
    private Long contactChlId;
    private String contactChlCode;
    private String contactChlName;
    private String contactChlType;
    private String contactChlDesc;
    private Long regionId;


}
