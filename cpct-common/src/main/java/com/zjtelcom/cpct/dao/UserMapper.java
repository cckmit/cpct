package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author:HuangHua
 * @Descirption:
 * @Date: Created by huanghua on 2018/5/8.
 * @Modified By:
 */
@Mapper
@Repository
public interface UserMapper {

    /**查询操作**/
    public List<User> query(User user);

    /**
     * 根据用户名查询用户信息
     * @param userName 用户名称
     * @return 用户信息
     * @author nhf
     * @date 2018年5月21日
     */
    public User queryUserByName(String userName);

    /**
     * 查询用户列表
     * @return 用户列表
     * @author nhf
     * @date 2018年5月21日
     */
    public List<User> userList();

}
