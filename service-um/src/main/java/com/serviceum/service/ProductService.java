package com.serviceum.service;

import com.serviceum.models.Product;
import com.serviceum.repository.ProductRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class ProductService {

    @Inject
    private ProductRepository productRepository;

    public List<Product> productList(){
        return productRepository.findAll();
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

}
