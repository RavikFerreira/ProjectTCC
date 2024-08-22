package com.serviceum.service;

import com.serviceum.kafka.Producer;
import com.serviceum.models.Event;
import com.serviceum.models.Order;
import com.serviceum.repository.OrderRepository;
import com.serviceum.utils.JsonUtil;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.time.LocalDateTime;
import java.util.List;


@Singleton
public class OrderService {

    @Inject
    private OrderRepository orderRepository;
    @Inject
    private EventService eventService;
    @Inject
    private Producer producer;
    @Inject
    private JsonUtil jsonUtil;


    public List<Order> list(){
        return orderRepository.findAll();
    }

    public Order addOrder(Order order){
        Order orders = orderRepository.save(order);
        producer.sendEvent(jsonUtil.toJson(createPayload(orders)));
        return orders;

    }
    private Event createPayload(Order order){
        Event event = new Event();
        event.setId(order.getId());
        event.setPayload(order);
        event.setCreatedAt(LocalDateTime.now());
        eventService.save(event);
        return event;
    }


}
