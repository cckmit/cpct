package com.zjtelcom.cpct.domain;

import java.io.Serializable;

/**
 * @Description
 * @Author pengy
 * @Date 2018/6/12 15:43
 */
public class Test implements Serializable {

    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Test{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
