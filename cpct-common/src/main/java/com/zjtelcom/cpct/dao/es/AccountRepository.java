package com.zjtelcom.cpct.dao.es;

import com.zjtelcom.cpct.dto.es.AccountInfo;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("elasticAccountInfoRepository")
public interface AccountRepository extends ElasticsearchRepository<AccountInfo,Long> {

    AccountInfo findByAccountName(String accountName);

    List<AccountInfo> findAllByAccountName(String accountName);

    Page<AccountInfo> findByAccountName(String accountName, Pageable page);

    Page<AccountInfo> findByAccountNameLike(String accountName, Pageable page);

    Page<AccountInfo> findByAccountNameNot(String accountName, Pageable page);


    @Query("{\"bool\" : {\"must\" : {\"term\" : {\"nickName\" : \"?0\"}}}}")
    Page<AccountInfo> findByNickName(String nickName, Pageable pageable);

//    @Query("{\"bool\" : {\"must\" : {\"term\" : {\"nickName\" : \"?0\"}}}}")
//    Page<AccountInfo> findByNickName(String nickName, Pageable pageable);





}
