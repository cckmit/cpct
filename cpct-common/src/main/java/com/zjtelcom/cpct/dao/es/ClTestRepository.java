package com.zjtelcom.cpct.dao.es;

import com.zjtelcom.cpct.dto.es.Employee;
import com.zjtelcom.cpct.service.impl.api.ClTest;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public interface ClTestRepository extends ElasticsearchRepository<ClTest,Long> {

}
