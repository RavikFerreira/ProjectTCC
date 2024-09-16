package com.servicesdois.repository;

import com.servicesdois.models.Payment;
import io.micronaut.data.mongodb.annotation.MongoRepository;
import io.micronaut.data.repository.CrudRepository;

@MongoRepository(databaseName = "orders-db")
public interface PaymentRepository extends CrudRepository<Payment, String> {
}
