package com.zjtelcom.cpct.enums;

/**
 * @Description AreaCodeEnum
 * @Author lincaho
 * @Date 2018/12/06 10:15
 */
public enum AreaCodeEnum {

    ZHEJIAGN(1L, 8330000L, 0L),
    QUZHOU(570L, 8330800L, 20L),
    HAGNZHOU(571L, 8330100L, 10L),
    HUZHOU(572L, 8330500L, 11L),
    JIAXING(573L, 8330400L, 12L),
    NINGBO(574L, 8330200L, 13L),
    SHAOXING(575L, 8330600L, 14L),
    TAIZHOU(576L, 8331000L, 15L),
    WENZHOU(577L, 8330300L, 16L),
    LISHUI(578L, 8331100L, 17L),
    JINHUA(579L, 8330700L, 18L),
    ZHOUSHAN(580L, 8330900L, 19L);


    private Long lanId;
    private Long regionId;
    private Long latnId;

    AreaCodeEnum(Long lanId, Long regionId, Long latnId) {
        this.lanId = lanId;
        this.regionId = regionId;
        this.latnId = latnId;
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

    public Long getLatnId() { return latnId; }

    public void setLatnId(Long latnId) { this.latnId = latnId; }

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

    public static Long getLatnIdByRegionId(Long RegionId){
        for (AreaCodeEnum areaCodeEnum : AreaCodeEnum.values()) {
            if(RegionId.equals(areaCodeEnum.regionId)){
                return areaCodeEnum.latnId;
            }
        }
        return null;
    }

    public enum sysAreaCode{
        CHAOGUAN("cpcp0001","C1","集团"),
        SHENGJI("cpcpch0001","C2","省级"),
        FENGONGSI("cpcpch0002","C3","市级"),
        FENGJU("cpcpch0003","C4","区县"),
        ZHIJU("cpcpch0004","C5","区县");

        private String sysPostCode;
        private String sysArea;
        private String sysAreaName;


        public String getSysAreaName() {
            return sysAreaName;
        }

        public void setSysAreaName(String sysAreaName) {
            this.sysAreaName = sysAreaName;
        }

        public String getSysPostCode() {
            return sysPostCode;
        }

        public void setSysPostCode(String sysPostCode) {
            this.sysPostCode = sysPostCode;
        }

        public String getSysArea() {
            return sysArea;
        }

        public void setSysArea(String sysArea) {
            this.sysArea = sysArea;
        }

        sysAreaCode(String sysPostCode, String sysArea,String sysAreaName) {
            this.sysPostCode = sysPostCode;
            this.sysArea = sysArea;
            this.sysAreaName = sysAreaName;
        }

    }
    public static String getSysAreaBySysPostCode(String sysPostCode) {
        for (sysAreaCode sysAreaCode : sysAreaCode.values()) {
            if (sysPostCode.equals(sysAreaCode.sysPostCode)){
                return sysAreaCode.sysArea;
            }
        }
        return null;
    }

    public static String getSysAreaNameBySysArea(String sysArea) {
        for (sysAreaCode sysAreaCode : sysAreaCode.values()) {
            if (sysArea.equals(sysAreaCode.sysArea)){
                return sysAreaCode.sysAreaName;
            }
        }
        return null;
    }

}
