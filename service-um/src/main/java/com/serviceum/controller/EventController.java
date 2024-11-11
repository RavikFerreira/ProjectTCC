package com.serviceum.controller;

import com.serviceum.models.Event;
import com.serviceum.service.EventService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import jakarta.inject.Inject;

import java.util.List;

@Controller("event/")
public class EventController {
    @Inject
    private EventService eventService;

    @Get("/{id}")
    public Event findById(@QueryValue String id){
        return eventService.findById(id);
    }
    @Get()
    public List<Event> findAll(){
        return eventService.findAll();
    }

}
