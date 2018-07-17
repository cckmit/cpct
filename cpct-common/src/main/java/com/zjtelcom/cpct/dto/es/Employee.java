package com.zjtelcom.cpct.dto.es;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;


/**
 * @Description
 * @Author pengy
 * @Date 2018/7/10 12:26
 */

@Document(indexName = "houyunfeng", shards = 1, replicas = 0, refreshInterval = "-1")
public class Employee {

    @Id
    private String id;

    private String firstName;

    private String lastName;

    private Integer age = 0;

    private String about;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

}