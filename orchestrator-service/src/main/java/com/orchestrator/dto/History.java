package com.orchestrator.dto;

import com.orchestrator.enums.EEventSource;
import com.orchestrator.enums.EStatus;
import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDateTime;

@Serdeable
public class History {

    private EEventSource source;
    private EStatus status;
    private String message;
    private LocalDateTime createdAt;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
