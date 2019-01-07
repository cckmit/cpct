package com.zjtelcom.cpct.enums;

/**
 * @Auther: anson
 * @Date: 2018/12/25
 * @Description: c3编码和对应名称
 */
public enum AreaNameEnum {

    ZHEJIAGN(1L, "浙江省"),
    QUZHOU(570L, "衢州市"),
    HAGNZHOU(571L, "杭州市"),
    HUZHOU(572L, "湖州市"),
    JIAXING(573L, "嘉兴市"),
    NINGBO(574L, "宁波市"),
    SHAOXING(575L, "绍兴市"),
    TAIZHOU(576L, "台州市"),
    WENZHOU(577L, "温州市"),
    LISHUI(578L, "丽水市"),
    JINHUA(579L, "金华市"),
    ZHOUSHAN(580L, "舟山市");


    private Long lanId;
    private String name;

    AreaNameEnum(Long lanId,String name){
        this.lanId=lanId;
        this.name=name;

    }

    /**
     * 通过c3编码得到名称
     * @param LandId
     * @return
     */
    public static String getNameByLandId(Long LandId){
        for (AreaNameEnum areaNameEnum:AreaNameEnum.values()){
            if(LandId==areaNameEnum.lanId){
                return areaNameEnum.name;
            }
        }
          return "";
    }


    /**
     * 通过C3名称获取C3编码
     * @param name
     * @return
     */
    public static Long getLanIdByName(String name){
        for (AreaNameEnum areaNameEnum:AreaNameEnum.values()){
             if(name.equals(areaNameEnum.name)){
                 return areaNameEnum.lanId;
             }
        }
        return null;
    }

}
