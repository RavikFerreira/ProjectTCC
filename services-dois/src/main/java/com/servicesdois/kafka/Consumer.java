package com.servicesdois.kafka;

import com.servicesdois.dto.Event;
import com.servicesdois.service.PaymentService;
import com.servicesdois.utils.JsonUtil;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@KafkaListener(groupId = "${kafka.consumer.group-id}")
public class Consumer {

    private static final Logger LOG = LoggerFactory.getLogger(Consumer.class);

    @Inject
    private PaymentService paymentService;
    @Inject
    private JsonUtil jsonUtil;

    @Topic("${kafka.topic.payment-success}")
    public void consumerPaymentSuccessEvent(String payload){
        LOG.info("Receiving success event {} from payment-success topic" , payload);
        Event event = jsonUtil.toEvent(payload);
        paymentService.realizedPayment(event);
    }
    @Topic("${kafka.topic.payment-fail}")
    public void consumerPaymentFailEvent(String payload){
        LOG.info("Receiving rollback event {} from payment-fail topic" , payload);
        Event event = jsonUtil.toEvent(payload);
        paymentService.realizedRefund(event);
    }

}
