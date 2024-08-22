package com.orchestrator.dto;

import com.orchestrator.enums.EEventSource;
import com.orchestrator.enums.EStatus;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static io.micronaut.core.util.CollectionUtils.isEmpty;

@Serdeable
public class Event {

    private String id;
    private Order payload;
    @Enumerated(EnumType.STRING)
    private EEventSource source;
    @Enumerated(EnumType.STRING)
    private EStatus status;
    private List<History> eventHistory;
    private LocalDateTime createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Order getPayload() {
        return payload;
    }

    public void setPayload(Order payload) {
        this.payload = payload;
    }

    public EEventSource getSource() {
        return source;
    }

    public void setSource(EEventSource source) {
        this.source = source;
    }

    public EStatus getStatus() {
        return status;
    }

    public void setStatus(EStatus status) {
        this.status = status;
    }

    public List<History> getEventHistory() {
        return eventHistory;
    }

    public void setEventHistory(List<History> eventHistory) {
        this.eventHistory = eventHistory;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void addToHistory(History history){
        if(isEmpty(eventHistory)){
            eventHistory = new ArrayList<>();
        }
        eventHistory.add(history);
    }

}
