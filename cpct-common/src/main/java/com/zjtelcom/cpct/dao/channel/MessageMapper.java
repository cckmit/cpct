package com.zjtelcom.cpct.dao.channel;

import com.zjtelcom.cpct.domain.channel.Message;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 信息持久层
 *
 * @author pengyu
 */
@Mapper
@Repository
public interface MessageMapper {
    int deleteByPrimaryKey(Long messageId);

    int insert(Message record);

    Message selectByPrimaryKey(Long messageId);

    List<Message> selectAll();

    int updateByPrimaryKey(Message record);
}