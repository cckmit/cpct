package com.zjtelcom.cpct.dao.channel;



import com.zjtelcom.cpct.domain.channel.ContactChlAbl;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface ContactChlAblMapper {
    int deleteByPrimaryKey(Long contacChlAblId);

    int insert(ContactChlAbl record);

    ContactChlAbl selectByPrimaryKey(Long contacChlAblId);

    List<ContactChlAbl> selectAll();

    int updateByPrimaryKey(ContactChlAbl record);
}