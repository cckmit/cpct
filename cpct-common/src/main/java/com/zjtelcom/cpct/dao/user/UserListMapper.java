package com.zjtelcom.cpct.dao.user;

import com.zjtelcom.cpct.dto.user.UserList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Mapper
@Repository
public interface UserListMapper {
    int deleteByPrimaryKey(Long userId);

    int insert(UserList record);

    UserList selectByPrimaryKey(Long userId);

    List<UserList> selectAll();

    int updateByPrimaryKey(UserList record);

    UserList getUserList(UserList userList);

    int checkRule(@Param("userPhone") String userPhone,@Param("ruleId") Long ruleId,@Param("ruleType") String ruleType);

}