package com.zjtelcom.cpct.dto.es;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;


@Document(indexName = "houyunfeng",type = "accountinfo",shards = 1,replicas = 0,refreshInterval = "-1")
public class AccountInfo {

    @Id
    private Long id;

    @Field
    private String accountName;

    @Field
    private String nickName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
