package com.zjtelcom.cpct.dao.blacklist;

import com.zjtelcom.cpct.domain.blacklist.BlackListDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface BlackListMapper {

    int addBlackList(BlackListDO blackListDO);

    int updateBlackList(BlackListDO blackListDO);

    int deleteBlackListById(List<String> phoneNumsDeleted);

    List<BlackListDO> getBlackListById(List<String> phoneNums);

    List<BlackListDO> getAllBlackList();

    int insertBatch(List<BlackListDO> blackListDOS);

}

