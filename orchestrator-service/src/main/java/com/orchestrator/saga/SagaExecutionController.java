package com.orchestrator.saga;

import com.orchestrator.dto.Event;
import com.orchestrator.enums.EEventSource;
import com.orchestrator.enums.ETopic;
import io.micronaut.http.annotation.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static com.orchestrator.saga.Handler.EVENT_SOURCE_INDEX;
import static com.orchestrator.saga.Handler.HANDLER;
import static com.orchestrator.saga.Handler.STATUS_INDEX;
import static com.orchestrator.saga.Handler.TOPIC_INDEX;
import static java.lang.String.format;

@Controller
public class SagaExecutionController {
    private static final Logger LOG = LoggerFactory.getLogger(SagaExecutionController.class);

    private boolean isEventSourceAndStatusValid(Event event, Object[] row){
        var source = row[EVENT_SOURCE_INDEX];
        var status = row[STATUS_INDEX];
        return source.equals(event.getSource()) && status.equals(event.getStatus());
    }

    public ETopic findTopicBySourceAndStatus(Event event){
        return (ETopic) Arrays.stream(HANDLER).filter(row -> isEventSourceAndStatusValid(event, row)) .map(i -> i[TOPIC_INDEX])
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Topic not found!"));
    }

    private String createSagaId(Event event){
        return format("TABLE ID: %s | EVENT ID %s", event.getPayload().getId(), event.getId());
    }

    private void logCurrentSaga(Event event, ETopic topic){
        var sagaId = createSagaId(event);
        EEventSource source = event.getSource();
        switch (event.getStatus()){
            case SUCCESS -> LOG.info("### CURRENT SAGA: {} | SUCCESS | NEXT TOPIC {} | {}" , source, topic, sagaId);
            case ROLLBACK_PENDING -> LOG.info("### CURRENT SAGA: {} | SENDING TO ROLLBACK CURRENT SERVICE | NEXT TOPIC {} | {}" , source, topic, sagaId);
            case FAIL -> LOG.info("### CURRENT SAGA: {} | SENDING TO ROLLBACK PREVIOUS SERVICE | NEXT TOPIC {} | {}" , source, topic, sagaId);
        }
    }
    public ETopic getNextTopic(Event event) {
        if (event.getSource() == null || event.getStatus() == null) {
            throw new RuntimeException("Source and status must be informed.");
        }
        var topic = findTopicBySourceAndStatus(event); logCurrentSaga(event, topic);
        return topic;
    }
}
