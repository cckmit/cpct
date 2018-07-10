package com.zjtelcom.cpct.dao.es;

import com.zjtelcom.cpct.dto.es.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @Description
 * @Author pengy
 * @Date 2018/7/10 12:27
 */
@Mapper
@Repository
public interface EmployeeRepository extends ElasticsearchRepository<Employee,String> {

    Employee queryEmployeeById(String id);

}
