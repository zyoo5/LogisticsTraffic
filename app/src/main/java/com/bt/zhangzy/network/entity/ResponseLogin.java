package com.bt.zhangzy.network.entity;

import java.util.List;

/**
 * Created by ZhangZy on 2016-1-23.
 */
public class ResponseLogin extends BaseEntity {
    public ResponseLogin() {
    }

    JsonUser user;
    JsonDriver driver;
    JsonCompany company;
    JsonEnterprise enterprise;
    JsonMember member;
    ResponseFavorites favorites;
    List<JsonMotorcades> motorcades;


    public JsonMember getMember() {
        return member;
    }

    public void setMember(JsonMember member) {
        this.member = member;
    }

    public List<JsonMotorcades> getMotorcades() {
        return motorcades;
    }

    public void setMotorcades(List<JsonMotorcades> motorcades) {
        this.motorcades = motorcades;
    }

    public ResponseFavorites getFavorites() {
        return favorites;
    }

    public void setFavorites(ResponseFavorites favorites) {
        this.favorites = favorites;
    }

    public JsonUser getUser() {
        return user;
    }

    public void setUser(JsonUser user) {
        this.user = user;
    }

    public JsonDriver getDriver() {
        return driver;
    }

    public void setDriver(JsonDriver driver) {
        this.driver = driver;
    }

    public JsonCompany getCompany() {
        return company;
    }

    public void setCompany(JsonCompany company) {
        this.company = company;
    }

    public JsonEnterprise getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(JsonEnterprise enterprise) {
        this.enterprise = enterprise;
    }
}
