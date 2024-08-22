package com.orchestrator.kafka;

import jakarta.inject.Inject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Producer {
    private static final Logger LOG = LoggerFactory.getLogger(Producer.class);

    @Inject
    private KafkaProducer kafkaProducer;

    public void sendEvent(String payload, String topic){
        try {
            LOG.info("Sending event to topic {} with data {}", topic, payload);
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, payload); kafkaProducer.send(record);
        } catch (Exception e) {
            LOG.error("Error trying to send data to topic {} with data {}", topic, payload, e);
        }
    }
}
