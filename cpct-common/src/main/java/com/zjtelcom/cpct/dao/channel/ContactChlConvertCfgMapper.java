package com.zjtelcom.cpct.dao.channel;



import com.zjtelcom.cpct.domain.channel.ContactChlConvertCfg;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface ContactChlConvertCfgMapper {
    int deleteByPrimaryKey(Long contactChlConvertCfgId);

    int insert(ContactChlConvertCfg record);

    ContactChlConvertCfg selectByPrimaryKey(Long contactChlConvertCfgId);

    List<ContactChlConvertCfg> selectAll();

    int updateByPrimaryKey(ContactChlConvertCfg record);
}