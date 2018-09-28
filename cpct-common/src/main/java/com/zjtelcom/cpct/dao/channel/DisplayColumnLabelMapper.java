package com.zjtelcom.cpct.dao.channel;

import com.zjtelcom.cpct.domain.channel.DisplayColumnLabel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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

    DisplayColumnLabel findByDisplayIdAndLabelId(@Param("displayId")Long displayId,@Param("labelId")Long labelId);

    List<DisplayColumnLabel> selectAll();

    List<DisplayColumnLabel> findListByDisplayId(@Param("displayId")Long displayId);

    List<Long> findOldIdListByDisplayId(@Param("displayId")Long displayId);

    int updateByPrimaryKey(DisplayColumnLabel record);
}