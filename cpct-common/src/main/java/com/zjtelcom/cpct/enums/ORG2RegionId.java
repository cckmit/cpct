package com.zjtelcom.cpct.enums;

public enum ORG2RegionId {

    ORG_ZHEJIAGN(800000000004L, 8330000L),
    ORG_QUZHOU(800000000040L, 8330800L),
    ORG_HAGNZHOU(800000000037L, 8330100L),
    ORG_HUZHOU(800000000021L, 8330500L),
    ORG_JIAXING(800000000022L, 8330400L),
    ORG_NINGBO(800000000023L, 8330200L),
    ORG_SHAOXING(800000000024L, 8330600L),
    ORG_TAIZHOU(800000000041L, 8331000L),
    ORG_WENZHOU(800000000025L, 8330300L),
    ORG_LISHUI(800000000039L, 8331100L),
    ORG_JINHUA(800000000038L, 8330700L),
    ORG_ZHOUSHAN(800000000026L, 8330900L);

    private Long orgId;
    private Long regionId;

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    ORG2RegionId(Long orgId, Long regionId) {
        this.orgId = orgId;
        this.regionId = regionId;
    }


    public static Long getRegionIdByorgId(Long orgId){
        for (ORG2RegionId org2RegionId : ORG2RegionId.values()) {
            if(orgId.equals(org2RegionId.orgId)){
                return org2RegionId.regionId;
            }
        }
        return null;
    }

    public static Long getOrgIdByRegionId(Long RegionId){
        for (ORG2RegionId org2RegionId : ORG2RegionId.values()) {
            if(RegionId.equals(org2RegionId.regionId)){
                return org2RegionId.orgId;
            }
        }
        return null;
    }
}
