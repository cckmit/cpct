package com.zjtelcom.cpct.dao.channel;

import com.zjtelcom.cpct.domain.channel.LabelGrpMbr;
import com.zjtelcom.cpct.domain.channel.Message;
import com.zjtelcom.cpct.domain.channel.MessageLabel;
import com.zjtelcom.cpct.dto.channel.MessageLabelDTO;
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
public interface MessageLabelMapper {
    int deleteByPrimaryKey(Long messageLabelId);

    int insert(MessageLabel record);

    MessageLabel selectByPrimaryKey(Long messageLabelId);

    List<MessageLabel> selectAll();

    int updateByPrimaryKey(MessageLabel record);

    List<MessageLabel> qureyMessageLabel(Message message);

    List<MessageLabel> qureyMessageLabelByMessageId(@Param("messageId")Long messageId);

    List<MessageLabel> findListBylabelId(@Param("labelId")Long labelId);

}