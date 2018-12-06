package com.zjtelcom.cpct.enums;

/**
 * @Description AreaCodeEnum
 * @Author lincaho
 * @Date 2018/12/06 10:15
 */
public enum AreaCodeEnum {

    ZHEJIAGN(1L, 8330000L),
    QUZHOU(570L, 8330800L),
    HAGNZHOU(571L, 8330100L),
    HUZHOU(572L, 8330500L),
    JIAXING(573L, 8330400L),
    NINGBO(574L, 8330200L),
    SHAOXING(575L, 8330600L),
    TAIZHOU(576L, 8331000L),
    WENZHOU(577L, 8330300L),
    LISHUI(578L, 8331100L),
    JINHUA(579L, 8330700L),
    ZHOUSHAN(580L, 8330900L);


    private Long lanId;
    private Long regionId;

    AreaCodeEnum(Long lanId, Long regionId) {
        this.lanId = lanId;
        this.regionId = regionId;
    }

    public Long getLanId() {
        return lanId;
    }

    public void setLanId(Long lanId) {
        this.lanId = lanId;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public static Long getRegionIdByLandId(Long LandId){
        for (AreaCodeEnum areaCodeEnum : AreaCodeEnum.values()) {
            if(LandId.equals(areaCodeEnum.lanId)){
                return areaCodeEnum.regionId;
            }
        }
        return null;
    }

    public static Long getLandIdByRegionId(Long RegionId){
        for (AreaCodeEnum areaCodeEnum : AreaCodeEnum.values()) {
            if(RegionId.equals(areaCodeEnum.regionId)){
                return areaCodeEnum.lanId;
            }
        }
        return null;
    }
}
