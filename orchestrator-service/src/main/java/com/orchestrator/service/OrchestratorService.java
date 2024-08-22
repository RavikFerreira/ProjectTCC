package com.orchestrator.service;

import com.orchestrator.dto.Event;
import com.orchestrator.dto.History;
import com.orchestrator.enums.EEventSource;
import com.orchestrator.enums.EStatus;
import com.orchestrator.enums.ETopic;
import com.orchestrator.kafka.Producer;
import com.orchestrator.saga.SagaExecutionController;
import com.orchestrator.utils.JsonUtil;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Singleton
@AllArgsConstructor
public class OrchestratorService {

    private static final Logger LOG = LoggerFactory.getLogger(OrchestratorService.class);
    @Inject
    private JsonUtil jsonUtil;
    @Inject
    private SagaExecutionController sagaExecutionController;
    @Inject
    private Producer producer;

    private ETopic getTopic (Event event){
        return sagaExecutionController.getNextTopic(event);
    }

    private void addHistory(Event event, String message){
        History history = new History();
        history.setSource(event.getSource());
        history.setStatus(event.getStatus());
        history.setMessage(message);
        history.setCreatedAt(LocalDateTime.now());
        event.addToHistory(history);
    }

    private void sendToProducerWithTopic(Event event, ETopic topic){
        producer.sendEvent(jsonUtil.toJson(event), topic.getTopic());
    }

    public void start(Event event){
        event.setSource(EEventSource.ORCHESTRATOR); event.setStatus(EStatus.SUCCESS);
        ETopic topic = getTopic(event); LOG.info("STARTED!"); addHistory(event, "Started!"); sendToProducerWithTopic(event,topic);
    }

    public void finishSuccess(Event event){
        event.setSource(EEventSource.ORCHESTRATOR); event.setStatus(EStatus.SUCCESS); LOG.info("FINISHED SUCCESSFULLY FOR EVENT {}", event.getId());
        addHistory(event, "Finished successfully!");
    }
    public void finishFail(Event event){
        event.setSource(EEventSource.ORCHESTRATOR); event.setStatus(EStatus.FAIL);
        LOG.info("FINISHED WITH ERRORS FOR EVENT {}", event.getId());
        addHistory(event, "Finished with errors!");
    }
    public void continueSaga(Event event) {
        ETopic topic = getTopic(event);
        LOG.info("SAGA CONTINUING FOR EVENT {}", event.getId());
        sendToProducerWithTopic(event,topic);
    }
}
