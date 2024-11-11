package com.orchestrator.dto;


import io.micronaut.serde.annotation.Serdeable;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Serdeable
public class Order implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private List<Product> products;

    public Order(String id, List<Product> products) {
        this.id = id;
        this.products = products;
    }
    public Order() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

}
