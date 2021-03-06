package com.bt.zhangzy.network.entity;

import com.zhangzy.base.http.BaseEntity;

import java.util.List;

/**
 * Created by ZhangZy on 2016-2-19.
 */
public class RequestOrderAllocation extends BaseEntity {

    int orderId;
    List<JsonOrderHistory> orderHistory;


    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public List<JsonOrderHistory> getOrderHistory() {
        return orderHistory;
    }

    public void setOrderHistory(List<JsonOrderHistory> orderHistory) {
        this.orderHistory = orderHistory;
    }
}
