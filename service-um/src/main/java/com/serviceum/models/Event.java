package com.serviceum.models;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Serdeable
@MappedEntity
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue
    private String id;
    private Order payload;
    private String source;
    private String status;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

}
