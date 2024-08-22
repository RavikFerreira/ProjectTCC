package com.orchestrator.config;

import com.orchestrator.enums.ETopic;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;

@Factory
public class KafkaConfig {

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;
    @Value("${kafka.consumer.group-id}")
    private String groupId;
    @Value("${kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        return props;
    }
    @Singleton
    @Bean
    public KafkaConsumer<String, String> kafkaConsumer() {
        return new KafkaConsumer<>(consumerProps());
    }


    private Map<String, Object> producerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }
    @Singleton
    @Bean
    public KafkaProducer<String, Object> kafkaProducer() {
        return new KafkaProducer<>(producerProps());
    }

    private NewTopic buildTopic(String name){
        NewTopic topic = new NewTopic(name, 1, (short)1);
        return topic;
    }
    @Bean
    public NewTopic startTopic(){
        return buildTopic(ETopic.START.getTopic());
    }
    @Bean
    public NewTopic orchestratorTopic(){
        return buildTopic(ETopic.ORCHESTRATOR.getTopic());
    }
    @Bean
    public NewTopic payment_successTopic(){
        return buildTopic(ETopic.FINISH_SUCCESS.getTopic());
    }
    @Bean
    public NewTopic payment_failTopic(){
        return buildTopic(ETopic.FINISH_FAIL.getTopic());
    }
    @Bean
    public NewTopic finish_successTopic(){
        return buildTopic(ETopic.FINISH_SUCCESS.getTopic());
    }
    @Bean
    public NewTopic finish_failTopic(){
        return buildTopic(ETopic.FINISH_FAIL.getTopic());
    }



}