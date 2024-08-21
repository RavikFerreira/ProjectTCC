package com.serviceum.repository;

import com.serviceum.models.Product;
import io.micronaut.data.mongodb.annotation.MongoRepository;
import io.micronaut.data.repository.CrudRepository;

@MongoRepository(databaseName = "orders-db")
public interface ProductRepository extends CrudRepository<Product, String> {

}
