package com.zjtelcom.cpct.dao.event;

import com.zjtelcom.cpct.domain.event.EventItemDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.util.List;

@Mapper
@Repository
public interface EventItemMapper {

    int deleteByPrimaryKey(Long evtItemId);

    int insert(EventItemDO record);

    EventItemDO selectByPrimaryKey(Long evtItemId);

    List<EventItemDO> selectAll();

    int updateByPrimaryKey(EventItemDO record);

    int saveEventItem(EventItemDO record);
}