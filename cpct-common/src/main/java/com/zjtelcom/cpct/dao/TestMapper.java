package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.Test;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description
 * @Author pengy
 * @Date 2018/6/12 15:41
 */
@Mapper
@Repository
public interface TestMapper {

    public List<Test> getList();

}
