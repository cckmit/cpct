package com.zjtelcom.cpct.dao.channel;

import com.zjtelcom.cpct.domain.channel.DisplayColumnLabel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 展示列关联标签持久层
 *
 * @author pengyu
 */
@Mapper
@Repository
public interface DisplayColumnLabelMapper {
    int deleteByPrimaryKey(Long displayColumnLabelId);

    int insert(DisplayColumnLabel record);

    DisplayColumnLabel selectByPrimaryKey(Long displayColumnLabelId);

    List<DisplayColumnLabel> selectAll();

    int updateByPrimaryKey(DisplayColumnLabel record);
}