package com.serviceum.repository;

import com.serviceum.models.Event;
import io.micronaut.data.mongodb.annotation.MongoRepository;
import io.micronaut.data.repository.CrudRepository;

@MongoRepository(databaseName = "orders-db")
public interface EventRepository extends CrudRepository<Event, String> {
}
