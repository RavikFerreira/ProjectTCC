package com.serviceum.service;

import com.serviceum.models.Event;
import com.serviceum.repository.EventRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class EventService {
    @Inject
    private EventRepository eventRepository;

    public List<Event> findAll(){
        return eventRepository.findAll();
    }

    public Event findById(String id){
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found by ID."));
    }
    public Event save(Event event){
        return eventRepository.save(event);
    }
}
