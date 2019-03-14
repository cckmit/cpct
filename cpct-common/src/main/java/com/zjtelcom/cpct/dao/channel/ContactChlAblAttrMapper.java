package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.ContactChlAblAttr;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ContactChlAblAttrMapper {
    int deleteByPrimaryKey(Long contactChlAblAttrId);

    int insert(ContactChlAblAttr record);

    ContactChlAblAttr selectByPrimaryKey(Long contactChlAblAttrId);

    List<ContactChlAblAttr> selectAll();

    int updateByPrimaryKey(ContactChlAblAttr record);
}