package com.zjtelcom.cpct.service;

import com.zjtelcom.cpct.domain.User;
import java.util.List;
import java.util.Map;

/**
 * @Author:HuangHua
 * @Descirption: 用户服务接口
 * @Date: Created by huanghua on 2018/6/5.
 * @Modified By:
 */
public interface UserService {
    /**用户查询接口**/
    public List<User> query(User user);

    /**根据用户名查询用户**/
    public Map<String, Object> queryUserByName(String userName);
}
