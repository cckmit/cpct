package com.zjtelcom.cpct.dao.campaign;

import com.zjtelcom.cpct.domain.channel.MktCamResource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

//电子券二维码，提供海报信息查询
@Repository
@Mapper
public interface MktCamResourceQRCodeMapper {
    MktCamResource selectRecordByRuleId(@Param("mktCamResourceId") Long mktCamResourceId);
    void updateQRUrlbyMktResourceId(@Param("qrUrl") String qrUrl,@Param("mktCamResourceId") Long mktCamResourceId);
    void updatePostUrlbyMktResourceId(@Param("postUrl") String postUrl,@Param("mktCamResourceId") Long mktCamResourceId);
    String getPostPathByRuleId(@Param("ruleId") Long ruleId);
}
