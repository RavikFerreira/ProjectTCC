package com.orchestrator.saga;


import static com.orchestrator.enums.EEventSource.ORCHESTRATOR;
import static com.orchestrator.enums.EEventSource.PAYMENT_SERVICE;
import static com.orchestrator.enums.EStatus.FAIL;
import static com.orchestrator.enums.EStatus.ROLLBACK_PENDING;
import static com.orchestrator.enums.EStatus.SUCCESS;
import static com.orchestrator.enums.ETopic.FINISH_FAIL;
import static com.orchestrator.enums.ETopic.FINISH_SUCCESS;
import static com.orchestrator.enums.ETopic.PAYMENT_FAIL;
import static com.orchestrator.enums.ETopic.PAYMENT_SUCCESS;

public class Handler {

    private Handler(){}

    public static final Object[][] HANDLER = {
            {ORCHESTRATOR, SUCCESS, PAYMENT_SUCCESS},
            {ORCHESTRATOR, FAIL, FINISH_FAIL},
            {PAYMENT_SERVICE, ROLLBACK_PENDING, PAYMENT_FAIL},
            {PAYMENT_SERVICE, FAIL, FINISH_FAIL},
            {PAYMENT_SERVICE, SUCCESS, FINISH_SUCCESS}
    };

    public static final int EVENT_SOURCE_INDEX = 0;
    public static final int STATUS_INDEX = 1;
    public static final int TOPIC_INDEX = 2;

}
