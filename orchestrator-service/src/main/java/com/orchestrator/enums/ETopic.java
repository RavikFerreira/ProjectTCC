package com.orchestrator.enums;

public enum ETopic {

    START("start"),
    ORCHESTRATOR("orchestrator"),
    PAYMENT_SUCCESS("payment-success"),
    PAYMENT_FAIL("payment-fail"),
    FINISH_SUCCESS("finish-success"),
    FINISH_FAIL("finish-fail");

    private final String topic;

    ETopic(String topic) {
        this.topic = topic;
    }
    public String getTopic() {
        return topic;
    }

}
