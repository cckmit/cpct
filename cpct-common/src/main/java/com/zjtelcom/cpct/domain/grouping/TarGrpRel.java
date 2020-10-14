package com.zjtelcom.cpct.domain.grouping;

import com.zjtelcom.cpct.BaseEntity;

/**
 * @Description:
 * @author: linchao
 * @date: 2020/09/23 10:21
 * @version: V1.0
 */
public class TarGrpRel extends BaseEntity {
    private Long id;
    private String negation1;
    private Long tarGrpId1;
    private String rel;
    private String negation2;
    private Long tarGrpId2;

    private String tarGrpName1;
    private String tarGrpName2;
    private String relName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNegation1() {
        return negation1;
    }

    public void setNegation1(String negation1) {
        this.negation1 = negation1;
    }

    public Long getTarGrpId1() {
        return tarGrpId1;
    }

    public void setTarGrpId1(Long tarGrpId1) {
        this.tarGrpId1 = tarGrpId1;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getNegation2() {
        return negation2;
    }

    public void setNegation2(String negation2) {
        this.negation2 = negation2;
    }

    public Long getTarGrpId2() {
        return tarGrpId2;
    }

    public void setTarGrpId2(Long tarGrpId2) {
        this.tarGrpId2 = tarGrpId2;
    }

    public String getTarGrpName1() {
        return tarGrpName1;
    }

    public void setTarGrpName1(String tarGrpName1) {
        this.tarGrpName1 = tarGrpName1;
    }

    public String getTarGrpName2() {
        return tarGrpName2;
    }

    public void setTarGrpName2(String tarGrpName2) {
        this.tarGrpName2 = tarGrpName2;
    }

    public String getRelName() {
        return relName;
    }

    public void setRelName(String relName) {
        this.relName = relName;
    }
}