package com.zjtelcom.cpct.dao.es;

import com.zjtelcom.cpct.dto.es.AccountInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

@Component("elasticAccountInfoRepository")
public interface AccountRepository extends ElasticsearchRepository<AccountInfo,Long> {

    AccountInfo findByAccountName(String accountName);



}
