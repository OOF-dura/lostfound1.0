package com.bmob.lostfound.bean;

import cn.bmob.v3.BmobUser;

/**
 * Created by frankdura on 2017/8/2.
 */

public class User extends BmobUser{
    private String userid;
    private String name;
    private boolean isStudent;

    public boolean isStudent() {
        return isStudent;
    }
    public void setStudent(boolean student) {
        isStudent = student;
    }
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;
}
