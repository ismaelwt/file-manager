package com.challenge.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ShoppingCart {

    private Integer userId;
    private String name;
    private List<Order> orders = new ArrayList<>();
}
