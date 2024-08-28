package com.servicesdois.models;

import com.servicesdois.enums.EPaymentStatus;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.event.PrePersist;
import io.micronaut.data.annotation.event.PreUpdate;
import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@MappedEntity
@Serdeable
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    private String id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private double totalAmount;
    private EPaymentStatus status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public EPaymentStatus getStatus() {
        return status;
    }

    public void setStatus(EPaymentStatus status) {
        this.status = status;
    }
    @PrePersist
    public void prePersist(){
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        status = EPaymentStatus.PENDING;
    }
    @PreUpdate
    public void preUpdate(){
        updatedAt = LocalDateTime.now();
    }


}
