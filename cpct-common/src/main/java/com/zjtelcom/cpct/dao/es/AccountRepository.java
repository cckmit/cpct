package com.zjtelcom.cpct.dao.es;

import com.zjtelcom.cpct.dto.es.AccountInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("elasticAccountInfoRepository")
public interface AccountRepository extends ElasticsearchRepository<AccountInfo,Long> {

    AccountInfo findByAccountName(String accountName);

    List<AccountInfo> findAllByAccountName(String accountName);



}
