package com.zjtelcom.cpct.dao.system;

import com.zjtelcom.cpct.domain.system.MsgTemplateDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface MsgTemplateMapper {
    int insertMsgTemplate(MsgTemplateDO msgTemplateDO);
    int updateMsgTemplate(MsgTemplateDO msgTemplateDO);
    int delMsgTemplate(int msgId);
    MsgTemplateDO selectTemplateById(int id);
    List<MsgTemplateDO> selectAllMsgTemplate();
    List<MsgTemplateDO> selectPageMsgTemplate(@Param("type") String type, @Param("msgType") String msgType, @Param("content") String content);
}
