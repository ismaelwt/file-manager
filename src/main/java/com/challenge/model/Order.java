package com.challenge.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Order {

    private Integer orderId;
    private Double total;
    private Integer date;
    private List<Product> products = new ArrayList<>();


    public Order(Integer orderId, Double total, Integer date, List<Product> products) {
        this.orderId = orderId;
        this.total = total;
        this.date = date;
        this.products = products;
    }

    public void setTotal(Double total) {
        this.total = format(total);
    }

    private Double format(Double number) {
        return (double) Math.round(number * 100) / 100;
    }
}
