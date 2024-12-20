package com.servicesdois.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.io.Serial;
import java.io.Serializable;

@Serdeable
public class Product implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private double price = 0.0;

    public Product(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;

    }

    public Product(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

}
