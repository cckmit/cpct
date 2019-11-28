package com.zjtelcom.cpct.enums;

import com.zjtelcom.cpct.util.ChannelUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: anson
 * @Date: 2018/12/25
 * @Description: c3编码和对应名称
 */
public enum OrgEnum {

    ZHEJIAGN(800000000004L, "浙江省"),
    QUZHOU(800000000040L, "衢州市"),
    HAGNZHOU(800000000037L, "杭州市"),
    HUZHOU(800000000021L, "湖州市"),
    JIAXING(800000000022L, "嘉兴市"),
    NINGBO(800000000023L, "宁波市"),
    SHAOXING(800000000024L, "绍兴市"),
    TAIZHOU(800000000041L, "台州市"),
    WENZHOU(800000000025L, "温州市"),
    LISHUI(800000000039L, "丽水市"),
    JINHUA(800000000038L, "金华市"),
    ZHOUSHAN(800000000026L, "舟山市");


    private Long orgId;
    private String name;

    OrgEnum(Long orgId, String name){
        this.orgId=orgId;
        this.name=name;

    }
    /**
     * 通过orgId编码得到名称
     * @return
     */
    public static String getNameByOrgId(){
        List<Long> stringList = new ArrayList<>();
        for (OrgEnum areaNameEnum: OrgEnum.values()){
            stringList.add(areaNameEnum.orgId);
        }
        String list = ChannelUtil.idList2String(stringList);
        return list;
    }



    /**
     * 通过orgId编码得到名称
     * @param orgId
     * @return
     */
    public static String getNameByOrgId(Long orgId){
        for (OrgEnum areaNameEnum: OrgEnum.values()){
            if(orgId.equals(areaNameEnum.orgId)){
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
        for (OrgEnum areaNameEnum: OrgEnum.values()){
             if(name.equals(areaNameEnum.name)){
                 return areaNameEnum.orgId;
             }
        }
        return null;
    }

}
