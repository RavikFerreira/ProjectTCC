package com.orchestrator.kafka;

import com.orchestrator.dto.Event;
import com.orchestrator.service.OrchestratorService;
import com.orchestrator.utils.JsonUtil;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@KafkaListener(groupId = "${kafka.consumer.group-id}")
public class Consumer {
    private static final Logger LOG = LoggerFactory.getLogger(Consumer.class);

    @Inject
    private OrchestratorService orchestratorService;
    @Inject
    private JsonUtil jsonUtil;

    @Topic("${kafka.topic.start}")
    public void consumerStartEvent(String payload){
        LOG.info("Receiving event {} from start topic" , payload); Event event = jsonUtil.toEvent(payload); orchestratorService.start(event);
    }
    @Topic("${kafka.topic.orchestrator}")
    public void consumerOrchestratorEvent(String payload){
        LOG.info("Receiving event {} from orchestrator topic" , payload); Event event = jsonUtil.toEvent(payload); orchestratorService.continueSaga(event);
    }

    @Topic("${kafka.topic.finish-success}")
    public void consumerFinishSuccessEvent(String payload){
        LOG.info("Receiving event {} from finish-success topic" , payload); Event event = jsonUtil.toEvent(payload); orchestratorService.finishSuccess(event);
    }

    @Topic("${kafka.topic.finish-fail}")
    public void consumerFinishFailEvent(String payload){
        LOG.info("Receiving ending notification event {} from finish-fail topic" , payload);
        Event event = jsonUtil.toEvent(payload); orchestratorService.finishFail(event);
    }

}
