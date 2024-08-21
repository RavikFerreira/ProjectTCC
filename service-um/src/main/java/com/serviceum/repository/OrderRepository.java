package com.serviceum.repository;

import com.serviceum.models.Order;
import io.micronaut.data.mongodb.annotation.MongoRepository;
import io.micronaut.data.repository.CrudRepository;

@MongoRepository(databaseName = "orders-db")
public interface OrderRepository extends CrudRepository<Order, String> {
}
