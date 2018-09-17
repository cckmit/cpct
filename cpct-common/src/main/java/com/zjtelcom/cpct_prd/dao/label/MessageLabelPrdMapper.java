package com.zjtelcom.cpct_prd.dao.label;

import com.zjtelcom.cpct.domain.channel.Message;
import com.zjtelcom.cpct.domain.channel.MessageLabel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 信息标签持久层
 *
 * @author pengyu
 */
@Mapper
@Repository
public interface MessageLabelPrdMapper {
    int deleteByPrimaryKey(Long messageLabelId);

    int insert(MessageLabel record);

    MessageLabel selectByPrimaryKey(Long messageLabelId);

    List<MessageLabel> selectAll();

    int updateByPrimaryKey(MessageLabel record);

    List<MessageLabel> qureyMessageLabel(Message message);

    List<MessageLabel> qureyMessageLabelByMessageId(@Param("messageId") Long messageId);

}