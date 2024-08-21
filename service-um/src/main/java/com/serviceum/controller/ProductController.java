package com.serviceum.controller;


import com.serviceum.models.Product;
import com.serviceum.service.ProductService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;

import java.util.List;

@Controller("product/")
public class ProductController {

    @Inject
    private ProductService menuService;

    @Get()
    public HttpResponse<List<Product>> listProducts(){
        return HttpResponse.ok(menuService.productList());
    }

    @Post()
    public HttpResponse<Product> addProduct(@Body Product product) {
        Product products = menuService.addProduct(product);
        return HttpResponse.created(products);
    }
}
