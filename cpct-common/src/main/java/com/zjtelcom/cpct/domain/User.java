package com.zjtelcom.cpct.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author:HuangHua
 * @Descirption:用户实体类
 * @Date: Created by huanghua on 2018/5/8.
 * @Modified By:
 */
public class User implements Serializable{

    /**
     * 序列号
     */
    private static final long serialVersionUID = 1L;

    /**id,主键**/
    private int id;

    /**用户登陆名**/
    private String loginName;

    /**密码**/
    private String passWord;

    /**email**/
    private String email;

    /**联系电话**/
    private String phone;

    /**创建时间**/
    private Date createDate;

    /**更新时间**/
    private Date updateDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString(){
        return id + "  " + loginName + " " + email;
    }
}
